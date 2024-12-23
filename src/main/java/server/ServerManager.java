import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private GameManager gameManager = new GameManager();
    private List<ServerThread> clients = new ArrayList<>();
    private int maxClients = 2;
    

    // クライアントの追加
    public synchronized void addClient(ServerThread clientThread) {
        if (clients.size() > maxClients) {
            clientThread.sendMessage("FULL"); // サーバーが満員の場合は通知
            clientThread.close();
            return;
        }
        int playerNumber = clients.size() + 1; // プレイヤー番号は1から始まる
        clientThread.setPlayerNumber(playerNumber); // サーバースレッドにプレイヤー番号をセット
        clients.add(clientThread); // クライアントをリストに追加

        clientThread.sendMessage("PLAYER_NUMBER:" + playerNumber); // クライアントに通知
        System.out.println("プレイヤー " + playerNumber + " が接続しました。");
        if (clients.size() == maxClients) {
            System.out.println("プレイヤーが揃いました。ゲームを開始します。");
            startGame(); // プレイヤーが揃ったらゲームをスタート
        }
    }
    public synchronized int getClientCount() {
       return clients.size();
    }

    // クライアントの削除と接続終了
    public synchronized void removeClient(ServerThread clientThread) {
        synchronized (clients) {
            if (clients.contains(clientThread)) {
                try {
                    clientThread.sendMessage("DISCONNECT"); // 切断メッセージを送信
                    clientThread.getClient().close(); // 接続を閉じる
                } catch (Exception e) {
                    System.err.println("クライアントのクローズ処理中にエラーが発生しました: " + e.getMessage());
                }
                clients.remove(clientThread); // リストから削除
                System.out.println("クライアントが切断されました。現在の接続数: " + clients.size());
                broadcast("クライアントが切断されました。現在の接続数: " + clients.size());
            }
        }
    }
    // 全クライアントの接続を終了
    public synchronized void shutdownAllClients() {
        broadcast("サーバーがシャットダウンします。");
        for (ServerThread clientThread : clients) {
            try {
                clientThread.getClient().close(); // 各クライアントの接続を閉じる
            } catch (Exception e) {
                System.err.println("クライアントのシャットダウン中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
        }
        clients.clear(); // リストをクリア
        System.out.println("全クライアントの接続を終了しました。");
    }
    
    // メッセージの処理
    public synchronized void processMessage(ServerThread clientThread, String message) {
        try {
            if (message.equalsIgnoreCase("/RESET")) {
                resetGame();
                return;
            }
            else if (message.equalsIgnoreCase("WHOSE_TURN")) {
                String response = "現在のターンはプレイヤー " + (gameManager.getCurrentPlayerIndex() + 1) + " です。";
                clientThread.sendMessage(response); // 直接 `ServerThread` に送信
                return;
            }
            else if (message.startsWith("PLAY_CARD")) {
                int clientIndex = clients.indexOf(clientThread); // クライアントのインデックスを取得
                if (clientIndex == -1) {
                    System.err.println("クライアントがリストに存在しません。");
                    return;
                }
                String response = gameManager.processPlayerAction(clientIndex, message);
                broadcast(response);
                broadcastGameState();

                // ゲーム終了判定
                if (gameManager.isGameFinished()) {
                    broadcast("ゲーム終了！ 勝者: " + gameManager.getWinnerInfo());
                    return;
                }
                else {
                    // 次のプレイヤーのターンを通知
                    int nextPlayer = gameManager.getCurrentPlayerIndex() + 1;
                    broadcast("次のターンはプレイヤー " + nextPlayer + " です。");
                }
            }
            else if (message.equalsIgnoreCase("/QUIT")) {
                removeClient(clientThread);
            } else {
                clientThread.sendMessage("無効なコマンドです。");
            }
        } catch (Exception e) {
            System.err.println("クライアントのメッセージ処理中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    public synchronized void broadcast(String message) {
        List<ServerThread> disconnectedClients = new ArrayList<>();
        for (ServerThread client : clients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.println("ブロードキャスト送信エラー: " + e.getMessage());
                disconnectedClients.add(client);
            }
        }
        // 切断されたクライアントを削除
        for (ServerThread client : disconnectedClients) {
            removeClient(client);
        }
    }

    public synchronized void startGame() {
        try {
            gameManager.startGame();
            broadcast("START");
            broadcastGameState(); // 初期状態を全プレイヤーに送信
        } catch (Exception e) {
            System.err.println("ゲーム開始処理中にエラーが発生しました: " + e.getMessage());
        }
    }
    public synchronized void resetGame() {
        try {
            gameManager.resetGame();
            broadcast("ゲームがリセットされました！ 新しいゲームを開始します。");
            startGame();
        } catch (Exception e) {
            System.err.println("ゲームリセット中にエラーが発生しました: " + e.getMessage());
        }
    }
public synchronized void broadcastGameState() {
    for (int i = 0; i < clients.size(); i++) {
        ServerThread client = clients.get(i);
        sendGameState(client, i); // プレイヤーごとに送信
    }
}
private void sendCards(ServerThread client, List<Card> cards, String area) {
    for (Card card : cards) {
        client.sendMessage("UPDATE_CARD:" + card.getId() + ":" + area);
    }
}

private void sendGameState(ServerThread client, int clientIndex) {
    try {
        Game game = gameManager.getGame();
        int currentPlayer = gameManager.getCurrentPlayerIndex();

        sendCards(client, game.getField().getCards(), "field");
        sendCards(client, game.getPlayers().get(clientIndex).getHand(), "hand");
        sendCards(client, game.getPlayers().get(Math.abs(clientIndex-1)).getHand(), "opponentHand");
        client.sendMessage("TURN:" + currentPlayer);

        if (gameManager.isGameFinished()) {
            client.sendMessage("GAME_OVER:" + gameManager.getWinnerInfo());
        }
    } catch (Exception e) {
        System.err.println("ゲーム状態送信エラー: " + e.getMessage());
    }
}


// カードリストをIDのCSV形式に変換
private String cardIDsToString(List<Card> cards) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < cards.size(); i++) {
        sb.append(cards.get(i).getId());
        if (i < cards.size() - 1) sb.append(",");
    }
    return sb.toString();
}


}
