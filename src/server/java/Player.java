import java.util.ArrayList;
import java.util.List;
//プレイヤーの手札と取り札を管理する。
public class Player {
    private ArrayList<Card> hand; // 手札
    private ArrayList<Card> captures; // 取り札
    private ArrayList<String> achievedRoles; // 成立した役のリスト
    private boolean isKoiKoi; // こいこいを選択しているか
    private int playerID;
    private boolean declaredEnd = false; // 「こいこい」をしないと宣言した場合のフラグ

    public Player() {
        hand = new ArrayList<>();
        captures = new ArrayList<>();
        achievedRoles = new ArrayList<>();
        isKoiKoi = false;
    }
    public boolean hasDeclaredEnd() {
        return declaredEnd;
    }
    
    public void declareEnd() {
        this.declaredEnd = true;
    }
    
    public void setPlayerID(int num){
        this.playerID = num;
    }
    public int getPlayerID(){
        return this.playerID;
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
        if (index < 0 || index >= hand.size()) {
            throw new IllegalArgumentException("不正なカードインデックス: " + index);
        }
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
    public ArrayList<String> getAchievedRoles() {
        return achievedRoles;
    }

    public void setAchievedRoles(List<String> roles) {
        this.achievedRoles = new ArrayList<>(roles);
    }

    public boolean isKoiKoi() {
        return isKoiKoi;
    }

    public void setKoiKoi(boolean koiKoi) {
        this.isKoiKoi = koiKoi;
    }
}