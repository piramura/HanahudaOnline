import java.util.ArrayList;
import java.util.List;
//プレイヤーの手札と取り札を管理する。
public class Player {
    private ArrayList<Card> hand; // 手札
    private ArrayList<Card> captures; // 取り札
    private RoleResult roleResult; // 直近の役成立情報
    private boolean isKoiKoi; // こいこいを選択しているか
    private int playerID;
    private boolean declaredEnd = false; // 「こいこい」をしないと宣言した場合のフラグ

    public Player() {
        this.hand = new ArrayList<>();
        this.captures = new ArrayList<>();
        this.roleResult = new RoleResult(playerID, new ArrayList<>(), 0);
        this.isKoiKoi = false;
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
    public Card removeCaptureCardindex(int index) {
        if (index < 0 || index >= captures.size()) {
            throw new IllegalArgumentException("不正なカードインデックス: " + index);
        }
        return captures.remove(index);
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
    public RoleResult getRoleResult() {
        return roleResult;
    }

    public void setRoleResult(RoleResult roleResult) {
        this.roleResult = roleResult;
    }
    public boolean isKoiKoi() {
        return isKoiKoi;
    }

    public void setKoiKoi(boolean koiKoi) {
        this.isKoiKoi = koiKoi;
    }
}