import java.util.List;

public class RoleResult {
    private final int playerId; // プレイヤーID
    private final List<String> achievedRoles;
    private final int totalScore;

    public RoleResult(int playerId, List<String> achievedRoles, int totalScore) {
        this.playerId = playerId;
        this.achievedRoles = achievedRoles;
        this.totalScore = totalScore;
    }
    // プレイヤーIDを取得
    public int getPlayerId() {
        return playerId;
    }

    // 成立した役を取得
    public List<String> getAchievedRoles() {
        return achievedRoles;
    }

    // 合計得点を取得
    public int getTotalScore() {
        return totalScore;
    }

    @Override
    public String toString() {
        return "プレイヤー " + playerId + " の役: " + achievedRoles + ", 合計得点: " + totalScore;
    }
}
