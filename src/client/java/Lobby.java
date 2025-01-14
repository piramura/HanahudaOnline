import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Lobby extends JFrame {
    private JButton onlineButton;
    private GameClient client;

    public Lobby() {
        setTitle("Lobby");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // オンライン対戦ボタン
        onlineButton = new JButton("オンライン対戦");
        onlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startOnlineMatch();
            }
        });

        add(onlineButton, BorderLayout.CENTER);
        setVisible(true);
    }
    // オンライン対戦開始
    private void startOnlineMatch() {
        System.out.println("オンライン対戦開始");

        // ボタンを無効化
        onlineButton.setEnabled(false);
        GameController gameController = new GameController();
        //GameClient client = new GameClient("https://hanahudaonline.onrender.com");
        GameClient client = new GameClient("http://localhost:10030");
        client.setGameController(gameController);
        gameController.setGameClient(client);
        gameController.startOnlineMatch();

    }
    // ➡️ Ctrl + C で切断処理を行う
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (client != null) {
                try {
                    System.out.println("Ctrl + C 検出: 切断処理を実行中...");
                    client.disconnect();
                } catch (Exception e) {
                    System.err.println("切断処理中にエラーが発生しました: " + e.getMessage());
                }
            }
        }));
    }
    public static void main(String[] args) {
        new Lobby();
    }
}
