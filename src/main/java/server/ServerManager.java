import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private GameManager gameManager = new GameManager();
    private List<ServerThread> clients = new ArrayList<>();
    private int maxClients = 2;

    // クライアントの追加
    public synchronized void addClient(ServerThread clientThread) {
        if (clients.size() >= maxClients) {
             System.err.println("クライアントの追加に失敗しました: サーバーは満員です。");
        try {
            clientThread.sendMessage("FULL"); // クライアントに満員を通知
            clientThread.close(); // 接続を切断
        } catch (Exception e) {
            System.err.println("接続を拒否する際にエラーが発生しました: " + e.getMessage());
        }
        return;
        }
        clients.add(clientThread); // ServerThread をリストに追加
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
                sendPlayerDetails(clientThread, clientIndex);// プレイヤーへの詳細情報送信

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
    public void sendCardUpdateToClient(ServerThread client, int cardId, String area) {
        String message = "UPDATE_CARD:" + cardId + ":" + area; // カードIDと配置場所を送信
        client.sendMessage(message);
    }
    // 特定のクライアントにカード情報を送信
    public void sendCardUpdate(ServerThread client, int cardId, String area) {
        String message = "UPDATE_CARD:" + cardId + ":" + area;
        client.sendMessage(message);
    }

    // 全クライアントにカード情報を送信
    public void broadcastCardUpdate(int cardId, String area) {
        String message = "UPDATE_CARD:" + cardId + ":" + area;
        for (ServerThread client : clients) {
            client.sendMessage(message);
        }
    }

    // ターン情報を送信
    public void sendTurnInfo(int playerId) {
        String message = "TURN:" + playerId;
        for (ServerThread client : clients) {
            client.sendMessage(message);
        }
    }

    // ゲーム終了通知を送信
    public void sendGameOver(int winnerId) {
        String message = "GAME_OVER:" + winnerId;
        for (ServerThread client : clients) {
            client.sendMessage(message);
        }
    }


    public synchronized void startGame() {
        try {
            gameManager.startGame();
            broadcast("ゲームが開始されました！ プレイヤー 1 のターンです。");

            // 各プレイヤーに個別のゲーム情報を送信
            for (int i = 0; i < clients.size(); i++) {
                try {
                    ServerThread clientThread = clients.get(i);
                    sendPlayerDetails(clientThread, i); // プレイヤーの詳細情報を送信
                } catch (Exception e) {
                    System.err.println("プレイヤー " + (i + 1) + " へのゲーム情報送信中にエラーが発生しました: " + e.getMessage());
                    e.printStackTrace();
                }
            }   
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
    private void sendPlayerDetails(ServerThread clientThread, int clientIndex) {
        try {
            String handMessage = "あなたの手札: " + gameManager.getGame().getPlayers().get(clientIndex).getHand().toString();
            clientThread.sendMessage(handMessage);

            String captureMessage = "あなたの取り札: " + gameManager.getGame().getPlayers().get(clientIndex).getCaptures().toString();
            clientThread.sendMessage(captureMessage);

            int numRules = gameManager.getGame().getPlayers().get(clientIndex).chacknumberRules();
            if (numRules > 0) {
                broadcast("プレイヤー " + (clientIndex + 1) + " は " + numRules + " つの役を成立させました！");
            } else {
                broadcast("プレイヤー " + (clientIndex + 1) + " は役を成立させませんでした。");
            }
        } catch (Exception e) {
            System.err.println("プレイヤー " + (clientIndex + 1) + " への詳細情報送信中にエラーが発生しました: " + e.getMessage());
        }
    }
    public void sendGameState(ServerThread client,int clientIndex) {
    Game game = gameManager.getGame();
    int currentPlayer = gameManager.getCurrentPlayerIndex();

    // 場の札を送信
    for (Card card : game.getField().getCards()) {
        sendCardUpdate(client, card.getId(), "field");
    }

    // プレイヤーの手札を送信
    List<Card> playerHand = game.getPlayers().get(clientIndex).getHand();
    for (Card card : playerHand) {
        sendCardUpdate(client, card.getId(), "hand");
    }

    // プレイヤーの取り札を送信
    List<Card> playerCaptures = game.getPlayers().get(clientIndex).getCaptures();
    for (Card card : playerCaptures) {
        sendCardUpdate(client, card.getId(), "capture");
    }

    // 現在のターン情報を送信
    sendTurnInfo(currentPlayer);

    // ゲーム終了情報を送信
    if (gameManager.isGameFinished()) {
        sendGameOver(currentPlayer);
    }
}
public void broadcastGameState() {
    for (int i = 0; i < clients.size(); i++) {
        ServerThread client = clients.get(i);
        sendGameState(client, i);
    }
}

}
