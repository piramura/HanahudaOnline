import java.io.*;
import javax.swing.*;
class MultiClient {
    private static JFrame clientWindow;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Connecting to Server...");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // try {
            String serverHost = "localhost"; // サーバーホスト名
            int serverPort = 10030;         // サーバーポート番号
            CommClient client = new CommClient(serverHost, serverPort);

            // サーバーとの接続を確認
            if (!verifyConnection(client)) {
                System.err.println("接続を終了します。");
                frame.dispose(); // ウィンドウを閉じる
                return; // サーバーが満員または接続失敗の場合に終了
            }

            // サーバー接続成功時にメインウィンドウを生成
            SwingUtilities.invokeLater(() -> {
                frame.dispose(); // 接続確認用のウィンドウを閉じる
                MainFrame mainFrame = new MainFrame();
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setVisible(true);

                GameController gameController = new GameController(mainFrame, client);
                gameController.startGame();
            });

            // サーバーからのメッセージを受信するスレッドを開始
            startReceiverThread(client);

        // } catch (IOException e) {
        //     System.err.println("エラー: " + e.getMessage());
        //     frame.dispose(); // エラー時にウィンドウを閉じる
        //     e.printStackTrace();
        // }
    });
    }
    private static void closeClient(CommClient client) {
        try {
            if (clientWindow != null) {
                SwingUtilities.invokeLater(() -> clientWindow.dispose()); // ウィンドウを閉じる
            }
            client.close();
            System.out.println("クライアント接続を終了しました。");
        } catch (Exception e) {
            System.err.println("クライアント終了中にエラー: " + e.getMessage());
        }
    }

    // サーバーとの接続確認
    private static boolean verifyConnection(CommClient client) {
        try {
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                client.send("HELLO");
                System.out.println("HELLO を送信しました。応答待機中...");
                String response = client.recv();
                if ("HELLO".equals(response)) {
                    System.out.println("サーバーから正しい応答を受信しました。");
                    return true;
                }else if ("FULL".equals(response)) {
                System.err.println("サーバーは満員です。接続を終了します。");
                client.close();
                return false; // 満員で終了
            }
                System.out.println("サーバーからの応答待機中... (" + (i + 1) + "/" + retries + ")");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("接続確認中にエラーが発生しました: " + e.getMessage());
        }
        return false;
    }

    // サーバーからのメッセージを受信するスレッド
    private static void startReceiverThread(CommClient client) {
        new Thread(() -> {
            try {
                String message;
                while ((message = client.recv()) != null) {
                    System.out.println("サーバーから: " + message);
                    handleServerMessage(message,client);
                }
            } catch (Exception e) {
                System.out.println("サーバーとの接続が切れました。");
            } finally {
            closeClient(client); // 接続が切れたらウィンドウも閉じる
        }
        }).start();
    }

    // サーバーからのメッセージを処理
    private static void handleServerMessage(String message,CommClient client) {
        if (message.equals("DISCONNECT")) {
        System.out.println("サーバーから切断されました。");
        closeClient(client); // ウィンドウを閉じる処理を呼び出す
    } else if (message.startsWith("あなたの手札: ")) {
            System.out.println("手札更新: " + message.substring(8));
        } else if (message.startsWith("あなたの取り札: ")) {
            System.out.println("取り札更新: " + message.substring(8));
        }
    }

    // ユーザー入力をサーバーに送信
    private static void sendUserInputToServer(CommClient client) throws IOException {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            String msg;
            while ((msg = input.readLine()) != null) {
                if (msg.trim().isEmpty()) {
                    System.out.println("空のメッセージは送信されません。");
                    continue;
                }
                client.send(msg);
            }
        }
    }
}
