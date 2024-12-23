import java.util.HashMap;
import java.util.Map;

public class ClientSessionManager {
    private Map<String, Integer> validSessions = new HashMap<>();

    // 新しいセッションIDを登録
    public synchronized void registerSession(String sessionId) {
        validSessions.put(sessionId, validSessions.size() + 1); // 登録時にプレイヤー番号を割り当て
        System.out.println("セッションIDを登録: " + sessionId);
    }

    // セッションIDが有効か確認
    public boolean isValidSession(String sessionId) {
        return validSessions.containsKey(sessionId);
    }

    // セッションIDを削除
    public void removeSession(String sessionId) {
        validSessions.remove(sessionId);
    }
    public Integer getPlayerIndex(String sessionId) {
        return validSessions.get(sessionId);
    }
    public Map<String, Integer> getSessions() {
        return validSessions;
    }
}
