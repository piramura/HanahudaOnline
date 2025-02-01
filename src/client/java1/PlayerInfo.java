// プレイヤー情報を保持するクラス
public class PlayerInfo {
    private String playerName;
    private int iconNum;
    private int level;

    public PlayerInfo(String playerName, int iconNum, int level) {
        this.playerName = playerName;
        this.iconNum = iconNum;
        this.level = level;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getIconNum() {
        return iconNum;
    }

    public int getLevel() {
        return level;
    }
    @Override
    public String toString() {
        return "PlayerInfo{" +
            "playerName='" + playerName + '\'' +
            ", iconNum=" + iconNum +
            ", level=" + level +
            '}';
    }
}