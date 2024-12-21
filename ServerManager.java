import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private GameManager gameManager = new GameManager();
    private List<CommServer> clients = new ArrayList<>();
    private int maxClients = 2;

    // クライアントの追加
    public synchronized void addClient(CommServer client) {
        if (clients.size() >= maxClients) {
            client.send("サーバーは満員です。接続を終了します。");
            client.close();
            return;
        }
        clients.add(client);
        int newClientIndex = clients.size() - 1;
         // 現在のゲーム情報を新しいクライアントに送信
        String initialMessage = "ゲームに接続しました！ あなたはプレイヤー " + (newClientIndex + 1) + " です。\n";
        initialMessage += gameManager.getPlayerGameState(newClientIndex);
        sendMessage(newClientIndex, initialMessage);
        // 全員に通知
        broadcast("プレイヤー " + (newClientIndex + 1) + " が接続しました。現在のプレイヤー数: " + clients.size());
        if (clients.size() == maxClients) {
            startGame();
        }
    }

    // ゲーム開始処理
    public synchronized void startGame() {
        gameManager.startGame();
        // broadcast("ゲームが開始されました！ プレイヤー 1 のターンです。\n" + gameManager.getGameState());
        broadcast("ゲームが開始されました！ プレイヤー 1 のターンです。\n");
        // プレイヤーごとに個別の情報を送信
        for (int i = 0; i < clients.size(); i++) {
            String message = gameManager.getPlayerGameState(i);
            sendMessage(i, message);
        }
    }
    public void resetGame() {
        gameManager.resetGame();  // ゲームのリセット
        broadcast("ゲームがリセットされました！ 新しいゲームを開始します。");
        startGame();  // 新しいゲームを開始
    }


    // メッセージの処理
    public synchronized void processMessage(int clientIndex, String message) {
        String response;
        if (message.equalsIgnoreCase("/RESET")) {
            resetGame();  // リセット処理を呼び出す
            return;
        }
        if (message.equalsIgnoreCase("WHOSE_TURN")) {
            response = "現在のターンはプレイヤー " + (gameManager.getCurrentPlayerIndex() + 1) + " です。";
            sendMessage(clientIndex, response);
            return;
        }
         if (message.startsWith("PLAY_CARD")) {
            response = gameManager.processPlayerAction(clientIndex, message);
            broadcast(response);  // プレイ結果を全員に送信
            // プレイヤーの手札更新通知
            String handMessage = "あなたの手札: " + gameManager.getGame().getPlayers().get(clientIndex).getHand().toString();
            sendMessage(clientIndex, handMessage);
            // プレイヤーの取り札情報を個別送信
            String captureMessage = "あなたの取り札: " + gameManager.getGame().getPlayers().get(clientIndex).getCaptures().toString();
            sendMessage(clientIndex, captureMessage);
            // 役判定の実行
            gameManager.checkRules(clientIndex);
            int numRules = gameManager.getGame().getPlayers().get(clientIndex).chacknumberRules();

            if (numRules > 0) {
                String result = "プレイヤー " + (clientIndex + 1) + " は " + numRules + " つの役を成立させました！";
                broadcast(result);  // 役判定結果を送信
            } else {
                broadcast("プレイヤー " + (clientIndex + 1) + " は役を成立させませんでした。");
            }

            // ゲーム終了判定
            if (gameManager.isGameFinished()) {
                broadcast("ゲーム終了！ 勝者: " + gameManager.getWinnerInfo());
                return;
            }

            // 次のプレイヤーのターン通知
            int nextPlayer = gameManager.getCurrentPlayerIndex() + 1;
            broadcast("次のターンはプレイヤー " + nextPlayer + " です。");

        } else if (message.equalsIgnoreCase("/QUIT")) {
            removeClient(clientIndex);
            return;
        } else {
            response = "無効なコマンドです。";
            sendMessage(clientIndex, response);
        }
    }

    // クライアントへのメッセージ送信
    public synchronized void sendMessage(int clientIndex, String message) {
        try {
            clients.get(clientIndex).send(message);
        } catch (Exception e) {
            System.out.println("送信エラー: クライアント " + clientIndex + " を切断します。");
            removeClient(clientIndex);
        }
    }


    // 全クライアントにブロードキャスト
    public synchronized void broadcast(String message) {
        for (CommServer client : clients) {
            client.send(message);
        }
    }

    // クライアントの削除
    public synchronized void removeClient(int clientIndex) {
        if (clientIndex >= 0 && clientIndex < clients.size()) {
            clients.get(clientIndex).close();
            clients.remove(clientIndex);
            System.out.println("クライアント " + clientIndex + " が切断されました。現在の接続数: " + clients.size());
            broadcast("クライアント " + clientIndex + " が切断されました。");
        }
    }
}
