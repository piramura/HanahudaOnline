import javax.swing.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
public class GameController {
    private MainFrame mainFrame;
    private GameClient gameClient;
    private List<Integer> field;
    private List<Integer> player1Cards;
    private List<Integer> player2Cards;
    private int currentTurn;
    private int playerId; // 自分のプレイヤーIDを管理

    public List<Integer> getField() {
        return field;
    }

    public void setField(List<Integer> field) {
        this.field = field;
    }

    public List<Integer> getPlayer1Cards() {
        return player1Cards;
    }

    public void setPlayer1Cards(List<Integer> player1Cards) {
        this.player1Cards = player1Cards;
    }

    public List<Integer> getPlayer2Cards() {
        return player2Cards;
    }

    public void setPlayer2Cards(List<Integer> player2Cards) {
        this.player2Cards = player2Cards;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }
    public boolean isChange(String rawGameState) {
    boolean stateChanged = false;

    String[] lines = rawGameState.split("\n");
    for (String line : lines) {
        if (line.startsWith("Field:")) {
            List<Integer> newField = parseCardList(line.substring(7));
            if (!newField.equals(getField())) {
                setField(newField);
                stateChanged = true;
            }
        } else if (line.startsWith("Player1 :")) {
            List<Integer> newPlayer1Cards = parseCardList(line.substring(10));
            if (!newPlayer1Cards.equals(getPlayer1Cards())) {
                setPlayer1Cards(newPlayer1Cards);
                stateChanged = true;
            }
        } else if (line.startsWith("Player2 :")) {
            List<Integer> newPlayer2Cards = parseCardList(line.substring(10));
            if (!newPlayer2Cards.equals(getPlayer2Cards())) {
                setPlayer2Cards(newPlayer2Cards);
                stateChanged = true;
            }
        } else if (line.startsWith("現在のターン:")) {
            int newTurn = Integer.parseInt(line.substring(7).trim());
            if (newTurn != getCurrentTurn()) {
                setCurrentTurn(newTurn);
                stateChanged = true;
            }
        }
    }

    return stateChanged; // 変更があった場合に true を返す
}
public void pollGameState() {
    Timer timer = new Timer(1000, e -> {
        try {
            
                gameClient.fetchGameState();
            
        } catch (Exception ex) {
            System.err.println("ゲーム状態の取得に失敗: " + ex.getMessage());
        }
    });
    timer.start();
}

public void parseGameState(String rawGameState) {
    String[] lines = rawGameState.split("\n");

        for (String line : lines) {
            if (line.startsWith("Field:")) {
                setField(parseCardList(line.substring(7)));
            }
            if (line.startsWith("Player1 :")) {
    if (playerId == 1) {
        System.out.println("Player1 情報を自分のカードとして設定: " + line.substring(10));
        setPlayer1Cards(parseCardList(line.substring(10)));
    } else {
        System.out.println("Player1 情報を相手のカードとして設定: " + line.substring(10));
        setPlayer2Cards(parseCardList(line.substring(10)));
    }
}
if (line.startsWith("Player2 :")) {
    if (playerId == 2) {
        System.out.println("Player2 情報を自分のカードとして設定: " + line.substring(10));
        setPlayer1Cards(parseCardList(line.substring(10)));
    } else {
        System.out.println("Player2 情報を相手のカードとして設定: " + line.substring(10));
        setPlayer2Cards(parseCardList(line.substring(10)));
    }
}
if (line.startsWith("現在のターン:")) {
                setCurrentTurn(Integer.parseInt(line.substring(7).trim()));
            }
        }
        
            refreshUI();
}
        
    public void refreshUI() {
    mainFrame.updateBoard(getField());
        mainFrame.updatePlayerHand(getPlayer1Cards());
        mainFrame.updateOpponentHand(getPlayer2Cards().size());
    mainFrame.updadateTurn(getCurrentTurn());
}
    private List<Integer> parseCardList(String cardString) {
        List<Integer> cardList = new ArrayList<>();
        String[] cardNumbers = cardString.split(",");
        for (String card : cardNumbers) {
            cardList.add(Integer.parseInt(card.trim()));
        }
        return cardList;
    }
    public GameController() {
        this.mainFrame = new MainFrame(this);
        
    }
    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    // // イベントリスナーを設定
    // private void setupEventListeners() {
    //     mainFrame.addStartGameListener(() -> startOnlineMatch());
    //     mainFrame.addCardClickListener(card -> handleCardClick(card));
    //     mainFrame.addQuitListener(() -> quitGame());
    // }

    // オンライン対戦開始
    public void startOnlineMatch() {
        try {
            gameClient.initializeSession("default_passphrase");
            playerId = gameClient.fetchPlayerId(); // 自分のPlayerIDを取得
            System.out.println("現在の Player ID: " + playerId);

            while (!gameClient.isGameStarted()) {
               System.out.println("ゲームが開始されるのを待っています...");
                Thread.sleep(1000); // 1秒待機
            }
            
            gameClient.fetchGameState();
            // メインフレームを表示（既存のフィールドを使用）
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        gameClient.disconnect();
                    } catch (Exception e) {
                        System.err.println("切断通知中にエラーが発生しました: " + e.getMessage());
                    }
                    System.exit(0); // プログラム終了
                }
                
        });
        // ポーリングを開始
        pollGameState(); 
            refreshUI();
        } catch (Exception e) {
            System.err.println("セッションの初期化に失敗しました: " + e.getMessage());
        }
    }

    // カードをクリックしたときの処理
    public void handleCardClick(int cardID) {
        try {
            gameClient.playCard(cardID,this.playerId);
            gameClient.fetchGameState();
            refreshUI();
        } catch (Exception e) {
            System.err.println("カード送信エラー: " + e.getMessage());
        }
    }

    // ゲーム終了
    private void quitGame() {
        System.out.println("ゲームを終了します。");
        System.exit(0);
    }
    
    // クライアントで通知を処理するメソッド
public void handleDisconnectionNotification(String message) {
    if (message.startsWith("DISCONNECTED_PLAYER:")) {
        int disconnectedPlayer = Integer.parseInt(message.split(":")[1].trim());
        System.out.println("プレイヤー " + disconnectedPlayer + " が切断されました。");

        // ゲーム画面を更新
        mainFrame.showDisconnectionMessage(disconnectedPlayer);
    }
}

}
