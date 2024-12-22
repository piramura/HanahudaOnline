import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class CardManager {
    private List<Card> cards;

    public CardManager() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public Card getCardAtPosition(int x, int y) {
        for (Card card : cards) {
            if (isPointInsideCard(x, y, card)) {
                return card;
            }
        }
        return null;
    }

    private boolean isPointInsideCard(int x, int y, Card card) {
        Rectangle bounds = card.getBounds(); // カードの位置とサイズ
        return bounds.contains(x, y);
    }
}
