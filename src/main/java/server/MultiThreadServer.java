import java.util.ArrayList;
import java.util.List;
import java.io.IOException;


public class MultiThreadServer {
    private static List<ServerThread> clients = new ArrayList<>();//接続されたクライアントのスレッドリストを保持するためのリスト
    public static ServerManager serverManager = new ServerManager();//ゲーム全体の進行を管理するオブジェクト
    private static int clientCounter = 0;//接続されたクライアントの数を追跡するカウンター

    public static void main(String[] args) {
        System.out.println("サーバーが起動しました...");
        CommServer cs = new CommServer(10030);

        while (true) {
            // クライアント接続を受け付ける
            CommServer newClient = new CommServer(cs);  // 接続を受け入れる
            ServerThread clientThread = new ServerThread(newClient, clientCounter++);
            serverManager.addClient(newClient);  // サーバー管理者にクライアント登録
            new Thread(clientThread).start();  // スレッド開始
            System.out.println("クライアント " + (clientCounter - 1) + " が接続しました。");
        }
    }
}

class ServerThread extends Thread {
    private CommServer client;
    private int clientIndex;

    public ServerThread(CommServer client, int clientIndex) {
        this.client = client;
        this.clientIndex = clientIndex;
    }
    public int getClientIndex() {
        return clientIndex;
    }
    public void run() {        
        String message;
         // Renderのヘルスチェックリクエストを無視する
        
        while ((message = client.recv()) != null) {
             // メッセージが無視されるべきもの（null）か確認
            if (message.trim().isEmpty()) {
                continue; // 空のメッセージを無視
            }

            if (message.startsWith("HEAD /")) {
                System.out.println("ヘルスチェックリクエストを無視しました。");
                continue; // ヘルスチェックを無視
            }

            System.out.println("受信 [" + clientIndex + "]: " + message);
            MultiThreadServer.serverManager.processMessage(clientIndex, message);
            }
        
        client.close();  // 接続終了
        MultiThreadServer.serverManager.removeClient(clientIndex); // クライアント削除
    }
    public void sendMessage(String message) {
        client.send(message);  // メッセージ送信
    }
}
