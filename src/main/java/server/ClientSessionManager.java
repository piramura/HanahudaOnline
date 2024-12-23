import java.util.HashSet;
import java.util.Set;

public class ClientSessionManager {
    private Set<String> validSessions = new HashSet<>();

    // 新しいセッションIDを登録
    public void registerSession(String sessionId) {
        validSessions.add(sessionId);
        System.out.println("セッションIDを登録: " + sessionId);
    }

    // セッションIDが有効か確認
    public boolean isValidSession(String sessionId) {
        return validSessions.contains(sessionId);
    }

    // セッションIDを削除
    public void removeSession(String sessionId) {
        validSessions.remove(sessionId);
    }
}
