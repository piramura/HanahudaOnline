import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private CardArea boardArea;       // 場のカード
    private CardArea playerHandArea; // プレイヤーの手札
    private CardArea opponentHandArea; // 相手の手札

    public MainFrame() {
        setTitle("Hanafuda Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClose(); // ウィンドウ閉じる処理を呼び出す
            }
        });
        // 各エリアを初期化
        boardArea = new CardArea(2, 4);
        playerHandArea = new CardArea(1, 8);
        opponentHandArea = new CardArea(1, 8);

        // UIに追加
        add(opponentHandArea, BorderLayout.NORTH);
        add(boardArea, BorderLayout.CENTER);
        add(playerHandArea, BorderLayout.SOUTH);

    }
    private void handleWindowClose() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "ゲームを終了しますか？\n（サーバーとの接続も切断されます）",
            "終了確認",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // ウィンドウを閉じる
            System.exit(0); // プログラム終了
        }
    }
    // UI更新用のメソッド
    public void updateBoard(List<Card> boardCards) {
        boardArea.updateCards(boardCards);
    }

    public void updatePlayerHand(List<Card> playerHand) {
        playerHandArea.updateCards(playerHand);
    }

    public void updateOpponentHand(int cardCount) {
        opponentHandArea.clearCards();
        for (int i = 0; i < cardCount; i++) {
            opponentHandArea.addCard(new Card(-1, "Hidden", true)); // 裏向きカード
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
    public CardArea getFieldArea() {
    return boardArea; // 場のカードエリア
}

public CardArea getPlayerHandArea() {
    return playerHandArea; // プレイヤー手札エリア
}

public CardArea getOpponentHandArea() {
    return opponentHandArea; // 相手手札エリア
}

// プレイヤー取り札エリア（後で必要なら追加）
public CardArea getPlayerCaptureArea() {
    // 必要に応じてエリアを追加し、管理してください
    return new CardArea(2, 4);
}


}
