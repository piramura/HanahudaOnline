import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final CardArea boardArea;
    private final CardArea playerHandArea;
    private final CardArea opponentHandArea;
    private JPanel turnPanel;

    public MainFrame(GameController controller) {
        setTitle("Hanafuda Game");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UIパーツの初期化
        boardArea = new CardArea(2, 4,controller);
        playerHandArea = new CardArea(1, 8,controller);
        opponentHandArea = new CardArea(1, 8,controller);

        // 場のカードエリア
        add(boardArea, BorderLayout.CENTER);

        // プレイヤーの手札エリア
        add(playerHandArea, BorderLayout.SOUTH);

        // 相手の手札エリア
        add(opponentHandArea, BorderLayout.NORTH);
        // ➡️ 「ゲーム終了」ボタンの追加
        JButton exitButton = new JButton("ゲーム終了");
        exitButton.addActionListener(e -> {
            try {
                controller.getGameClient().disconnect();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "切断中にエラーが発生しました: " + ex.getMessage());
            }
        });
        add(exitButton, BorderLayout.EAST);
    }

    public void updateBoard(List<Integer> fieldCards) {
        // Nullチェックを追加
        if (fieldCards == null) {
            System.err.println("fieldCardsが null です。");
            return;
        }
        boardArea.clearCards(); // 既存のカードを削除
        // ボードエリアをリセットして更新
        SwingUtilities.invokeLater(() -> {
            for (int cardId : fieldCards) {
                boardArea.addCard(cardId); // 新しいカードを追加
            }
        });
    }


    public void updatePlayerHand(List<Integer> playerHand) {
        // Nullチェックを追加
        if (playerHand == null) {
            System.err.println("プレイヤーの手札が null です。");
            return;
        }
        playerHandArea.clearCards(); // 既存のカードを削除
        SwingUtilities.invokeLater(() -> {
            for (int cardId : playerHand) {
                playerHandArea.addCard(cardId); // 新しいカードを追加
            }
        });
    }

    public void updateOpponentHand(int count) {
        
        opponentHandArea.clearCards();
        SwingUtilities.invokeLater(() -> {
        
        for (int i = 0; i < count; i++) {
            opponentHandArea.addCard(-1);
        }
           });
    }
    public void updadateTurn(int turn){
        
    }
    public void showDisconnectionMessage(int disconnectedPlayer) {
        JOptionPane.showMessageDialog(
            this,
            "プレイヤー " + disconnectedPlayer + " が切断されました。",
            "切断通知",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
