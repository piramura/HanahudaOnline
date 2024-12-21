import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class MultiThreadServer {
    private static List<ServerThread> clients = new ArrayList<>(); // 接続されたクライアントのスレッドリストを保持するためのリスト
    public static ServerManager serverManager = new ServerManager(); // ゲーム全体の進行を管理するオブジェクト
    private static int clientCounter = 0; // 接続されたクライアントの数を追跡するカウンター

    public static void main(String[] args) {
        MultiThreadServer server = new MultiThreadServer(); // インスタンスを作成
        server.start(); // インスタンスメソッドを呼び出し
    }
public void start() {
    System.out.println("サーバーが起動しました...");

    // シャットダウンフックを追加
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("サーバーをシャットダウンしています...");
        serverManager.shutdownAllClients(); // ServerManager に全クライアントの接続終了を依頼
        System.out.println("サーバーが正常に終了しました。");
    }));

    try {
        CommServer cs = new CommServer(10030);
        while (true) {
            try {
                CommServer newClient = cs.acceptClient();

                if (newClient== null) {
                   System.err.println("接続失敗: クライアントをスキップします。");
                    continue;
                }
                ServerThread clientThread = new ServerThread(this, newClient, clientCounter++);
                if(!clientThread.verifyConnection()){
                    newClient.close();
                    continue;
                }
                serverManager.addClient(clientThread);
                clients.add(clientThread);
                new Thread(clientThread).start();
                System.out.println("クライアント " + (clientCounter - 1) + " が接続しました。");
            } catch (Exception e) {
                System.err.println("クライアント処理中にエラーが発生: " + e.getMessage());
            }
        }
    } catch (Exception e) {
        System.err.println("サーバー起動中にエラーが発生: " + e.getMessage());
    }
}
}

class ServerThread extends Thread {
    private CommServer client;
    private int clientIndex;
    private MultiThreadServer server; // MultiThreadServer の参照を保持

    public ServerThread(MultiThreadServer server, CommServer client, int clientIndex) {
        this.server = server;
        this.client = client;
        this.clientIndex = clientIndex;
    }

    public int getClientIndex() {
        return clientIndex;
    }
    public CommServer getClient() {
        return client;
    }
    public boolean verifyConnection() {
        try {
            client.send("HELLO");
            String response = client.recv();
            return "HELLO".equals(response);
        } catch (Exception e) {
            System.err.println("接続確認中にエラー: " + e.getMessage());
            return false;
        }
    }

    public void run() {
        try {
            String message;
            while ((message = client.recv()) != null) {
                if (message.trim().isEmpty()) continue;
                System.out.println("受信 [" + clientIndex + "]: " + message);
                MultiThreadServer.serverManager.processMessage(this, message);
            }
        } catch (Exception e) {
            System.err.println("スレッド実行中にエラーが発生しました: " + e.getMessage());
        } finally {
            System.out.println("クライアント " + clientIndex + " をクローズします。");
            MultiThreadServer.serverManager.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        client.send(message); // メッセージ送信
    }
    public void close() {
        client.close();
    }
}
