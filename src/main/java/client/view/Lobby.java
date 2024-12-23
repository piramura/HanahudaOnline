import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Lobby extends JFrame {
    private JButton onlineButton;

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

    // MultiClientを別スレッドで実行
    new Thread(() -> {
        try {
            GameClient.main(new String[0]); // MultiClientのメインメソッドを呼び出す
        } catch (Exception ex) {
            ex.printStackTrace();
            // エラーが発生した場合、ボタンを再度有効化
            SwingUtilities.invokeLater(() -> onlineButton.setEnabled(true));
        }
    }).start();
}

    public static void main(String[] args) {
        new Lobby();
    }
}
