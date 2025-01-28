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
    // id を使って特定のカードを取得
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
    public void processPlayedCard(Player player, Card playedCard,int fieldCardId) {
        // fieldCardId からカードを取得
        if(fieldCardId == -1){
            // 同じ月のカードがなければ場に追加
            this.addCard(playedCard);
            System.out.println("場に残したカード: " + playedCard);
        }else{
            Card fieldCard = this.getCardById(fieldCardId);
            if (!isThreeCards(fieldCard)) {
                // 3枚未満の場合、普通にそのカードを取る
                player.captureCard(fieldCard);
                player.captureCard(playedCard);
    
                System.out.println("場札から取ったカード: " + fieldCard);
            } else {
                // 3枚同じ月のカードがある場合、すべて取る
                ArrayList<Card> takenCards = takeCardsByMonth(fieldCard.getMonth());
                for (Card card : takenCards) {
                    player.captureCard(card);
                }
                player.captureCard(playedCard);

                System.out.println("場札から取ったカード(3枚以上): " + takenCards);
            }
        }
    }
    private boolean isThreeCards(Card card){
        int count = 0;
        for (Card c : cards) {
            if (c.getMonth() == card.getMonth()) {
                count++;
            }
        }
        return count == 3; // 3枚の場合に true を返す
    }
    //後からここも選べるようにする？？
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

    //場から引いた時どっちを選ぶかも、プレイヤーに委ねる？これは後で？？？？
    //
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
