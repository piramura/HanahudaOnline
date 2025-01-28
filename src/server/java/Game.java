import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//ゲーム全体の進行を管理する。配牌、ターン進行、得点計算を行う。
public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private Field field;
    private int nowTurn;
    private HanahudaGameLogic logic; // ロジックをフィールドとして保持
    private Map<Integer, Boolean> koiKoiStates; // プレイヤーIDと「こいこい」状態のマップ

    public Game(Deck deck,HanahudaGameLogic logic) {
        this.deck = deck;
        this.logic = logic;
        this.players = new ArrayList<>(); // プレイヤーリストを初期化
        this.field = new Field();
        this.koiKoiStates = new HashMap<>();
    }
    //両方の手札がからならtrueを返す関数。
    public boolean isBothHandEmpty(){
        return players.get(0).getHand().isEmpty() && players.get(1).getHand().isEmpty();
    }
    // プレイヤーが「こいこい」を選択
    public void setKoiKoi(int playerId, boolean isKoiKoi) {
        koiKoiStates.put(playerId, isKoiKoi);
    }

    // プレイヤーの「こいこい」状態を取得
    public boolean isKoiKoi(int playerId) {
        return koiKoiStates.getOrDefault(playerId, false);
    }

    // 「こいこい」状態を解除これ多分いらない。
    public void clearKoiKoi(int playerId) {
        koiKoiStates.remove(playerId);
    }

    // 全プレイヤーの「こいこい」状態を確認
    public Map<Integer, Boolean> getAllKoiKoiStates() {
        return new HashMap<>(koiKoiStates); // マップのコピーを返す
    }

    //プレイヤーを追加する。
    public void addPlayer(Player player) {
        players.add(player);
    }

    //山を返す。
    public Deck getDeck() {
        return deck;
    }

    //場を返す。
    public Field getField() {
        return field;
    }

    //プレイヤーを返す。
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public void setNowTurn(int turnNum){
        this.nowTurn = turnNum;
    }
    public int getNowTurn(){
        return nowTurn;
    }

}