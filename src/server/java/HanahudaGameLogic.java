import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;
public class HanahudaGameLogic {
    //スレッドプールを作成
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Game game;
    private int playCount = 0; // 現在のターンでのプレイ回数
    private Random random = new Random(); // ここで1つだけ作成
    private boolean botMode;
    private boolean isKoiKoiWaiting;
    private int lastScoringPlayer = -1; // 最後に役を成立させたプレイヤーのID
    private RoleResult previousRoleResult;
    private boolean botFinish = false;
    private String disconnectMessage = ""; // 切断通知を保存

    public void setDisconnectMessage(String message) {
        this.disconnectMessage = message;
    }

    public String getDisconnectMessage() {
        return disconnectMessage;
    }

    public Game getGame(){return game;}//
    public void setBotMode(boolean botMode){this.botMode = botMode;}
    public boolean getBouMode(){return botMode;}
    public boolean isKoiKoiWaiting() {return this.isKoiKoiWaiting;}//こいこい待機中かどうか
    public void resetIsKoiKoiWaiting() {
        System.out.println("こいこいリセット");
        this.isKoiKoiWaiting=false;
    
    }//こいこい待機中かどうか

    public void resetGame() {
        Deck deck = new Deck();
        this.game = new Game(deck);
        game.initializeGame(this);
        game.setCurrentPlayerIndex(1);

        this.lastScoringPlayer = -1;
        this.isKoiKoiWaiting = false;
        this.botFinish = false;
        System.out.println("ゲーム開始！ 配札完了！");
    }
   

    //PLAY_CARDで呼ばれる関数。
    public String processPlayerAction(int playerIndex, int handCardId, int fieldCardId) {
        Player currentPlayer = game.getPlayers().get(playerIndex == 2 ? 1 : 0);
        if(game.getPlayCount()==0){
            previousRoleResult = currentPlayer.getRoleResult();
            System.out.println("previousRoleResultセット"+previousRoleResult);
        }
        if (playerIndex != game.getCurrentPlayerIndex()) {
            return "ERROR: 現在のプレイヤーではありません。";
        }
        String playResult = handleCardPlay(playerIndex, handCardId, fieldCardId);//内部処理は委託する。
        game.incrementPlayCount();

        if (game.getPlayCount() >= 2) {
            RoleResult newRoleResult = RoleChecker.checkRules(currentPlayer);
            currentPlayer.setRoleResult(newRoleResult);
            boolean isUpScore = false; 
            System.out.println("previousRoleResult"+previousRoleResult);
            System.out.println("newRoleResult"+newRoleResult);
            if ((previousRoleResult.getAchievedRoles().size() <= newRoleResult.getAchievedRoles().size()) && (previousRoleResult.getTotalScore() < newRoleResult.getTotalScore())) {
                isUpScore = true;
            }
            previousRoleResult = newRoleResult;

            if (!newRoleResult.getAchievedRoles().isEmpty() && isUpScore) {
                lastScoringPlayer = playerIndex;
                currentPlayer.setKoiKoi(true);
                this.isKoiKoiWaiting = true;
                // **役をカンマ区切りの `String` に変換**
                String roleString = String.join(",", newRoleResult.getAchievedRoles());

                return "KOIKOI_WAITING: 役が成立しました。「こいこい」を選択してください。\n" +
                    "役: " + roleString + "\n" +
                    "得点: " + newRoleResult.getTotalScore();
            }
            
            return game.getIsEnd() ? "GAME_END: 相手の勝利。" : "NEXT_TURN: ターン " + game.getCurrentTurn();
        
        }
        return "CONTINUE_TURN: プレイヤー " + playerIndex + " のターンが継続中です。\n" + playResult;
    }


    //processPlayerActionの補助関数
    public String handleCardPlay(int playerIndex, int cardId,int fieldcard) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        Player player = game.getPlayers().get(playerIndex-1);//ここでー１をして0と1に直す
        int handIndex = findCardIndexInHand(player, cardId);
        Card playedCard = player.playCard(handIndex);
        game.getField().playCardOnField(player, playedCard, fieldcard);
        if (game.getPlayCount() == 0) {
            Card drawnCard = game.getDeck().draw();
            if (drawnCard != null) {
                player.addCardToHand(drawnCard);
                return "山札からカードを引きましたCardId: " + drawnCard.getId();
            }
            return "山札が空です。";//起こらない
        }
    
        return game.getPlayCount() == 1 ? "2回目のPlayなので相手のターンへ" : "エラー: 3回目のプレイは無効";
    }

    private int findCardIndexInHand(Player player, int cardId) {
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getId() == cardId) {
                return i;
            }
        }
        throw new IllegalArgumentException("指定されたカードIDが手札にありません: " + cardId);
    }

    public String determineWinner() {
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        System.out.println("player1.isKoiKoi()"+ player1.isKoiKoi());
        System.out.println("player2.isKoiKoi()"+player2.isKoiKoi());
        int score1 = player1.getRoleResult().getTotalScore();
        int score2 = player2.getRoleResult().getTotalScore();
        System.out.println("lastScoringPlayer"+lastScoringPlayer);
        // **最後に役を成立させたプレイヤー以外のスコアを0にする**
        if (lastScoringPlayer == 1) {
            score2 = 0;
        } else if (lastScoringPlayer == 2) {
            score1 = 0;
        } else {
            // **どちらも役を成立させていない場合**
            score1 = 0;
            score2 = 0;
        }
        // こいこいをしたプレイヤーのスコア処理
        if (player1.isKoiKoi()) {
            score1 = 0;
        }
        if (player2.isKoiKoi()) {
            score2 = 0;
        }
        int winnerStatus;
        String winnerMessage;

        if (score1 > score2) {
            winnerStatus = 1; // プレイヤー1の勝利
            winnerMessage = "勝利";
        } else if (score2 > score1) {
            winnerStatus = 2; // プレイヤー2の勝利
            winnerMessage = "勝利";
        } else {
            winnerStatus = 0; // 引き分け
            winnerMessage = "引き分け";
        }

        return winnerStatus + "," + winnerMessage; // 「1,プレイヤー 1 は勝利」 のような形式で返す
    }
    
    //これらはパブリックで管理される。
    public void nextTurn() {
        if (isKoiKoiWaiting) {
            System.out.println("こいこい待機中のため、ターンを進めません。");
            return;
        }
        game.nextTurn();
        //System.out.println("game.getIsEnd()=="+game.getIsEnd());
        if (game.getIsEnd()) {
            return;
        }
        int currentPlayerId = game.getCurrentPlayerIndex();
        if (botMode && isBotTurn(currentPlayerId)) {
            playBotTurn(currentPlayerId);
        }   
    }

    //BOTのターンかどうかを判定
    private boolean isBotTurn(int playerId) {
        return playerId == 2;//2はコンピュータのターン
    }
    //BOTのプレイ
    private void playBotTurn(int botPlayerId) {
        System.out.println("BOTのターンです。");
        Player bot = game.getPlayers().get(botPlayerId - 1);
        game.resetPlayCount();

        // **1回目のプレイ**
        scheduler.schedule(() -> {
            int[] bestMove = chooseBestMove(bot, game.getField());
            int bestCardId = bestMove[0];
            int bestFieldCardId = bestMove[1];

            System.out.println("1回目プレイ: " + bestCardId + " -> " + bestFieldCardId);
            System.out.println(handleCardPlay(2, bestCardId, bestFieldCardId));
            game.incrementPlayCount();
        }, 500, TimeUnit.MILLISECONDS);

        // **2回目のプレイ（場の更新を待つ）**
        scheduler.schedule(() -> {
            Card handCard = bot.getHand().get(bot.getHand().size() - 1);
            int handCardId = handCard.getId();
            int fieldCardId = chooseBestFieldCard(bot, game.getField(), handCard);

            System.out.println("2回目プレイ: " + handCardId + " -> " + fieldCardId);
            System.out.println(handleCardPlay(2, handCard.getId(), fieldCardId));
            game.incrementPlayCount();

            // **役のチェックを遅延実行（プレイ後に判定する）**
            scheduler.schedule(() -> {
                if (game.getPlayCount() >= 2) {
                    RoleResult newResult = RoleChecker.checkRules(bot);
                    bot.setRoleResult(newResult);

                    if (!botFinish && newResult.getAchievedRoles().size() > 0) {
                        botFinish = true;
                        lastScoringPlayer = botPlayerId;
                    }

                    game.resetPlayCount();

                    if (botFinish) {
                        System.out.println("BOTが役を達成したためゲーム終了");
                        endGame();
                        return;
                    }
                }
                nextTurn();
            }, 500, TimeUnit.MILLISECONDS);
        }, 5000, TimeUnit.MILLISECONDS);
    }

    private int findMatchingCardInField(Field field, int cardId) {
        List<Card> fieldCards = field.getCards();
        for (Card card : fieldCards) {
            if (card.getMonth().ordinal()  == cardId / 4 ) {
                System.out.println("card.getMonth().ordinal()"+card.getMonth().ordinal() +"cardId/4"+ (cardId / 4) );
                return card.getId();
            }
        }
        return -1; // 場に一致するカードがない場合は -1（そのまま場に出す）
    }
    public void endGame(){game.setIsEnd(true);}//ゲーム終了処理
    public boolean isGameFinished() {return game.getIsEnd();}

    public int[] chooseBestMove(Player player, Field field) {
        List<Card> hand = player.getHand();
        int bestCardId = -1;
        int bestFieldCardId = -1;
        int maxScore = Integer.MIN_VALUE;
    
        for (Card handCard : hand) {
            // 場のカードがない場合（単独プレイ）
            int score = evaluateMove(player, field, handCard, null);
            if (score > maxScore) {
                maxScore = score;
                bestCardId = handCard.getId();
                bestFieldCardId = -1;
            }

            // 場にマッチするカードがある場合
            for (Card fieldCard : field.getCards()) {
                if (fieldCard.getMonth() == handCard.getMonth()) {
                    score = evaluateMove(player, field, handCard, fieldCard);
                    if (score > maxScore) {
                        maxScore = score;
                        bestCardId = handCard.getId();
                        bestFieldCardId = fieldCard.getId();
                    }
                }
            }
        }
        return new int[]{bestCardId, bestFieldCardId};
    }

    public int chooseBestFieldCard(Player player, Field field, Card handCard) {
        int bestFieldCardId = -1;
        int maxScore = Integer.MIN_VALUE;
    
        for (Card fieldCard : field.getCards()) {
            if (fieldCard.getMonth() == handCard.getMonth()) {
                int score = evaluateMove(player, field, handCard, fieldCard);
                if (score > maxScore) {
                    maxScore = score;
                    bestFieldCardId = fieldCard.getId();
                }
            }
        }
        return bestFieldCardId;
    }

    private int evaluateMove(Player player, Field field, Card handCard, Card fieldCard) {
        List<Card> simulatedCaptures = new ArrayList<>(player.getCaptures());
        int previousScore = player.getRoleResult().getTotalScore();//過去のスコア

        boolean isMatching = (fieldCard != null);
        if (isMatching) {
            simulatedCaptures.add(handCard);
            simulatedCaptures.add(fieldCard);
        }
        RoleResult simulatedResult = RoleChecker.checkRules(player);
        int newScore = simulatedResult.getTotalScore();// 現在のスコア
        int scoreDelta = newScore - previousScore;
        int evaluation = 0;
        //評価基準1:役が成立するなら高評価
        evaluation += scoreDelta * 5;
        //評価基準2:高得点のカードを取れるなら加点
        if (handCard.getPoint() == Card.Point.HIKARI) evaluation += 3;
        if (handCard.getPoint() == Card.Point.TANE) evaluation += 2;
        if (handCard.getPoint() == Card.Point.TANZAKU) evaluation += 1;
        //評価基準3:場のカードとマッチするなら加点
        if (isMatching) evaluation += 3;
        //評価基準4:相手に有利なカードを場に残さない（マッチしない場合のみ適用）
        if (!isMatching && field.hasMatchingCard(handCard)) {
            evaluation -= 2; // 相手に取られやすいカードならペナルティ
        }
    
        return evaluation;
    }

    public int checkScoreForCards(List<Card> cards) {
        int lightCount = 0;
        int rainyLightCount = 0;
        int hanamiCount = 0;
        int tsukimiCount = 0;
        int inoshikaChoCount = 0;
        int redShortCount = 0;
        int blueShortCount = 0;
        int taneCount = 0;
        int tanCount = 0;
        int kasuCount = 0;
        for (Card card : cards) {
            for (Card.Role role : card.getRole()) {
                switch (role.getName()) {
                    case "五光": lightCount++; break;
                    case "雨四光": rainyLightCount++; break;
                    case "花見で一杯": hanamiCount++; break;
                    case "月見で一杯": tsukimiCount++; break;
                    case "猪鹿蝶": inoshikaChoCount++; break;
                    case "赤短": redShortCount++; break;
                    case "青短": blueShortCount++; break;
                    case "タネ": taneCount++; break;
                    case "タン": tanCount++; break;
                    case "カス": kasuCount++; break;
                }
            }
        }
        int totalScore = 0;
        if (lightCount == 5) totalScore += 10;
        else if (lightCount == 4) totalScore += (rainyLightCount > 0 ? 7 : 8);
        else if (lightCount - rainyLightCount == 3) totalScore += 5;
    
        if (hanamiCount == 2) totalScore += 5;
        if (tsukimiCount == 2) totalScore += 5;
    
        if (inoshikaChoCount == 3) totalScore += 5;
    
        if (redShortCount >= 3) totalScore += 5;
        if (blueShortCount >= 3) totalScore += 5;
    
        if (taneCount >= 5) totalScore += 1;
        if (tanCount >= 5) totalScore += 1;
        if (kasuCount >= 10) totalScore += 1;
    
        return totalScore;
    }
}