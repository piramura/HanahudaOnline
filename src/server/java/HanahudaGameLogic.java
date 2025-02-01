import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;
public class HanahudaGameLogic {
    // **スレッドプールを作成**
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Game game;
    private int playCount = 0; // 現在のターンでのプレイ回数
    private Random random = new Random(); // **ここで1つだけ作成**
    public Game getGame(){
        return game;
    }

    public void resetGame() {
        Deck deck = new Deck();
        this.game = new Game(deck);
        game.initializeGame(this);
        game.setCurrentPlayerIndex(1);
        System.out.println("ゲーム開始！ 配札完了！");
    }
    
    

    //PLAY_CARDで呼ばれる関数。
    public String processPlayerAction(int playerIndex, int handCardId, int fieldCardId) {
        System.out.println("DEBUG: processPlayerAction() を実行: Player " + playerIndex + 
                       " | handCardId: " + handCardId + " | fieldCardId: " + fieldCardId);
        if (playerIndex != game.getCurrentPlayerIndex()) {
            System.out.println("playerindex"+playerIndex+"!= game.getCurrentPlayerIndex()"+game.getCurrentPlayerIndex());
            return "ERROR: 現在のプレイヤーではありません。";
        }
        //System.out.println("HanahudaLogicの中でprocessPlayerActionを処理中");
        String playResult = handleCardPlay(playerIndex, handCardId, fieldCardId);//内部処理は委託する。
        game.incrementPlayCount();
        System.out.println("DEBUG: handleCardPlay() の結果: " + playResult);

        if (playResult == null || playResult.isEmpty()) {
            System.err.println("ERROR: playResult が null または空のため、不正な状態");
            return "ERROR: サーバーで処理中に問題が発生しました。";
        }
        System.out.println("DEBUG: game.getPlayCount()=="+game.getPlayCount());
        
        if (game.getPlayCount() >= 2) {
            int tmpPlayerId = (playerIndex == 2) ? 1 : 0; // 相手プレイヤーのIDを取得
            Player currentPlayer = game.getPlayers().get(tmpPlayerId);

            // **前回の役を取得**
            RoleResult previousRoleResult = currentPlayer.getRoleResult();
            System.out.println("DEBUG: 現在の役の成立結果: " + previousRoleResult);
            // **現在の得点を計算**
            RoleResult newRoleResult = RoleChecker.checkRules(currentPlayer);
            currentPlayer.setRoleResult(newRoleResult); // プレイヤーに役情報を保存

            System.out.println("DEBUG: 現在の役の成立結果: " + newRoleResult);
            // **こいこい選択判定**
            if (!newRoleResult.getAchievedRoles().isEmpty() && 
            !previousRoleResult.getAchievedRoles().containsAll(newRoleResult.getAchievedRoles())
            ) {
                currentPlayer.setKoiKoi(true); // こいこい状態を更新
                return "KOIKOI_WAITING: 役が成立しました。「こいこい」を選択してください。\n" +
                   "役: " + newRoleResult.getAchievedRoles() + "\n" +
                   "得点: " + newRoleResult.getTotalScore();
            } else {
                currentPlayer.setKoiKoi(false); // こいこいを解除
                if (game.getIsEnd()) {
                    return "GAME_END: 相手の勝利。ターン " + game.getCurrentTurn() + " です。\n" + playResult;
                } else {
                    return "NEXT_TURN: ターン " + game.getCurrentTurn() + " です。\n" + playResult;
                }
            }
            

        }
        System.out.println("CONTINUE_TURN: プレイヤー " + playerIndex + " のターンが継続中です。\n" + playResult);
        return "CONTINUE_TURN: プレイヤー " + playerIndex + " のターンが継続中です。\n" + playResult;
    }
    //processPlayerActionの補助関数
    public String handleCardPlay(int playerIndex, int cardId,int fieldcard) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        System.out.println("HanahudaLogicの中でhandleCardPlayを処理中");
        // // デッキの中身を出力
        // game.printDeckContents();
        // プレイヤーの手札を確認
        //game.printPlayerHands();
        Player player = game.getPlayers().get(playerIndex-1);//ここでー１をして0と1に直す
        int handIndex = findCardIndexInHand(player, cardId);
        Card playedCard = player.playCard(handIndex);
        if(fieldcard == -1){
            game.getField().addCard(playedCard);
            System.out.println("ばにカードをおきます");
        }else{
            Field field = game.getField();
            if (!field.isThreeCards(playedCard)) {
                Card fieldCard = game.getField().takeCardById(fieldcard);
                System.out.println("三枚ないとき");
                player.captureCard(fieldCard);
                player.captureCard(playedCard);
            } else {
                List<Card> takenCards = field.takeCardsByMonth(playedCard.getMonth());
                System.out.println("DEBUG: 三枚のカードある場合"+takenCards);
                for (Card card : takenCards) {
                    player.captureCard(card);
                }
                player.captureCard(playedCard);
            }
        }
        if (game.getPlayCount() == 0) {
            Card drawnCard = game.getDeck().draw();
            player.addCardToHand(drawnCard);
            System.out.println("DEBUG: 1回目のプレイ");

            return "山から引いたカードを手札に追加したのでそれを元に場のカード選べ";
        } else if(game.getPlayCount() == 1) {
            System.out.println("DEBUG: 2回目のプレイ - 山札からカードは引かない");
 
            return "相手のターンへ";
        }else{
            System.out.println("DEBUG: 3回目のプレイ - EROOR");
            return "3回目のプレイ - EROOR";
        }
    }
    private int findCardIndexInHand(Player player, int cardId) {
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            System.out.println("DEBUG: hand.get(i).getId() " + hand.get(i).getId() + " と " + cardId);
            if (hand.get(i).getId() == cardId) {
                return i;
            }
        }
        throw new IllegalArgumentException("指定されたカードIDが手札にありません: " + cardId);
    }

    public String determineWinner() {
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
    
        // **Player クラスに保存された最新の RoleResult を使う**
        RoleResult result1 = player1.getRoleResult();
        RoleResult result2 = player2.getRoleResult();
    
        System.out.println(result1);
        System.out.println(result2);
    
        int score1 = result1.getTotalScore();
        int score2 = result2.getTotalScore();
    
        String result = "ゲーム終了！\n";
        result += "プレイヤー 1 の得点: " + score1 + "\n";
        result += "プレイヤー 2 の得点: " + score2 + "\n";
    
        if (score1 > score2) {
            result += "プレイヤー 1 の勝利！";
        } else if (score2 > score1) {
            result += "プレイヤー 2 の勝利！";
        } else {
            result += "引き分け！";
        }
    
        return result;
    }
    
    
    //これらはパブリックで管理される。
    public void nextTurn() {
        System.out.println("DEBUG:  HanahudaGameLogicでnextTurn()が呼ばれました");
        System.out.println("game.getIsEnd()=="+game.getIsEnd());
        if (game.getIsEnd()) {
            System.out.println("GAME_END: ゲーム終了");
            return;
        }
        game.nextTurn();
        int currentPlayerId = game.getCurrentPlayerIndex();
        if (isBotTurn(currentPlayerId)) {
            playBotTurn(currentPlayerId);
        }
    }
    // **BOTのターンかどうかを判定**
    private boolean isBotTurn(int playerId) {
        return playerId == 2;//2はコンピュータのターン
    }

    // **BOTがプレイする処理**
    private void playBotTurn(int botPlayerId) {
        System.out.println("BOTのターンです。");

        // **プレイヤーの手札を取得**
        Player player = game.getPlayers().get(botPlayerId - 1); // 0-based indexに変換
        Field field = game.getField();
        if (player.getHand().isEmpty()) {
            System.out.println("BOTの手札が空のため、ターンをスキップします。");
            nextTurn();
            return;
        }

        List<Card> hand = player.getHand();

        // **1回目のアクション (1枚目のカードをプレイ)**
        scheduler.schedule(() -> {
            if (!player.getHand().isEmpty()) {
                int randomIndex = random.nextInt(hand.size()); // **手札の中からランダムなインデックス**
                int handCardId = hand.get(randomIndex).getId(); // **カードIDを取得**

                int fieldCardId = findMatchingCardInField(field, handCardId);
                System.out.println(handleCardPlay(botPlayerId, handCardId, fieldCardId));
                game.incrementPlayCount();
            } else {
                System.out.println("BOTの手札が空で1枚目のプレイをスキップします。");
            }

            // **場の更新を待ってから2回目のアクションを実行**
            waitForFieldUpdate(() -> {
                scheduler.schedule(() -> {
                    if (!player.getHand().isEmpty()) {
                        int handCardId = hand.get(hand.size() - 1).getId(); // **最後のカードを取得**
                        int fieldCardId = findMatchingCardInField(field, handCardId);
                        System.out.println(handleCardPlay(botPlayerId, handCardId, fieldCardId));
                    } else {
                        System.out.println("BOTの手札が空で2回目のプレイをスキップします。");
                    }

                    // **次のターンへ**
                    scheduler.schedule(() -> {
                        nextTurn();
                    }, 2500, TimeUnit.MILLISECONDS); // **2.5秒後に次のターンへ**

                }, 2500, TimeUnit.MILLISECONDS); // **1回目のプレイの後、場の更新後に実行**
            });

        }, 2500, TimeUnit.MILLISECONDS); // **最初のアクションを2.5秒後に実行**
    }

    /**
     * **場の更新を待つメソッド**
     * @param callback 場の更新後に実行する処理
     */
    private void waitForFieldUpdate(Runnable callback) {
        scheduler.schedule(() -> {
            System.out.println("場の更新完了。次の処理を実行します。");
            callback.run();
        }, 500, TimeUnit.MILLISECONDS); // **0.5秒待機してからコールバックを実行**
    }

    // **場にあるカードと一致するカードを探す**
    private int findMatchingCardInField(Field field, int cardId) {
        List<Card> fieldCards = field.getCards();
        for (Card card : fieldCards) {
            if (card.getMonth().ordinal()  == cardId / 4 ) {
                return card.getId();
            }
        }
        return -1; // 場に一致するカードがない場合は -1（そのまま場に出す）
    }
    public void endGame(){
        //ゲーム終了処理
        game.setIsEnd(true);
        System.out.println("GAME_END: endGameだけど");
        //リザルトを返してゲームリセット
    }
    
    public boolean isGameFinished() {
        //System.out.println("game.isBothHandEmpty()="+game.isBothHandEmpty()+" game.getIsEnd()="+game.getIsEnd());
        return game.getIsEnd();
    }
    
    
}
