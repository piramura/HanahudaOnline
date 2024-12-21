import java.net.*;
import java.io.*;

class CommServer {
    private ServerSocket serverS = null;
    private Socket clientS = null;
    private OutputStream out = null;
    private InputStream in = null;
    private int port = 0;

    CommServer() {}
    CommServer(int port) { open(port); }

    ServerSocket getServerSocket() { return serverS; }
    int getPortNo() { return port; }

    boolean open(int port) {
        this.port = port;
        try {
            serverS = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
            return true;
        } catch (IOException e) {
            System.err.println("サーバーソケットの作成に失敗しました: " + e.getMessage());
            return false;
        }
    }

    public CommServer acceptClient() throws IOException {
        Socket clientSocket = serverS.accept(); // クライアント接続を受け入れる
        CommServer newClient = new CommServer();
        newClient.clientS = clientSocket;
        newClient.initializeStreams();
        return newClient;
    }

    private void initializeStreams() throws IOException {
        if (clientS != null) {
            out = clientS.getOutputStream();
            in = clientS.getInputStream();
        }
    }

    boolean send(String msg) {
        try {
            if (out != null) {
                out.write((msg + "\n").getBytes());
                out.flush();
                return true;
            }
        } catch (IOException e) {
            System.err.println("送信エラー: " + e.getMessage());
        }
        return false;
    }

    String recv() {
        try {
            if (in == null) return null;
            byte[] buffer = new byte[1024];
            int length = in.read(buffer);
            if (length == -1) return null;
            return new String(buffer, 0, length).trim();
        } catch (IOException e) {
            System.err.println("受信エラー: " + e.getMessage());
            return null;
        }
    }

    void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientS != null) clientS.close();
        } catch (IOException e) {
            System.err.println("クローズ処理中にエラーが発生しました: " + e.getMessage());
        } finally {
            in = null;
            out = null;
            clientS = null;
        }
    }
}
