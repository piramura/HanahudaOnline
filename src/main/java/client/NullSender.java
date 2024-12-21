import java.io.OutputStream;
import java.net.Socket;

public class NullSender {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 10030)) {
            OutputStream out = socket.getOutputStream();
            // データを送信しないで接続を閉じる（nullをシミュレート）
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
