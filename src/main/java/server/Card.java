import java.util.ArrayList;
import java.util.List;
//各カードの情報を管理するクラス
public class Card{
	private final Month month; // 月
    private final Point point; // 種別（光、短冊、カスなど）
    private final List<Role> roles; // 複数の役を持つ
	private boolean isInPlay;// カードが場にあるかどうか
    private int id; // 一意のID

	// コンストラクタ
    public Card(int id,Month month, Point point) {
        this.month = month;
        this.point = point;
        this.roles = new ArrayList<>();
        this.id = id;
    }
    public int getId() {
        return id;
    }
    // ゲッター
    public Month getMonth() {
		return month;
	}
	public Point getPoint() {
		return point;
	}
    public List<Role> getRole() {
        return roles;
    }

	public boolean isInPlay() {
    	return isInPlay;
	}
	// セッター
	public void addRole(String roleName, int score) {
        roles.add(new Role(roleName, score));
    }

	public void setInPlay(boolean inPlay) {
    	this.isInPlay = inPlay;
	}


    // 内部クラス：役の情報を管理
    public static class Role {
        private final String name;
        private final int score;

        public Role(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
    public enum Month {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;
    }
    public enum Point {
        HIKARI, TANE, TANZAKU, KASU;
    }

    @Override
    public String toString() {
        // 役の文字列を構築
        String rolesString = "";
        if (!roles.isEmpty()) {
            for (int i = 0; i < roles.size(); i++) {
                Role role = roles.get(i);
                rolesString += role.name + ":" + role.score + "点";
                if (i < roles.size() - 1) {
                    rolesString += ", "; // 最後の役にはカンマを付けない
                }
            }
            rolesString = " (" + rolesString + ")";
        }

        // 全体の文字列を構築
        return month + " - " + point + rolesString;
    }
}