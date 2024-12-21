import java.net.*;
import java.io.*;

class CommServer {
    private ServerSocket serverS = null;
    private Socket clientS = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private int port=0;

    CommServer() {}
    CommServer(int port) { open(port); }
    CommServer(CommServer cs) { serverS=cs.getServerSocket(); open(cs.getPortNo()); }
   
    ServerSocket getServerSocket() { return serverS; } 
    int getPortNo() { return port; }

    boolean open(int port){
      this.port=port;
      try{ 
	 if (serverS == null) {
    //  serverS = new ServerSocket(port); 
     // すべてのインターフェースで接続待機
                serverS = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
   }
      } catch (IOException e) {
         System.err.println("�ݡ��Ȥ˥��������Ǥ��ޤ���");
         System.exit(1);
      }
      try{
         clientS = serverS.accept();
         out = new PrintWriter(clientS.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
      } catch (IOException e) {
         System.err.println("Accept�˼��Ԥ��ޤ�����");
         System.exit(1);
      }
      return true;
    }

    boolean send(String msg){
        if (out == null) { return false; }
        out.println(msg);
        return true;
    }

    String recv() {
    String msg = null;
    if (in == null) {
        return null;
    }
    try {
        msg = in.readLine();
        if (msg == null || msg.trim().isEmpty()) {
            return null;
        }
    } catch (IOException e) {
        System.err.println("受信エラー: " + e.getMessage());
    }
    return msg;
}


    int setTimeout(int to){
        try{
          clientS.setSoTimeout(to);
        } catch (SocketException e){
          System.err.println("�����ॢ���Ȼ��֤��ѹ��Ǥ��ޤ���");
          System.exit(1);
        }
        return to;
    }

    void close() {
    try {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (clientS != null) {
            clientS.close();
        }
        if (serverS != null) {
            serverS.close();
        }
    } catch (IOException e) {
        System.err.println("クローズ処理中にエラーが発生しました: " + e.getMessage());
    }finally {
        in = null;
        out = null;
        clientS = null;
        serverS = null;
    }
}

}

class CommClient {
   Socket clientS = null;
   BufferedReader in = null;
   PrintWriter out = null;

   CommClient() {}
   CommClient(String host,int port) { open(host,port); }

   boolean open(String host,int port){
     try{
       clientS = new Socket(InetAddress.getByName(host), port);
       in = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
       out = new PrintWriter(clientS.getOutputStream(), true);
     } catch (UnknownHostException e) {
       System.err.println("�ۥ��Ȥ���³�Ǥ��ޤ���");
       System.exit(1);
     } catch (IOException e) {
       System.err.println("IO���ͥ������������ޤ���");
       System.exit(1);
     }
     return true;
   }

    boolean send(String msg){
      if (out == null) { return false; }
      out.println(msg);
      return true;
    }

    String recv(){
        String msg=null;
        if (in == null) { return null; }
        try{
          msg=in.readLine();
        } catch (SocketTimeoutException e){
            return null;
        } catch (IOException e) {
          System.err.println("�����˼��Ԥ��ޤ�����");
          System.exit(1);
        }
        return msg;
    }

    int setTimeout(int to){
        try{
          clientS.setSoTimeout(to);
        } catch (SocketException e){
          System.err.println("�����ॢ���Ȼ��֤��ѹ��Ǥ��ޤ���");
          System.exit(1);
        }
        return to;
    }

    void close(){
      try{
        in.close();  out.close();
        clientS.close();
      } catch (IOException e) {
          System.err.println("�����åȤΥ��������˼��Ԥ��ޤ�����");
          System.exit(1);
      }
      in=null; out=null;
      clientS=null;
    }
}

