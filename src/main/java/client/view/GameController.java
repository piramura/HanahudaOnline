import java.util.ArrayList;
import java.util.List;
public class GameController {
    private MainFrame mainFrame;
    private CommClient commClient;
    private GameStateManager gameStateManager;
    private CardManager cardManager;
    private int playerID; // プレイヤーIDを保持するフィールド
    private int currentturn;
    // プレイヤーIDを設定するメソッドを追加
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
    public void setCurrentTurn(int currentturn) {
        this.currentturn = currentturn;
    }
    public GameController(MainFrame frame, CommClient client) {
        this.mainFrame = frame;
        this.commClient = client;
        this.gameStateManager = new GameStateManager(); // GameStateManagerを内部で初期化
        this.cardManager = new CardManager(); // CardManagerの初期化
    }
    public GameStateManager getGameStateManager() {
        return this.gameStateManager; // GameController が保持する GameStateManager を返す
    }


    // ゲーム開始
    public void startGame() {
        mainFrame.updateBoard(gameStateManager.getFieldCards());
        mainFrame.updatePlayerHand(gameStateManager.getPlayerHand());
        mainFrame.updateOpponentHand(gameStateManager.getOpponentHand().size());
    }
        // サーバー応答を処理して状態を更新
    public void updateGameState(List<Integer> fieldCardIDs, List<Integer> playerHandIDs, int opponentHandCount) {
        // GameStateManager の状態を更新
        gameStateManager.updateState(fieldCardIDs, playerHandIDs, opponentHandCount);

        // 更新された状態を UI に反映
        updateUI();
    }
    // ゲーム終了処理
public void handleGameOver() {
    System.out.println("ゲーム終了処理を実行中...");
}
    
    // 初期化処理
    private void setupInitialCards() {
        // 場のカード
        List<Card> fieldCards = gameStateManager.getFieldCards();
        mainFrame.updateBoard(fieldCards);

        // プレイヤーの手札
        List<Card> playerHand = gameStateManager.getPlayerHand();
        mainFrame.updatePlayerHand(playerHand);

        // 相手の手札
        int opponentCardCount = gameStateManager.getOpponentHand().size();
        mainFrame.updateOpponentHand(opponentCardCount);

        mainFrame.repaint();
    }
    public void updateTurnInfo(int currentPlayer) {
    // UIのターン表示を更新
    mainFrame.updateTurnLabel(currentPlayer );
    gameStateManager.setCurrentPlayer(currentPlayer);
}

    // GameController.java
public void handleCardClick(Card card) {
    try {
        if (gameStateManager.getCurrentPlayer() == playerID) { // 現在のターンの確認
        System.out.println("クリックしたカードID: " + card.getId());
        sendCardToServer(card.getId());
    } else {
        System.out.println("現在のターンではありません。");
    }
    } catch (IllegalArgumentException e) {
        System.err.println(e.getMessage());
    }
}
    private void sendCardToServer(int cardID) {
    String message = "PLAY_CARD:" + cardID;
    try {
        commClient.send(message); // サーバーに送信
        System.out.println("サーバーに送信しました: " + message);
    } catch (Exception e) {
        System.err.println("サーバーへの送信エラー: " + e.getMessage());
    }
}


    public void updateUI() {
        mainFrame.updateBoard(gameStateManager.getFieldCards());
        mainFrame.updatePlayerHand(gameStateManager.getPlayerHand());
        mainFrame.updateOpponentHand(gameStateManager.getOpponentHand().size());
    }

    // サーバーにクリック情報を送信
    private void sendCardClickToServer(int cardID) {
        try {
            String message = "PLAY_CARD:" + cardID;
            commClient.send(message);
            System.out.println("Sent to server: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send card click to server: " + e.getMessage());
        }
    }
    // GameStateManager に追加
public void addCardToArea(int cardID, String area) {
    Card newCard = gameStateManager.cardFactory.createCard(cardID); // カードを生成

    switch (area) {
        case "field":
            gameStateManager.addFieldCard(newCard); // フィールドに追加
            break;
        case "hand":
            gameStateManager.addPlayerCard(newCard); // プレイヤーの手札に追加
            break;
        default:
            throw new IllegalArgumentException("無効なエリア指定: " + area);
    }
}
public void updateTurn(int turn) {
    mainFrame.updateTurnLabel(turn);
    System.out.println("ターンが更新されました: currentturn " + turn);
}
public void updateOpponentHand(int count) {
    gameStateManager.updateOpponentHandCount(count);
    mainFrame.updateOpponentHand(count); // UIを更新
}
    
}

