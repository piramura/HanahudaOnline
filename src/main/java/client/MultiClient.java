import java.io.*;

class MultiClient{
   public static void main(String[] args){
    //  CommClient client = new CommClient("localhost",10030);//"localhost" は自分のPCのアドレス。
    //  //ここをグローバルIPアドレスにすればいい。
    //  BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    //  // サーバーからのメッセージ受信用スレッド
    try (BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {
         // ngrokで生成されたTCPドメインとポートを入力
         System.out.print("接続するngrokのドメイン（例: 0.tcp.ngrok.io:12345）を入力してください: ");
         String serverInfo = consoleInput.readLine();

         // ドメインとポートを分割
         String[] parts = serverInfo.split(":");
         String serverHost = parts[0]; // 例: "0.tcp.ngrok.io"
         int serverPort = Integer.parseInt(parts[1]); // 例: 12345

         // サーバーに接続
         CommClient client = new CommClient(serverHost, serverPort);
         System.out.println("サーバーに接続しました: " + serverHost + ":" + serverPort);

         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        new Thread(() -> {//サーバーからのメッセージを受け取るスレッドを作成し、常時受信待機
            String message;
            try {
                while ((message = client.recv()) != null) {
                    System.out.println("サーバーから: " + message);
                    // 手札情報のログ出力強化
                if (message.startsWith("あなたの手札: ")) {
                    System.out.println("手札更新: " + message.substring(8));
                }
                // 取り札情報の表示
                if (message.startsWith("あなたの取り札: ")) {
                    System.out.println("取り札更新: " + message.substring(8));
                    }
                }
            } catch (Exception e) {
                System.out.println("サーバーとの接続が切れました。");
            }
        }).start();

        // ユーザー入力送信用
         String msg;
         while ((msg = input.readLine()) != null) {
            client.send(msg); // メッセージ送信
         }

         client.close(); // 接続終了
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   }

