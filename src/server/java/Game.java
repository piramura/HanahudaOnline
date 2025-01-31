import java.util.List;
import java.util.ArrayList;
//ゲーム全体の進行を管理する。配牌、ターン進行、得点計算を行う。
public class Game {
    private Deck deck;
    private List<Player> players;
    private Field field;
    private int currentPlayerIndex;
    private int playCount;
    private int currentTurn;
    private boolean isEnd;

    public Game(Deck deck) {
        this.deck = deck;
        this.field = new Field();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentTurn = 0;//初期化するときに0か1かランダム
        this.playCount = 0;
        this.isEnd = false;
    }
    // 初期化メソッド
    public void initializeGame(HanahudaGameLogic logic) {
        shuffleDeck();
        setupPlayers();
        setupField();
    }
    private void shuffleDeck() {
        deck.shuffle();
    }

    private void setupPlayers() {
        for (int i = 0; i < 2; i++) {
            Player player = new Player();
            players.add(player);
            for (int j = 0; j < 8; j++) {
                player.addCardToHand(deck.draw());
            }
        }
    }

    private void setupField() {
        while (true) {
            field.clear();
            for (int i = 0; i < 8; i++) {
                field.addCard(deck.draw());
            }
            if (!hasFourSameMonthCards(field.getCards())) {
                break;
            }
            deck.shuffle();
        }
    }

    private boolean hasFourSameMonthCards(List<Card> cards) {
        int[] monthCounts = new int[12];
        for (Card card : cards) {
            int month = (card.getId() - 1) / 4;
            monthCounts[month]++;
            if (monthCounts[month] >= 4) {
                return true;
            }
        }
        return false;
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
    public List<Player> getPlayers() {
        return players;
    }
     //現在のターン
    public int getCurrentTurn() {
        return currentTurn;
    }
   
    public void setCurrentTurn(int index) {
        this.currentTurn = index;
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void resetPlayCount() {
        this.playCount = 0;
    }

    public void incrementPlayCount() {
        this.playCount++;
    }
    public boolean getIsEnd(){
        return isEnd;
    }
    public void setIsEnd(boolean isEnd){
        this.isEnd = isEnd;
    }
    //両方の手札がからならtrueを返す関数。
    public boolean isBothHandEmpty(){
        return players.get(0).getHand().isEmpty() && players.get(1).getHand().isEmpty();
    }
    public void nextTurn() {
        if (isBothHandEmpty()) {
            endGame();
        } else {
            playCount = 0; // プレイ回数をリセット
            System.out.println("次のターンへ。現在のプレイヤー: " + currentTurn);
            currentTurn++;
            currentPlayerIndex = currentTurn % 2 + 1; // 次のプレイヤーに切り替え
            System.out.println("次のターンへ。現在のプレイヤー: " + currentPlayerIndex);
        }
    }
    
    private void endGame() {
        System.out.println("ゲーム終了！勝者を判定します...");
        // ゲーム終了処理を記述（勝者の判定やリセット処理など）
    }
    // デッキの中身を出力する関数
    public void printDeckContents() {
        System.out.println("現在のデッキの中身:");
        List<Card> cards = deck.getCards(); // デッキ内のカードを取得
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            System.out.println("カード " + (i + 1) + ": ID=" + card.getId() + ", 月=" + card.getMonth());
        }
    }
    // 全プレイヤーの手札を出力する関数
    public void printPlayerHands() {
        System.out.println("現在の全プレイヤーの手札:");

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            System.out.println("プレイヤー " + (i + 1) + " の手札:");
            
            List<Card> hand = player.getHand();
            if (hand.isEmpty()) {
                System.out.println("手札は空です。");
            } else {
                for (int j = 0; j < hand.size(); j++) {
                    Card card = hand.get(j);
                    System.out.println("  カード " + (j + 1) + ": ID=" + card.getId() + ", 月=" + card.getMonth());
                }
            }
        }
    }


}