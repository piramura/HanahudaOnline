import java.util.ArrayList;
import java.util.Collections;
//山札を管理するクラス
//カードの初期化、シャッフル、カードを引く操作がある。
public class Deck{
	private ArrayList<Card> cards;
	public Deck() {
		//山の初期化。
        cards = new ArrayList<>();
         // 各月ごとにカードを追加
         int cardId = 0;
         //これがハードコーディングってやつか。
        //1月
        Card card0 = new Card(cardId++,Card.Month.JANUARY, Card.Point.HIKARI);
        card0.addRole("五光", 10); // 光札の役
        card0.addRole("四光", 8);
        card0.addRole("雨四光", 7);
        card0.addRole("三光", 5);
        cards.add(card0);
        Card card1 = new Card(cardId++,Card.Month.JANUARY, Card.Point.TANZAKU);
        card1.addRole("赤短", 5); // 赤短の役
        card1.addRole("タン", 1); // 短冊の役
        cards.add(card1);
        Card card2 = new Card(cardId++,Card.Month.JANUARY, Card.Point.KASU);
        card2.addRole("カス", 1); // カス札
        cards.add(card2);
        Card card3 = new Card(cardId++,Card.Month.JANUARY, Card.Point.KASU);
        card3.addRole("カス", 1); // カス札
        cards.add(card3);

        //2月
        Card card4 = new Card(cardId++,Card.Month.FEBRUARY, Card.Point.TANE);
        card4.addRole("タネ", 1); 
        cards.add(card4);
        Card card5 = new Card(cardId++,Card.Month.FEBRUARY, Card.Point.TANZAKU);
        card5.addRole("赤短", 5);
        card5.addRole("タン", 1); 
        cards.add(card5);
        Card card6 = new Card(cardId++,Card.Month.FEBRUARY, Card.Point.KASU);
        card6.addRole("カス", 1); // カス札
        cards.add(card6);
        Card card7 = new Card(cardId++,Card.Month.FEBRUARY, Card.Point.KASU);
        card7.addRole("カス", 1); // カス札
        cards.add(card7);

        //3月
        Card card8 = new Card(cardId++,Card.Month.MARCH, Card.Point.HIKARI);
        card8.addRole("五光", 10); // 光札の役
        card8.addRole("花見で一杯", 5); // 光札の役
        card8.addRole("四光", 8);
        card8.addRole("雨四光", 7);
        card8.addRole("三光", 5);
        cards.add(card8);
        Card card9 = new Card(cardId++,Card.Month.MARCH, Card.Point.TANZAKU);
        card9.addRole("赤短", 5);
        card9.addRole("タン", 1); 
        cards.add(card9);
        Card card10 = new Card(cardId++,Card.Month.MARCH, Card.Point.KASU);
        card10.addRole("カス", 1); // カス札
        cards.add(card10);
        Card card11 = new Card(cardId++,Card.Month.MARCH, Card.Point.KASU);
        card11.addRole("カス", 1); // カス札
        cards.add(card11);

        //4月
        Card card12 = new Card(cardId++,Card.Month.APRIL, Card.Point.TANE);
        card12.addRole("タネ", 1); 
        cards.add(card12);
        Card card13 = new Card(cardId++,Card.Month.APRIL, Card.Point.TANZAKU);
        card13.addRole("タン", 1); 
        cards.add(card13);
        Card card14 = new Card(cardId++,Card.Month.APRIL, Card.Point.KASU);
        card14.addRole("カス", 1); // カス札
        cards.add(card14);
        Card card15 = new Card(cardId++,Card.Month.APRIL, Card.Point.KASU);
        card15.addRole("カス", 1); // カス札
        cards.add(card15);

        //5月
        Card card16 = new Card(cardId++,Card.Month.MAY, Card.Point.TANE);
        card16.addRole("タネ", 1); 
        cards.add(card16);
        Card card17 = new Card(cardId++,Card.Month.MAY, Card.Point.TANZAKU);
        card17.addRole("タン", 1); 
        cards.add(card17);
        Card card18 = new Card(cardId++,Card.Month.MAY, Card.Point.KASU);
        card18.addRole("カス", 1); // カス札
        cards.add(card18);
        Card card19 = new Card(cardId++,Card.Month.MAY, Card.Point.KASU);
        card19.addRole("カス", 1); // カス札
        cards.add(card19);

        //6
        Card card20 = new Card(cardId++,Card.Month.JUNE, Card.Point.TANE);
        card20.addRole("猪鹿蝶", 5); 
        card20.addRole("タネ", 1); 
        cards.add(card20);
        Card card21 = new Card(cardId++,Card.Month.JUNE, Card.Point.TANZAKU);
        card21.addRole("青短", 1); 
        card21.addRole("タン", 1); 
        cards.add(card21);
        Card card22 = new Card(cardId++,Card.Month.JUNE, Card.Point.KASU);
        card22.addRole("カス", 1); // カス札
        cards.add(card22);
        Card card23 = new Card(cardId++,Card.Month.JUNE, Card.Point.KASU);
        card23.addRole("カス", 1); // カス札
        cards.add(card23);
        //7
        Card card24 = new Card(cardId++,Card.Month.JULY, Card.Point.TANE);
        card24.addRole("猪鹿蝶", 5); 
        card24.addRole("タネ", 1); 
        cards.add(card24);
        Card card25 = new Card(cardId++,Card.Month.JULY, Card.Point.TANZAKU);
        card25.addRole("タン", 1); 
        cards.add(card25);
        Card card26 = new Card(cardId++,Card.Month.JULY, Card.Point.KASU);
        card26.addRole("カス", 1); // カス札
        cards.add(card26);
        Card card27 = new Card(cardId++,Card.Month.JULY, Card.Point.KASU);
        card27.addRole("カス", 1); // カス札
        cards.add(card27);

        //8
        Card card28 = new Card(cardId++,Card.Month.AUGUST, Card.Point.HIKARI);
        card28.addRole("五光", 10); // 光札の役
        card28.addRole("月見で一杯", 10); // 光札の役
        card28.addRole("四光", 8);
        card28.addRole("雨四光", 7);
        card28.addRole("三光", 5);
        cards.add(card28);
        Card card29 = new Card(cardId++,Card.Month.AUGUST, Card.Point.TANE);
        card29.addRole("タネ", 1); 
        cards.add(card29);
        Card card30 = new Card(cardId++,Card.Month.AUGUST, Card.Point.KASU);
        card30.addRole("カス", 1); // カス札
        cards.add(card30);
        Card card31 = new Card(cardId++,Card.Month.AUGUST, Card.Point.KASU);
        card31.addRole("カス", 1); // カス札
        cards.add(card31);

        //9
        Card card32 = new Card(cardId++,Card.Month.SEPTEMBER, Card.Point.HIKARI);
        card32.addRole("五光", 10); // 光札の役
        card32.addRole("月見で一杯", 5);
        card32.addRole("花見で一杯", 5);
        card32.addRole("四光", 8);
        card32.addRole("雨四光", 7);
        cards.add(card32);
        Card card33 = new Card(cardId++,Card.Month.SEPTEMBER, Card.Point.TANE);
        card33.addRole("青短", 5); 
        card33.addRole("タン", 1);
        cards.add(card33);
        Card card34 = new Card(cardId++,Card.Month.SEPTEMBER, Card.Point.KASU);
        card34.addRole("カス", 1); // カス札
        cards.add(card34);
        Card card35 = new Card(cardId++,Card.Month.SEPTEMBER, Card.Point.KASU);
        card35.addRole("カス", 1); // カス札
        cards.add(card35);

        //10
        Card card36 = new Card(cardId++,Card.Month.OCTOBER, Card.Point.TANE);
        card36.addRole("猪鹿蝶", 5); 
        card36.addRole("タネ", 1); 
        cards.add(card36);
        Card card37 = new Card(cardId++,Card.Month.OCTOBER, Card.Point.TANZAKU);
        card37.addRole("青短", 5); 
        card37.addRole("タン", 1); 
        cards.add(card37);
        Card card38 = new Card(cardId++,Card.Month.OCTOBER, Card.Point.KASU);
        card38.addRole("カス", 1); // カス札
        cards.add(card38);
        Card card39 = new Card(cardId++,Card.Month.OCTOBER, Card.Point.KASU);
        card39.addRole("カス", 1); // カス札
        cards.add(card39);

        //11
        Card card40 = new Card(cardId++,Card.Month.NOVEMBER, Card.Point.HIKARI);
        card40.addRole("五光", 10); // 光札の役
        card40.addRole("雨四光", 7);
        cards.add(card40);
        Card card41 = new Card(cardId++,Card.Month.NOVEMBER, Card.Point.TANE);
        card41.addRole("タネ", 1); 
        cards.add(card41);
        Card card42 = new Card(cardId++,Card.Month.NOVEMBER, Card.Point.TANZAKU);
        card42.addRole("タン", 1); 
        cards.add(card42);
        Card card43 = new Card(cardId++,Card.Month.NOVEMBER, Card.Point.KASU);
        card43.addRole("カス", 1); // カス札
        cards.add(card43);

        //12
        Card card44 = new Card(cardId++,Card.Month.DECEMBER, Card.Point.HIKARI);
        card44.addRole("五光", 10); // 光札の役
        card44.addRole("四光", 8);
        card44.addRole("雨四光", 7);
        cards.add(card44);
        Card card45 = new Card(cardId++,Card.Month.DECEMBER, Card.Point.KASU);
        card45.addRole("カス", 1); // カス札
        cards.add(card45);
        Card card46 = new Card(cardId++,Card.Month.DECEMBER, Card.Point.KASU);
        card46.addRole("カス", 1); // カス札
        cards.add(card46);
        Card card47 = new Card(cardId++,Card.Month.DECEMBER, Card.Point.KASU);
        card47.addRole("カス", 1); // カス札
        cards.add(card47);
        

    }
	public void shuffle() {
        Collections.shuffle(cards);//Collectionsのシャッフル機能をつかう。
    }

    public Card draw() {
		//引く
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        return null; // 山札が空の場合
    }

    //Testように追加
    public ArrayList<Card> getCards() {
        return cards;
    }
}