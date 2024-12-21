import java.util.ArrayList;
//ゲーム全体の進行を管理する。配牌、ターン進行、得点計算を行う。
public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private Field field;
    private int nowTurn;


    public int getNowTurn(){
        return nowTurn;
    }

    public Game() {
        deck = new Deck();
        players = new ArrayList<>();// 複数のプレイヤーを管理するリスト
        field = new Field();
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
    public void reset() {
        deck = new Deck();  // 新しい山札
        players.clear();    // プレイヤーリストのクリア
        field = new Field();  // 場のリセット
        nowTurn = 0;  // ターンカウンターのリセット
        System.out.println("ゲーム内部の状態がリセットされました。");
    }


    //各ターンの処理
    public void playTurn(Player player, Card playedCard) {
        System.out.println("プレイヤーが場に出したカード: " + playedCard);

        // 1. 手札からカードを削除
        player.getHand().remove(playedCard);
        // 2. 場に同じ月のカードがあれば取る
        ArrayList<Card> takenCards = field.takeCardsByMonth(playedCard.getMonth());
        if (!takenCards.isEmpty()) {
            // 1枚だけ取る
            Card takenCard = takenCards.remove(0); // 最初のカードを取得
            player.captureCard(takenCard);
            player.captureCard(playedCard); // 自分のカードも取り札に加える

            // 残りのカードを場に戻す
            for (Card remainingCard : takenCards) {
                field.addCard(remainingCard);
            }

            System.out.println("場札から取ったカード: " + takenCard);
        } else {
            // 同じ月のカードがなければ、場札に追加
            field.addCard(playedCard);
            System.out.println("場に残したカード: " + playedCard);
        }

        // 3. 山札から1枚引く
        Card drawnCard = deck.draw();
        if (drawnCard != null) {
            System.out.println("山札から引いたカード: " + drawnCard);

            // 4. 山札から引いたカードを場に出して処理
            ArrayList<Card> drawnTakenCards = field.takeCardsByMonth(drawnCard.getMonth());
            if (!drawnTakenCards.isEmpty()) {
                // 1枚だけ取る
                Card drawnTakenCard = drawnTakenCards.remove(0); // 最初のカードを取得
                player.captureCard(drawnTakenCard);
                player.captureCard(drawnCard);

                // 残りのカードを場に戻す
                for (Card remainingCard : drawnTakenCards) {
                    field.addCard(remainingCard);
                }

                System.out.println("山札から引いたカードで場札を取ったカード: " + drawnTakenCard);
                } else {
                // 場に追加
                field.addCard(drawnCard);
                System.out.println("山札から引いたカードを場に残した: " + drawnCard);
            }
        } else {
            System.out.println("山札が空です！");
        }

        System.out.println("現在の場の状態: " + field);
        System.out.println("プレイヤーの取り札: " + player.getCaptures());
        nowTurn++;
        System.out.println("現在のTurn: " + nowTurn);

    }

    //役判定
    public void checkRules(Player player){
        int lightCount = 0;         // 光札の枚数
        int rainyLightCount = 0;    // 雨札を含む光札の枚数
        int hanamiCount = 0;        // 花見で一杯
        int tsukimiCount = 0;       // 月見で一杯
        int inoshikaChoCount = 0;   // 猪鹿蝶
        int redShortCount = 0;      // 赤短冊
        int blueShortCount = 0;     // 青短冊
        int taneCount = 0;          // タネ札
        int tanCount = 0;           // 短冊札
        int kasuCount = 0;          // カス札
        int redAndBlueShortBonus = 0; // 赤短・青短のボーナス



        // プレイヤーの取り札をループ
        for (Card card : player.getCaptures()) {
            // カードの持つ役をループ
            for (Card.Role role : card.getRole()) {
                // 五光の判定
                if (role.getName().equals("五光")) {
                    lightCount++; // 光札をカウント
                }

                // 雨札を含む光札（雨入り四光）の判定
                if (role.getName().equals("雨四光")) {
                    rainyLightCount++; // 雨札を含む光札のカウント
                    }

                // 花見で一杯の判定（特定の役名で判定）
                if (role.getName().equals("花見で一杯")) {
                    hanamiCount++;
                }

                // 月見で一杯の判定
                if (role.getName().equals("月見で一杯")) {
                    tsukimiCount++;
                }

                // 猪鹿蝶の判定
                if (role.getName().equals("猪鹿蝶")) {
                    inoshikaChoCount++;
                }

                // 赤短の判定
                if (role.getName().equals("赤短")) {
                    redShortCount++;
                }

                // 青短の判定
                if (role.getName().equals("青短")) {
                    blueShortCount++;
                }

                // タネの判定
                if (role.getName().equals("タネ")) {
                    taneCount++;
                }

                // 短冊（タン）の判定
                if (role.getName().equals("タン")) {
                    tanCount++;
                }

                // カスの判定
                if (role.getName().equals("カス")) {
                    kasuCount++;
                }
            }
        }
        int totalScore = 0;
        ArrayList<String> achievedRoles = new ArrayList<>(); // 成立した役を記録
        ArrayList<Integer> achievedScores = new ArrayList<>();
        // 五光、四光、三光
        if (lightCount == 5) {
            totalScore += 10; // 五光
            achievedScores.add(10);
            achievedRoles.add("五光");
        } else if (lightCount == 4) {
            totalScore += rainyLightCount > 0 ? 7 : 8; // 四光
            achievedRoles.add(rainyLightCount > 0 ? "雨入り四光" : "四光");
            achievedScores.add(rainyLightCount > 0 ? 7 : 8);
        } else if (lightCount == 3) {
            totalScore += 5; // 三光
            achievedScores.add(5);
            achievedRoles.add("三光");

        }

        // 花見で一杯、月見で一杯
        if (hanamiCount > 0) {
            totalScore += 5;
            achievedScores.add(5);
            achievedRoles.add("花見で一杯");
        }
        if (tsukimiCount > 0) {
            totalScore += 5;
            achievedScores.add(5);
            achievedRoles.add("月見で一杯");
        }

        // 猪鹿蝶
        if (inoshikaChoCount == 3) {
            totalScore += 5 + (inoshikaChoCount - 3); // 猪鹿蝶 + 追加得点
            achievedScores.add(5);
            achievedRoles.add("猪鹿蝶");
        }

        // 赤短、青短
        if (redShortCount >= 3) {
           totalScore += 5 + (redShortCount - 3); // 赤短の点数
           achievedScores.add(5);
           achievedRoles.add("赤短");
        }       
        if (blueShortCount >= 3) {
            totalScore += 5 + (blueShortCount - 3); // 青短の点数
            achievedScores.add(5);
            achievedRoles.add("青短");
        }

        // タネ、タン、カス
        if (taneCount >= 5) {
            totalScore += 1 + (taneCount - 5); // タネの点数
            achievedScores.add(1 + (taneCount - 5));
            achievedRoles.add("タネ");
        }
        if (tanCount >= 5) {
            totalScore += 1 + (tanCount - 5); // 短冊の点数
            achievedScores.add(1 + (tanCount - 5));
            achievedRoles.add("タン");
        }
        if (kasuCount >= 10) {
            totalScore += 1 + (kasuCount - 10); // カスの点数
            achievedScores.add(1 + (kasuCount - 10));
            achievedRoles.add("カス");
        }

        // 高得点ボーナス処理
        if (totalScore >= 7) {
            achievedScores.add(totalScore);
            totalScore *= 2; // 高得点の場合、得点を倍にする
            achievedRoles.add("高得点ボーナス");
        }
        

        // 成立した役と得点の表示
        System.out.println("成立した役:");
        for (int i = 0; i < achievedRoles.size(); i++) {
            System.out.println("- " + achievedRoles.get(i) + ": " + achievedScores.get(i) + "点");
        }

        // バグチェック: 成立役がない場合
        if (achievedRoles.isEmpty()) {
            System.out.println("エラー: 役が成立していません。役判定ロジックを確認してください。");
        }

        // プレイヤーの得点を表示
        System.out.println("プレイヤーの得点: " + totalScore);
        player.setnumberRules(achievedRoles.size());//

    }
}