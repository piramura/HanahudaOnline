import java.io.*;
import javax.swing.*;
import java.util.*;
class MultiClient {
    private static JFrame clientWindow;
    private static CommClient client; // スコープ内で保持
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Connecting to Server...");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try {
            String serverHost = "localhost"; // サーバーホスト名
            int serverPort = 10030;         // サーバーポート番号
            CommClient client = CommClient.getInstance(serverHost, serverPort);

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
                mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                mainFrame.setVisible(true);

                GameController gameController = new GameController(mainFrame, client);
                gameController.startGame();
                // サーバーからのメッセージを受信するスレッドを開始
                startReceiverThread(client,gameController);
            });

            

        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
            frame.dispose(); // エラー時にウィンドウを閉じる
            e.printStackTrace();
        }
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

    private static void startReceiverThread(CommClient client, GameController gameController) {
    new Thread(() -> {
        try {
            String message;
            while ((message = client.recv()) != null) {
                handleMessage(message, gameController);
            }
        } catch (Exception e) {
            System.out.println("サーバーとの接続が切れました。");
        } finally {
            closeClient(client);
        }
    }).start();
}
private static void handleMessage(String message, GameController gameController) {
    // メッセージを改行で分割
    String[] lines = message.split("\n");
    for (String line : lines) {
        if (line.startsWith("TURN:")) {
            int currentPlayer = Integer.parseInt(message.split(":")[1]);
            gameController.setCurrentTurn(currentPlayer);
        } else if (line.startsWith("OPPONENT_HAND_COUNT:")) {
            int opponentHandCount = Integer.parseInt(message.split(":")[1]);
            gameController.updateOpponentHand(opponentHandCount);
        } else if (line.startsWith("START")) {
            gameController.startGame();
        } else if (line.startsWith("UPDATE_CARD:")) {
            handleUpdateCard(line, gameController);
        } else if (line.startsWith("PLAYER_NUMBER:")) {
            int playerNumber = Integer.parseInt(line.split(":")[1]);
            System.out.println("あなたはプレイヤー " + playerNumber + " です。");
            gameController.setPlayerID(playerNumber);
        } else if (line.equals("GAME_OVER")) {
            System.out.println("ゲームが終了しました。");
            gameController.handleGameOver();
        } else if (line.equals("FULL")) {
            System.out.println("サーバーが満員です。ゲームを開始できません。");
            closeClient(client);
            System.exit(0);
        } else {
            System.out.println("サーバーから: " + line);
        }
    }
}

private static void handleUpdateCard(String message, GameController gameController) {
    String[] parts = message.split(":");
    int cardID = Integer.parseInt(parts[1]);
    String area = parts[2];

    gameController.addCardToArea(cardID, area);

    // UI 更新
    gameController.updateUI();
}

private static List<Integer> parseCardIDs(String csv) {
    List<Integer> result = new ArrayList<>();
    try {
        String[] ids = csv.split(",");
        for (String id : ids) {
            result.add(Integer.parseInt(id.trim())); // 余計な空白をトリムしてからパース
        }
    } catch (NumberFormatException e) {
        System.err.println("カードIDのパース中にエラー: " + e.getMessage() + " | 元の入力: " + csv);
    }
    return result;
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
