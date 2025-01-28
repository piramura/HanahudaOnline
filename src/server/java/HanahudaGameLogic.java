import java.util.ArrayList;
import java.util.List;

public class HanahudaGameLogic {
    private Game game;
    private int currentPlayerIndex;
    private int playCount = 0; // 現在のターンでのプレイ回数
    public Game getGame(){
        return game;
    }

    public void resetGame() {
        Deck deck = new Deck();
        this.game = new Game(deck);
        game.initializeGame(this);
        System.out.println("ゲーム開始！ 配札完了！");
    }
    private boolean isCurrentPlayer(int playerIndex) {
        return playerIndex == currentPlayerIndex;
    }
    
    

    //PLAY_CARDで呼ばれる関数。
    public String processPlayerAction(int playerIndex, int handCardId, int fieldCardId) {
        if (playerIndex != game.getCurrentPlayerIndex()) {
            return "ERROR: 現在のプレイヤーではありません。";
        }
        //System.out.println("HanahudaLogicの中でprocessPlayerActionを処理中");
        String playResult = handleCardPlay(playerIndex, handCardId, fieldCardId);//内部処理は委託する。
        game.incrementPlayCount();

        if (game.getPlayCount() >= 2) {
            RoleResult roleResult = RoleChecker.checkRules(game.getPlayers().get(playerIndex));
            if (!roleResult.getAchievedRoles().isEmpty()) {
                //game.getPlayers().get(playerIndex).addScore(roleResult.getTotalScore());
                return "KOIKOI_WAITING: 役が成立しました。「こいこい」を選択してください。\n" +
                       "役: " + roleResult.getAchievedRoles() + "\n" +
                       "得点: " + roleResult.getTotalScore();
            }
            game.resetPlayCount();
            game.setCurrentPlayerIndex((game.getCurrentPlayerIndex() + 1) % 2);
            return "NEXT_TURN: プレイヤー " + game.getCurrentPlayerIndex() + " のターンです。\n" + playResult;

        }
        System.out.println("CONTINUE_TURN: プレイヤー " + playerIndex + " のターンが継続中です。\n" + playResult);
        return "CONTINUE_TURN: プレイヤー " + playerIndex + " のターンが継続中です。\n" + playResult;
    }
    //processPlayerActionの補助関数
    public String handleCardPlay(int playerIndex, int cardId,int fieldcard) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        System.out.println("HanahudaLogicの中でhandleCardPlayを処理中");
        // デッキの中身を出力
        game.printDeckContents();
        // プレイヤーの手札を確認
        game.printPlayerHands();
        Player player = game.getPlayers().get(playerIndex);
        int handIndex = findCardIndexInHand(player, cardId);
        Card playedCard = player.playCard(handIndex);
        if(fieldcard == -1){
            game.getField().addCard(playedCard);
            System.out.println("ばにカードをおきます");
        }else{
            Field field = game.getField();
            Card fieldCard = game.getField().takeCardById(fieldcard);
            if (!field.isThreeCards(fieldCard)) {
                player.captureCard(fieldCard);
                player.captureCard(playedCard);
            } else {
                List<Card> takenCards = field.takeCardsByMonth(fieldCard.getMonth());
                for (Card card : takenCards) {
                    player.captureCard(card);
                }
                player.captureCard(playedCard);
            }
        }
        if (game.getPlayCount() == 0) {
            Card drawnCard = game.getDeck().draw();
            player.addCardToHand(drawnCard);
            System.out.println("1回目のプレイ");
            // デッキの中身を出力
            game.printDeckContents();
            // プレイヤーの手札を確認
            game.printPlayerHands();
            return "山から引いたカードを手札に追加したのでそれを元に場のカード選べ";
        } else {
            return "相手のターンへ";
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
        game.nextTurn();
    }
    
    
    public void endGame(){
        //ゲーム終了処理
        //リザルトを返してゲームリセット
    }

    public boolean isGameFinished() {
        return game.isBothHandEmpty();
    }
    
    
}
