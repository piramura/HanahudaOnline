import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

// import com.sun.net.httpserver.HttpServer;
// import java.net.InetSocketAddress;

public class HanahudaServer {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "10030"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // ClientSessionManagerを作成し、GameSessionManagerに渡す
        ClientSessionManager clientSessionManager = new ClientSessionManager();
        GameSessionManager gameSessionManager = new GameSessionManager(clientSessionManager);

        // エンドポイント設定
        server.createContext("/game/start", new StartGameHandler(gameSessionManager));
        server.createContext("/game/play", new PlayCardHandler(gameSessionManager));
        server.createContext("/game/state", new GameStateHandler(gameSessionManager));
        server.createContext("/session", new SessionHandler(clientSessionManager));


        server.setExecutor(null);
        System.out.println("サーバーがポート " + port + " で起動しました。");
        server.start();
    }
}

class StartGameHandler implements HttpHandler {
    private GameSessionManager gameSessionManager;

    public StartGameHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        gameSessionManager.startGame();
        String response = "ゲームが開始されました！";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
class PlayCardHandler implements HttpHandler {
    private GameSessionManager gameSessionManager;

    public PlayCardHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String sessionId = exchange.getRequestHeaders().getFirst("Session-ID");
        System.out.println("受信したセッションID: " + sessionId);
        String message = new String(exchange.getRequestBody().readAllBytes());

        String response = gameSessionManager.processMessage(sessionId, message);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
class GameStateHandler implements HttpHandler {
    private GameSessionManager gameSessionManager;

    public GameStateHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String sessionId = exchange.getRequestHeaders().getFirst("Session-ID");
        String response = gameSessionManager.getGameState(sessionId);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
class SessionHandler implements HttpHandler {
    private ClientSessionManager sessionManager;
    
    private static final String SECRET_PASSPHRASE = System.getenv().getOrDefault("GAME_SECRET", "default_passphrase");

    public SessionHandler(ClientSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 合言葉をリクエストボディから取得
        String passphrase = new String(exchange.getRequestBody().readAllBytes());
        System.out.println("受信した合言葉: " + passphrase);
        System.out.println("正しい合言葉: " + SECRET_PASSPHRASE);
        // 合言葉を検証
        if (!SECRET_PASSPHRASE.equals(passphrase)) {
            String response = "ERROR: 無効な合言葉";
            exchange.sendResponseHeaders(403, response.getBytes().length); // Forbidden
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }
        // セッションIDを発行
        String sessionId = java.util.UUID.randomUUID().toString();
        sessionManager.registerSession(sessionId);
        System.out.println("セッションIDを発行: " + sessionId);
        exchange.sendResponseHeaders(200, sessionId.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(sessionId.getBytes());
        }
    }
}
