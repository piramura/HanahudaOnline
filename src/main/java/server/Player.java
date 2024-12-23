import java.util.ArrayList;
//プレイヤーの手札と取り札を管理する。
public class Player {
    private ArrayList<Card> hand; // 手札
    private ArrayList<Card> captures; // 取り札
    private int numRules;
    private int playerID;

    public Player() {
        hand = new ArrayList<>();
        captures = new ArrayList<>();
    }
    
    public int chacknumberRules(){
        return numRules;
    }
    public void setPlayerID(int num){
        this.playerID = num;
    }
    public int getPlayerID(){
        return this.playerID;
    }
    public void setnumberRules(int num){
        numRules = num;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
        //手札に加える関数。山から配るときしか使わない想定。
        //花札は減る一方のはず。
    }

    public void captureCard(Card card) {
        captures.add(card);
        //カードを場からとる関数。
    }

    public Card playCard(int index) {
        return hand.remove(index);
        //手札からカードを出すときに使う。indexで管理することで選べるようにしようとした。
    }

    public ArrayList<Card> getHand() {
        return hand;
        //手札を取得したいときに使う。
    }

    public ArrayList<Card> getCaptures() {
        return captures;
        //取り札を取得したいときに使う。
    }
}