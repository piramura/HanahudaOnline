import java.util.List;

public class GameController implements GameStateListener {
    private MainFrame mainFrame;
    private GameClient gameClient;
    private GameStateManager gameStateManager;
    private int playerID; // プレイヤーID
    private int currentTurn;

    // コンストラクタ
    public GameController(MainFrame frame, GameClient client) {
        this.mainFrame = frame;
        this.gameClient = client;
        this.gameStateManager = new GameStateManager(); // ゲーム状態管理クラスの初期化
    }

    // プレイヤーID設定
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
        gameStateManager.setCurrentPlayer(playerID);
    }

    // 現在のターンを設定
    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    // ゲーム開始
    public void startGame() {
        syncUIWithGameState(); // UI初期化
    }

    // ゲーム状態の更新
    public void updateGameStateFromServer() {
        try {
            String response = gameClient.fetchGameState(); // サーバーから状態を取得
            // gameStateManager.updateStateFromResponse(response); // レスポンスを解析し状態を更新
            syncUIWithGameState(); // UI更新
        } catch (Exception e) {
            System.err.println("ゲーム状態の更新に失敗しました: " + e.getMessage());
        }
    }

    // カードクリック時の処理
    public void handleCardClick(Card card) {
        if (gameStateManager.getCurrentPlayer() != playerID) {
            System.out.println("現在のターンではありません。");
            return;
        }

        try {
            gameClient.playCard(String.valueOf(card.getId())); // カードをサーバーに送信
            updateGameStateFromServer(); // サーバー応答で状態を更新
        } catch (Exception e) {
            System.err.println("カード送信エラー: " + e.getMessage());
        }
    }

    // ゲーム終了処理
    public void handleGameOver() {
        System.out.println("ゲーム終了: 勝者は " + gameStateManager.getWinnerInfo());
        mainFrame.displayGameOver(gameStateManager.getWinnerInfo()); // ゲーム終了画面を表示
    }

    // UI更新
    private void syncUIWithGameState() {
        mainFrame.updateBoard(gameStateManager.getFieldCards()); // フィールドカード更新
        mainFrame.updatePlayerHand(gameStateManager.getPlayerHand()); // プレイヤーの手札更新
        mainFrame.updateOpponentHand(gameStateManager.getOpponentHand().size()); // 相手の手札数更新
        mainFrame.updateTurnLabel(gameStateManager.getCurrentPlayer()); // 現在のターンを更新
    }

    // GameStateListenerインターフェースの実装
    @Override
    public void onStateUpdated(GameStateManager manager) {
        syncUIWithGameState(); // 状態が更新されたらUIを同期
    }

    // ターン情報の更新
    public void updateTurn(int turn) {
        setCurrentTurn(turn);
        mainFrame.updateTurnLabel(turn);
        System.out.println("ターンが更新されました: " + turn);
    }

    // カードをエリアに追加
    public void addCardToArea(int cardID, String area) {
        gameStateManager.addCardToArea(cardID, area);
        syncUIWithGameState(); // UIを更新
    }

    // 相手の手札の更新
    public void updateOpponentHand(int count) {
        gameStateManager.updateOpponentHandCount(count);
        mainFrame.updateOpponentHand(count);
    }
}
