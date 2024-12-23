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
    public void processPlayedCard(Player player, Card playedCard) {
        // 場に同じ月のカードがあれば取る
        ArrayList<Card> takenCards = this.takeCardsByMonth(playedCard.getMonth());
        if (!takenCards.isEmpty()) {
            Card takenCard = takenCards.remove(0);
            player.captureCard(takenCard);
            player.captureCard(playedCard);

            // 残りのカードを場に戻す
            for (Card remainingCard : takenCards) {
                this.addCard(remainingCard);
            }

            System.out.println("場札から取ったカード: " + takenCard);
        } else {
            // 同じ月のカードがなければ場に追加
            this.addCard(playedCard);
            System.out.println("場に残したカード: " + playedCard);
        }
    }

    public void processDrawnCard(Player player, Card drawnCard) {
        // 山札から引いたカードを場に出して処理
        ArrayList<Card> drawnTakenCards = this.takeCardsByMonth(drawnCard.getMonth());
        if (!drawnTakenCards.isEmpty()) {
            Card drawnTakenCard = drawnTakenCards.remove(0);
            player.captureCard(drawnTakenCard);
            player.captureCard(drawnCard);

            // 残りのカードを場に戻す
            for (Card remainingCard : drawnTakenCards) {
                this.addCard(remainingCard);
            }

            System.out.println("山札から引いたカードで場札を取ったカード: " + drawnTakenCard);
        } else {
            // 場に追加
            this.addCard(drawnCard);
            System.out.println("山札から引いたカードを場に残した: " + drawnCard);
        }
    }
}
