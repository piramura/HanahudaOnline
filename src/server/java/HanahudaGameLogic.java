import java.util.ArrayList;
import java.util.List;

public class HanahudaGameLogic {
    private Game game;
    private int playCount = 0; // 現在のターンでのプレイ回数
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

            // **前回の得点を保存**
            List<String> previousRoles = new ArrayList<>(currentPlayer.getAchievedRoles());

            // **現在の得点を計算**
            RoleResult roleResult = new RoleResult(-1, List.of(""), -1);
            try {
                roleResult = RoleChecker.checkRules(currentPlayer);
            } catch (Exception e) {
                System.err.println("ERROR: RoleResult でエラー");
            }

            List<String> currentRoles = roleResult.getAchievedRoles();
            currentPlayer.setAchievedRoles(currentRoles);

            System.out.println("DEBUG: 現在の役の成立結果: " + currentRoles);
            // **こいこい選択判定**
            if (!currentRoles.isEmpty() && (!previousRoles.containsAll(currentRoles) || currentPlayer.isKoiKoi())) {
                currentPlayer.setKoiKoi(true); // こいこい状態を更新
                return "KOIKOI_WAITING: 役が成立しました。「こいこい」を選択してください。\n" +
                    "役: " + roleResult.getAchievedRoles() + "\n" +
                    "得点: " + roleResult.getTotalScore();
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
        game.printPlayerHands();
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
            // // デッキの中身を出力
            // game.printDeckContents();
            // // プレイヤーの手札を確認
            // game.printPlayerHands();
            return "山から引いたカードを手札に追加したのでそれを元に場のカード選べ";
        } else if(game.getPlayCount() == 1) {
            System.out.println("DEBUG: 2回目のプレイ - 山札からカードは引かない");
            // // デッキの中身を出力
            // game.printDeckContents();
            // // プレイヤーの手札を確認
            // game.printPlayerHands();
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

        RoleResult result1 = RoleChecker.checkRules(player1);
        RoleResult result2 = RoleChecker.checkRules(player2);

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
        if(game.getIsEnd()){
            System.out.println("GAME_END :ゲーム終了");
        }else{
            game.nextTurn();
        }
    }
    public void endGame(){
        //ゲーム終了処理
        game.setIsEnd(true);
        System.out.println("GAME_END: endGameだけど");
        //リザルトを返してゲームリセット
    }

    public boolean isGameFinished() {
        //System.out.println("game.isBothHandEmpty()="+game.isBothHandEmpty()+" game.getIsEnd()="+game.getIsEnd());
        return game.isBothHandEmpty() || game.getIsEnd();
    }
    
    
}
