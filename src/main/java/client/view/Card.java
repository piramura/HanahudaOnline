import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Card extends JButton {
    private int id;
    private String name;
    private boolean isFaceDown;
    private BufferedImage image;

    public Card(int id, String name, boolean isFaceDown, BufferedImage image) {
        this.id = id;
        this.name = name;
        this.isFaceDown = isFaceDown;
        this.image = image;

        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setFocusPainted(false); // フォーカス時の枠線を非表示

        // ボタンのテキストは非表示（画像で覆われるため）
        setText(null);

        // ボタンのクリックイベント
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClick();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isFaceDown) {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight()); // 裏面は灰色で塗りつぶす
        } else if (image != null) {
            g.drawImage(image, 0, 0, this); // 表面の画像を描画
        }
    }

    private void handleClick() {
        System.out.println("カードがクリックされました: ID=" + id + ", 表向き=" + !isFaceDown);
    }

    // Getter メソッド
    public int getId() {
        return id;
    }

    public boolean isFaceDown() {
        return isFaceDown;
    }

    public void setFaceDown(boolean faceDown) {
        isFaceDown = faceDown;
        repaint(); // 再描画
    }
}
