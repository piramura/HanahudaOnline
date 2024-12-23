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
        int cardIndex = Integer.parseInt(parts[1]);
            result = handleCardPlay(playerIndex, cardIndex);
        } catch (IllegalArgumentException e) {
            return "エラー: " + e.getMessage();
        }
        
        if (isGameFinished()) {
            return result;
        }
        return result + "\n次のターンはプレイヤー " + (currentPlayerIndex + 1) + " です。";
    }
    

    

    public String handleCardPlay(int playerIndex, int cardIndex) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        Player player = game.getPlayers().get(playerIndex);
        Card playedCard = player.playCard(cardIndex);

        game.getField().processPlayedCard(player, playedCard); // Field に委譲
        Card drawnCard = game.getDeck().draw();
        if (drawnCard != null) {
            game.getField().processDrawnCard(player, drawnCard); // Field に委譲
        }

        nextTurn();
        return "プレイヤー " + (playerIndex + 1) + " がカードをプレイしました。\n";
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
        return game.getDeck().getCards().isEmpty(); // 簡易版の終了条件
    }



}
