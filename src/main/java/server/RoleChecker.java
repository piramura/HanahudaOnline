import java.util.ArrayList;

public class RoleChecker {
    public static RoleResult checkRules(Player player){

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
        
        int playerId = player.getPlayerID(); // プレイヤーIDを取得
        // RoleResult を生成して返す
        return new RoleResult(playerId, achievedRoles, totalScore);
    }
    
}