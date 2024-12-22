import java.net.*;
import java.io.*;

class CommClient {
    private static CommClient instance; // シングルトンインスタンス
    private Socket clientS = null;
    private InputStream in = null; // BufferedReader -> InputStream に変更
    private OutputStream out = null; // PrintWriter -> OutputStream に変更

    // プライベートコンストラクタ
    private CommClient(String serverHost, int serverPort) throws IOException {
        clientS = new Socket(InetAddress.getByName(serverHost), serverPort);
        if (clientS.isClosed()) {
            throw new IOException("Socket が閉じられています。");
        }
        in = clientS.getInputStream();
        out = clientS.getOutputStream();
        System.out.println("接続成功: " + serverHost + ":" + serverPort);
    }

    // シングルトンインスタンスの取得
    public static synchronized CommClient getInstance(String serverHost, int serverPort) throws IOException {
        if (instance == null || instance.clientS.isClosed()) {
            instance = new CommClient(serverHost, serverPort);
        }
        return instance;
    }
    // boolean open(String host, int port) {
    //     try {
    //         clientS = new Socket(InetAddress.getByName(host), port);
    //         if (clientS.isClosed()) {
    //             throw new IOException("Socket が閉じられています。");
    //         }
    //         in = clientS.getInputStream();
    //         out = clientS.getOutputStream();
    //         System.out.println("接続成功: " + host + ":" + port);
    //     } catch (UnknownHostException e) {
    //         System.err.println("ホストが見つかりません: " + e.getMessage());
    //         return false;
    //     } catch (IOException e) {
    //         System.err.println("接続に失敗しました: " + e.getMessage());
    //         return false;
    //     }
    //     return true;
    // }

    boolean send(String msg) {
        if (out == null) {
            System.err.println("送信ストリームが null です。");
            return false;
        }
        try {
            out.write((msg + "\n").getBytes()); // 改行を付加して送信
            out.flush();
            System.out.println("送信: " + msg);
        } catch (IOException e) {
            System.err.println("送信エラー: " + e.getMessage());
            return false;
        }
        return true;
    }

    String recv() {
        if (in == null) {
            System.err.println("受信ストリームが null です。Socket が正しく初期化されていない可能性があります。");
            return null;
        }
        try {
            byte[] buffer = new byte[1024];
            int length = in.read(buffer); // データを受信
            if (length == -1) {
                System.err.println("受信ストリームが終了しました。");
                return null;
            }
            String msg = new String(buffer, 0, length).trim();
            System.out.println("受信: " + msg);
            return msg.isEmpty() ? null : msg;
        } catch (SocketTimeoutException e) {
            System.err.println("タイムアウトしました。");
            return null;
        } catch (IOException e) {
            System.err.println("受信エラー: " + e.getMessage());
            return null;
        }
    }


    int setTimeout(int to) {
        try {
            clientS.setSoTimeout(to);
        } catch (SocketException e) {
            System.err.println("タイムアウト設定に失敗しました: " + e.getMessage());
            System.exit(1);
        }
        return to;
    }
    public void setDefaultTimeout() {
        setTimeout(5000); // デフォルトタイムアウトを5秒に設定
    }


    public void close() {
        try {
            if (out != null) {
                out.write("/QUIT\n".getBytes()); // サーバーに終了を通知
                out.flush();
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientS != null) clientS.close();
            System.out.println("接続を正常に終了しました。");
        } catch (IOException e) {
            System.err.println("クローズ処理中にエラーが発生しました: " + e.getMessage());
        }
    }

}
