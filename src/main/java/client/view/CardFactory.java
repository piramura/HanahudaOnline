import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class CardFactory {
    //カードを生成する
    public Card createCard(int id, String name, boolean isFaceDown) {
        String imagePath = "images/" + id + ".png";
        BufferedImage image = convertToBufferedImage(new ImageIcon(imagePath));
        return new Card(id, name, isFaceDown, image);
    }

    public Card createCard(int id) {
        String name = "Card " + id;
        boolean isFaceDown = id < 0;
        return createCard(id, name, isFaceDown);
    }

    private BufferedImage convertToBufferedImage(ImageIcon icon) {
        Image img = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

}
