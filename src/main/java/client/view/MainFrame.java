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
        boardArea = new CardArea(2, 4);
        playerHandArea = new CardArea(1, 8);
        opponentHandArea = new CardArea(1, 8);

        // 場のカードエリア
        add(boardArea, BorderLayout.CENTER);

        // プレイヤーの手札エリア
        add(playerHandArea, BorderLayout.SOUTH);

        // 相手の手札エリア
        add(opponentHandArea, BorderLayout.NORTH);

    }

    public void updateBoard(List<Integer> fieldCards) {
        boardArea.updateCards(fieldCards);
    }

    public void updatePlayerHand(List<Integer> playerHand) {
        playerHandArea.updateCards(playerHand);
    }

    public void updateOpponentHand(int count) {
        opponentHandArea.clearCards();
        for (int i = 0; i < count; i++) {
            opponentHandArea.addCard(-1);
        }
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
