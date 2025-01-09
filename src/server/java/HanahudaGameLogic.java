import java.util.ArrayList;
import java.util.List;
public class HanahudaGameLogic {
    private Game game;
    private int currentPlayerIndex;

    public void  resetGame() {
        Deck deck = new Deck();
        deck.shuffle();
        game = new Game(deck,this);
        setupPlayers();//プレイヤーに初期の手札を配る処理を実行する。
        setupField();//山札から場に8枚のカードを配置する処理を行う。
        currentPlayerIndex = 0;
        System.out.println("ゲーム開始！ 配札完了！");
    }
    // プレイヤーをセットアップ
    private void setupPlayers() {
        for (int i = 0; i < 2; i++) {
            Player player = new Player();
            game.addPlayer(player);
            for (int j = 0; j < 8; j++) {
                player.addCardToHand(game.getDeck().draw());
            }
        }
    }
    public Game getGame(){
        return game;
    }

    // 場のカードをセットアップ
    private void setupField() {
        for (int i = 0; i < 8; i++) {
            game.getField().addCard(game.getDeck().draw());
        }
    }

        // プレイヤーのアクション（カードを出すなど）を処理し、ゲームの進行を制御。
        // 入力検証、カードのプレイ、ルールの適用を行う。
    public String processPlayerAction(int playerIndex, String action) {
        if (!isCurrentPlayer(playerIndex)) {
            return "エラー: あなたのターンではありません。";
        }
        

        String[] parts = validateAction(action);
        if (parts == null) {
            return "エラー: 不正なコマンド形式です。";
        }
        String result="エラー：けっか判定できません。";
        try {
        int cardIndex = Integer.parseInt(parts[1]);//いまは１～４８のすうじだからこいつがどこにあるかさがしてそれが手札なら値を返す。

            result = handleCardPlay(playerIndex, cardIndex);
        } catch (IllegalArgumentException e) {
            return "エラー: " + e.getMessage();
        }
        if (action.startsWith("DECLARE_END")) {
            game.getPlayers().get(playerIndex).declareEnd();
            return "プレイヤー " + (playerIndex + 1) + " がゲームを終了しました。";
        }
        
        if (isGameFinished()) {
            return result;
        }
        return result + "\n次のターンはプレイヤー " + (currentPlayerIndex + 1) + " です。";
    }
    

    

    public String handleCardPlay(int playerIndex, int cardId) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        Player player = game.getPlayers().get(playerIndex);
        // 手札内のインデックスを取得
        int handIndex = findCardIndexInHand(player, cardId);
        System.out.println("DEBUG: カードID " + cardId + " は手札のインデックス " + handIndex + " にあります。");

        // 手札からカードをプレイ
        Card playedCard = player.playCard(handIndex);

        game.getField().processPlayedCard(player, playedCard); // Field に委譲
        Card drawnCard = game.getDeck().draw();
        if (drawnCard != null) {
            game.getField().processDrawnCard(player, drawnCard); // Field に委譲
        }

        nextTurn();
        return "プレイヤー " + (playerIndex + 1) + " がカードをプレイしました。\n";
    }
    private int findCardIndexInHand(Player player, int cardId) {
        ArrayList<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {//工夫ポイント
            if (hand.get(i).getId() == cardId) {
                return i; // 一致した場合、そのインデックスを返す
            }
        }
        throw new IllegalArgumentException("指定されたカードIDが手札にありません: " + cardId);
    }

    private String[] validateAction(String action) {//プレイヤーからの入力を検証し、適切な形式かをチェックする。
        if (!action.startsWith("PLAY_CARD:")) {
            return null;
        }

        String[] parts = action.split(":");
        if (parts.length < 2 || !parts[1].matches("\\d+")) {
            return null;
        }

        return parts;
    }

    private boolean isCurrentPlayer(int playerIndex) {
        return playerIndex == (currentPlayerIndex % 2);
    }

    private void nextTurn() {
        currentPlayerIndex = currentPlayerIndex + 1;
        game.setNowTurn(currentPlayerIndex);
    }

    public boolean isGameFinished() {
        boolean bothHandsEmpty = game.getPlayers().stream().allMatch(player -> player.getHand().isEmpty());
        boolean anyPlayerDeclaredEnd = game.getPlayers().stream().anyMatch(Player::hasDeclaredEnd);
        // 両方のプレイヤーの手札が空、またはどちらかが役を成立させ「こいこい」をしない場合
        return bothHandsEmpty || anyPlayerDeclaredEnd;
    }



}
