    import javax.swing.*;
    import java.awt.*;
    import java.util.List;
    import java.awt.event.*;

    public class MainFrame extends JFrame {
        private CardArea boardArea;       // 場のカード
        private CardArea playerHandArea; // プレイヤーの手札
        private CardArea opponentHandArea; // 相手の手札
        private JFrame turnFrame;
        private JLabel turnLabel;

        public MainFrame() {
            // 初期化コード
            turnFrame = new JFrame("ターン情報");
            turnLabel = new JLabel("ターン: プレイヤー 1");
            turnFrame.add(turnLabel, BorderLayout.CENTER);
            turnFrame.pack();
            turnFrame.setVisible(true);
            setTitle("Hanafuda Game");
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 自動でウィンドウを閉じない
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
        // ターン情報を更新
    public void updateTurnLabel(int currentPlayer) {
        SwingUtilities.invokeLater(() -> {
        turnLabel.setText("ターン: プレイヤー " + (currentPlayer + 1));
    });
    }
        private void handleWindowClose() {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "ゲームを終了しますか？\n（サーバーとの接続も切断されます）",
                "終了確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("ウィンドウを閉じています...");
            // 正常終了
            dispose(); // ウィンドウを閉じる
            System.exit(0); // プログラム終了
        } else if (confirm == JOptionPane.NO_OPTION) {
            System.out.println("ユーザーが終了をキャンセルしました。");
            // 何も処理を行わない
        } else {
            System.out.println("予期しない選択肢が選ばれました。");
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
            CardFactory cardFactory = new CardFactory();
            for (int i = 0; i < cardCount; i++) {
                opponentHandArea.addCard(cardFactory.createCard(-1, "Hidden", true)); // 裏向きカード
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
