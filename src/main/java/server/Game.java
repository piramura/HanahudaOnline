import java.util.ArrayList;
//ゲーム全体の進行を管理する。配牌、ターン進行、得点計算を行う。
public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private Field field;
    private int nowTurn;
    private HanahudaGameLogic logic; // ロジックをフィールドとして保持

    public Game(Deck deck,HanahudaGameLogic logic) {
        this.deck = deck;
        this.logic = logic;
        this.players = new ArrayList<>(); // プレイヤーリストを初期化
        this.field = new Field();
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