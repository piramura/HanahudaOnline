import java.util.ArrayList;
//場札を管理する。場に出たカードや場札から取ったカードを操作する。
public class Field {
    private ArrayList<Card> cards; // 場にあるカード

    public Field() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
    public ArrayList<Card> takeCardsByMonth(Card.Month month) {
        ArrayList<Card> takenCards = new ArrayList<>();
        
        // cards リストをループして一致する月のカードを取得・削除
        cards.removeIf(card -> {
            if (card.getMonth() == month) {
                takenCards.add(card);
                return true; // 一致したカードを削除
            }
            return false; // 削除しない
        });

        return takenCards;
    }
    @Override
    public String toString() {
        String rolesString = "";
        for (int i = 0; i < cards.size(); i++) {
            rolesString += cards.get(i).toString();
            if (i < cards.size() - 1) {
                rolesString += ", "; // 最後のカード以外にはカンマとスペースを追加
            }
        }
        return "場札: " + rolesString;
    }
}
