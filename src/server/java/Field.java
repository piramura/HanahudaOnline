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

    public void clear() {
        cards.clear(); // cards リストをクリア
    }
    public ArrayList<Card> getCards() {
        return cards;
    }
    // id を使って特定のカードを取得これいる？？
    public Card getCardById(int id) {
        for (Card card : cards) {
            if (card.getId() == id) {
                return card; // id が一致するカードを返す
            }
        }
        throw new IllegalArgumentException("指定されたカードIDが場札に存在しません: " + id);
    }
    // id を使って特定のカードを場から削除
    public boolean removeCardById(int id) {
        return cards.removeIf(card -> card.getId() == id); // id が一致するカードを削除
    }
    // id を使って特定のカードを取得して削除
    public Card takeCardById(int id) {
        Card card = getCardById(id);// id に対応するカードを取得
        removeCard(card);           // 場から削除
        return card;                // 取得したカードを返す
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
        
    
    public boolean isThreeCards(Card card){
        int count = 0;
        for (Card c : cards) {
            if (c.getMonth() == card.getMonth()) {
                count++;
            }
        }
        return count == 3; // 2枚の場合に true を返す一枚はすでに取ってるから
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
}
