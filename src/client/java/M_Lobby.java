/* AWT関連*/
import java.awt.*;
import java.awt.event.*;
/* Swing関連*/
import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/* Preferences(設定保存)*/
import java.util.prefs.Preferences;
/* サウンド関連*/
import javax.sound.sampled.*;
/*入出力と画像処理*/
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
/* データ構造とユーティリティ*/
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

/* JPanelを継承したM_Lobbyクラスを定義します。*/
class M_Lobby extends JPanel {
    /* privateで宣言します。*/
    private C_Lobby c_Lobby;
    private boolean isSettingOpen = false;
    private boolean isPlayerIcon = false;
    private boolean isCheckMarkVisibleBGM;
    private boolean isCheckMarkVisibleSE;
    private boolean isReturnTitle = false;
    private boolean isSession = false;
    private boolean doorsOpen = false;
    private boolean isPlayerInfo = false;
    private boolean stopMovement = false;
    private int moveX = -1000;
    private int moveRectX = -3000;
    private float doorSize = 2.0f;
    private float wallSize = 2.4f;
    private float alpha = 0.0f;
    private float zoomFactor = 0.1f;
    private float scaleFactor = 1.0f;
    private Timer uiTimer;
    private Rectangle settingButtonBounds;
    private Rectangle closeButtonBounds;
    private Rectangle closeSessionButtonBounds;
    private Rectangle onlineButtonBounds;
    private Rectangle computerButtonBounds;
    private Rectangle howToPlayButtonBounds;
    private Rectangle returnTitleButtonBounds;
    private Rectangle playerIconBounds;
    private Rectangle roleIconBounds;
    private Rectangle checkBoxBGMBounds;
    private Rectangle checkBoxSEBounds;
    private Rectangle[] imageRectangles = new Rectangle[48];
    private JSlider slider1;
    private JSlider slider2;
    private ArrayList<Ball> balls = new ArrayList<>();
    private Random random = new Random();
    private String originalOpponentPlayerName = "Computer";
    private int opponentIconNumber = 0;
    private int opponentExperience = 0;
    private boolean gameStarted = false;
    private GameController gameController;
    private GameClient client;
    /* コンストラクタでLobbyの設定を行います。*/
    public M_Lobby(C_Lobby c_Lobby) {
        /* 初期化*/
        this.c_Lobby = c_Lobby;
        /* 各ボタンの位置とサイズを計算して、クリック判定用のRectangleを作成*/
        this.settingButtonBounds = new Rectangle(1835, 10, 77, 77);
        this.roleIconBounds = new Rectangle(1835, 100, 77, 77);
        this.closeButtonBounds = new Rectangle(1810, 50, 54, 54);
        this.closeSessionButtonBounds = new Rectangle(1030, 5, 20, 20);
        this.onlineButtonBounds = new Rectangle(1080, 125, 700, 200);
        this.computerButtonBounds = new Rectangle(1080, 325, 700, 200);
        this.howToPlayButtonBounds = new Rectangle(1080, 525, 700, 200);
        this.returnTitleButtonBounds = new Rectangle(720, 810, 480, 135);
        this.playerIconBounds = new Rectangle(60, 10, 114, 153);
        this.checkBoxBGMBounds = new Rectangle(775, 315, 80, 80);
        this.checkBoxSEBounds = new Rectangle(775, 515, 80, 80);
        /* 画像を配置するためのRectangle配列を作成*/
        for (int i = 0; i < 48; i++) {
            imageRectangles[i] = new Rectangle(800 + (i % 8) * 114, 80 + (i / 8) * 153, 114, 153);
        }
        /* フェードイン用タイマー*/
        Timer fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.06f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        fadeTimer.start();
        /* タイマーイベント処理 */
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Iterator<Ball> iterator = balls.iterator();
                while (iterator.hasNext()) {
                    Ball ball = iterator.next();
                    ball.x += ball.dx;
                    ball.y += ball.dy;
                    if (ball.x < -960 || ball.x > 2880 ||
                        ball.y < -540 || ball.y > 1620) {
                        iterator.remove();
                    }
                }
            }
        });
        timer.start();
    }

    /* ボールを3つ生成してランダムな方向に飛ばす*/
    public void generateRandomBalls(int x, int y) {
        for (int i = 0; i < 3; i++) {
            double angle = this.random.nextDouble() * 2 * Math.PI;
            double speed = 5 + this.random.nextDouble() * 4;
            int dx = (int) (Math.cos(angle) * speed);
            int dy = (int) (Math.sin(angle) * speed);
            this.balls.add(new Ball(x, y, dx, dy));
        }
    }

    /* チェックボックスの管理*/
    public void clickCheckBoxBGM() {
        this.isCheckMarkVisibleBGM = !this.isCheckMarkVisibleBGM;
        AppPreferences.putBoolean("BGM", this.isCheckMarkVisibleBGM);
    }

    public void clickCheckBoxSE() {
        this.isCheckMarkVisibleSE = !this.isCheckMarkVisibleSE;
        AppPreferences.putBoolean("SE", this.isCheckMarkVisibleSE);
    }

    /* 設定を開く*/
    public void openSetting() {
        this.isSettingOpen = true;
        this.slider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, AppPreferences.getInt("BGM音量", 50));
        this.slider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, AppPreferences.getInt("SE音量", 50));
        this.slider1.setBounds(1000, 335, 800, 40);
        this.slider2.setBounds(1000, 535, 800, 40);
        this.slider1.setMajorTickSpacing(25);
        this.slider2.setMajorTickSpacing(25);
        this.slider1.setMinorTickSpacing(5);
        this.slider2.setMinorTickSpacing(5);
        this.slider1.setBackground(new Color(0, 0, 0, 0));
        this.slider2.setBackground(new Color(0, 0, 0, 0));
        this.slider1.setOpaque(false);
        this.slider2.setOpaque(false);
        this.slider1.addChangeListener(e1 -> {AppPreferences.putInt("BGM音量", this.slider1.getValue());});
        this.slider2.addChangeListener(e1 -> {AppPreferences.putInt("SE音量", this.slider2.getValue());});
        c_Lobby.addSlider(slider1, slider2);
    }

    /* 設定を閉じる*/
    public void closeSetting() {
        this.isSettingOpen = false;
        c_Lobby.removeSlider(slider1, slider2);
    }

    public void setIsPlayerIcon(boolean isPlayerIcon) {
        this.isPlayerIcon = isPlayerIcon;
    }
    
    public void closeSession() {
        isSession = false;
        try {
            client.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /* オンライン対戦開始*/
    public void Online() {
        AppPreferences.putString("プレイヤー名", c_Lobby.getPlayerNameInputField().getText());
        gameController = new GameController();
        // client = new GameClient("https://hanahudaonline.onrender.com");
        client = new GameClient("http://localhost:10030");
        client.setGameController(gameController);
        gameController.setGameClient(client);
        gameController.setBotMode(true);
        System.out.println(gameController.getBotMode());
        gameController.startOnlineMatch();
        startTimer();
    }
    public void Computer(){
        AppPreferences.putString("プレイヤー名", c_Lobby.getPlayerNameInputField().getText());
        gameController = new GameController();
        // client = new GameClient("https://hanahudaonline.onrender.com");
        client = new GameClient("http://localhost:10030");
        client.setGameController(gameController);
        gameController.setGameClient(client);
        gameController.setBotMode(true);
        System.out.println(gameController.getBotMode());
        gameController.startOnlineMatch();
        startTimer();
    }

    /* タイマーでラベルを更新し、ゲームが開始されたら処理を切り替える*/
    private void startTimer() {
        /* セッション中*/
        isSession = true;
        c_Lobby.setTimeLabel();
        uiTimer = new Timer(64, e -> {
            int elapsedSeconds = gameController.getElapsedTime();
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            c_Lobby.setTimeText(minutes, seconds);
            if(gameController.getIsActive()) isSession = false;
            if (!isSession) {
                c_Lobby.removeTimeLabel();
                ((Timer) e.getSource()).stop();
            }
            try {
                if (!gameStarted && client.isGameStarted()) {
                    ((Timer) e.getSource()).stop();
                    gameStarted = true;
                    client.sendPlayerInfo(AppPreferences.getString("プレイヤー名", "player"), AppPreferences.getInt("プレイヤーアイコン", 0), AppPreferences.getInt("経験値", 0) / 50);
                    /* フェードアウト用タイマー*/
                    fadeOutTimer();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                ((Timer) e.getSource()).stop();
            }
        });
        uiTimer.start();
    }
    

    public void fadeOutTimer() {
        Timer fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.02f;
                if (alpha <= 0.0f) {
                    alpha = 0.0f;
                    ((Timer) e.getSource()).stop();
                    wallTimer();
                }
            }
        });
        fadeTimer.start();
    }

    public void wallTimer() {
        Timer wallTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wallSize -= 0.2f;
                if (wallSize <= 1) {
                    wallSize = 1;
                    ((Timer) e.getSource()).stop();
                    doorTimer();
                }
            }
        });
        wallTimer.start();
    }

    public void doorTimer() {
        Timer doorTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doorSize -= 0.04f;
                if(doorSize <= 1){
                    doorSize = 1;
                    ((Timer) e.getSource()).stop();
                    try{
                        client.fetchPlayerInfo();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    getOpponentInfo();
                    playerInfo();
                    delayTimer();
                }
            }
        });
        doorTimer.start();
    }

    public void delayTimer() {
        Timer delayTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                doorOpenTimer();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void doorOpenTimer() {
        doorsOpen = true;
        /* ドアが開くアニメーション*/
        Timer doorOpenTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomFactor += 0.08f;
                if (zoomFactor >= 0.8f) {
                    ((Timer) e.getSource()).stop();
                    startZoomAnimation();
                }
            }
        });
        doorOpenTimer.start();
    }

    /* 対戦相手の情報の取得*/
    private void getOpponentInfo() {
        PlayerInfo opponentInfo = gameController.getOpponentInfo();
        originalOpponentPlayerName = opponentInfo.getPlayerName();
        opponentIconNumber = opponentInfo.getIconNum();
        opponentExperience = opponentInfo.getLevel();
    }

    /* 対戦時のプレイヤーの情報の表示*/
    private void playerInfo(){
        Timer playerInfoTimer = new Timer(10, e -> {
            if(!stopMovement && moveX < 0)moveX += 20;
            if(!stopMovement && moveX >= 0)moveX += 80;
            if(!stopMovement && moveX < 0)moveRectX += 80;
            if(!stopMovement && moveX >= 0)moveRectX += 80;
            if(moveX >= 0) {
                stopMovement = true;
                Timer stopTimer = new Timer(1000, ev -> {
                    stopMovement = false;
                });
                stopTimer.setRepeats(false);
                stopTimer.start();
            }
            if(moveX >= 4000) {
                ((Timer)e.getSource()).stop();
            }
        });
        playerInfoTimer.start();
    }
    /* ズームアニメーションの開始*/
    private void startZoomAnimation() {
        Timer zoomTimer = new Timer(10, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleFactor += 0.04f;
                if(scaleFactor >= 4.0f){
                    ((Timer)e.getSource()).stop();
                    /* OnlineGameクラスに移動します*/
                    c_Lobby.moveOnlineGame(gameController, client);
                }
            }
        });
        zoomTimer.start();
    }

    public void setIsSession(boolean isActive) {
        isSession = isActive;
    }
    /* タイトルに戻る*/
    public void returnTitle() {
        isReturnTitle = true;
        /* フェードアウト用タイマー*/
        Timer fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.02f;
                if (alpha <= 0.0f) {
                    alpha = 0.0f;
                    ((Timer) e.getSource()).stop();
                    wall();
                }
            }
        });
        fadeTimer.start();
    }

    public void wall() {
        Timer wallTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wallSize -= 0.1f;
                if (wallSize <= 1) {
                    wallSize = 1;
                    ((Timer) e.getSource()).stop();
                    door();
                }
            }
        });
        wallTimer.start();
    }

    public void door() {
        Timer doorTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doorSize -= 0.02f;
                if(doorSize <= 1){
                    doorSize = 1;
                    ((Timer) e.getSource()).stop();
                    c_Lobby.moveTitle();
                }
            }
        });
        doorTimer.start();
    }
    public boolean getIsSettingOpen() { return isSettingOpen; }
    public boolean getIsPlayerIcon() { return isPlayerIcon; }
    public boolean getIsCheckMarkVisibleBGM() { return isCheckMarkVisibleBGM; }
    public boolean getIsCheckMarkVisibleSE() { return isCheckMarkVisibleSE; }
    public boolean getIsReturnTitle() { return isReturnTitle; }
    public boolean getIsSession() { return isSession; }
    public boolean getIsDoorsOpen() { return doorsOpen; }
    public boolean getIsPlayerInfo() { return isPlayerInfo; }
    public boolean getIsStopMovement() { return stopMovement; }
    public int getMoveX() { return moveX; }
    public int getMoveRectX() { return moveRectX; }
    public float getDoorSize() { return doorSize; }
    public float getWallSize() { return wallSize; }
    public float getAlpha() { return alpha; }
    public float getZoomFactor() { return zoomFactor; }
    public float getScaleFactor() { return scaleFactor; }
    public Timer getUiTimer() { return uiTimer; }
    public Rectangle getSettingButtonBounds() { return settingButtonBounds; }
    public Rectangle getCloseButtonBounds() { return closeButtonBounds; }
    public Rectangle getCloseSessionButtonBounds() { return closeSessionButtonBounds; }
    public Rectangle getOnlineButtonBounds() { return onlineButtonBounds; }
    public Rectangle getComputerButtonBounds() { return computerButtonBounds; }
    public Rectangle getHowToPlayButtonBounds() { return howToPlayButtonBounds; }
    public Rectangle getReturnTitleButtonBounds() { return returnTitleButtonBounds; }
    public Rectangle getPlayerIconBounds() { return playerIconBounds; }
    public Rectangle getRoleIconBounds() { return roleIconBounds; }
    public Rectangle getCheckBoxBGMBounds() { return checkBoxBGMBounds; }
    public Rectangle getCheckBoxSEBounds() { return checkBoxSEBounds; }
    public Rectangle getImageRectangles(int cardId) { return imageRectangles[cardId]; }
    public JSlider getSlider1() { return slider1; }
    public JSlider getSlider2() { return slider2; }
    public ArrayList<Ball> getBalls() { return balls; }
    public Random getRandom() { return random; }
    public String getOriginalOpponentPlayerName() { return originalOpponentPlayerName; }
    public int getOpponentIconNumber() { return opponentIconNumber; }
    public int getOpponentExperience() { return opponentExperience; }
    public boolean getIsGameStarted() { return gameStarted; }
    public GameController getGameController() { return gameController; }
    public GameClient getClient() { return client; }
}