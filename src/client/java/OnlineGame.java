/* AWT関連*/
import java.awt.*;
import java.awt.event.*;
/* Swing関連*/
import javax.swing.*;
import javax.swing.Timer;
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
import java.util.Arrays;

/* JPanelを継承したOnlineGameクラスを定義します。*/
class OnlineGame extends JPanel {
    /* privateで宣言します。*/
    private Image stage;
    private Image cardBack;
    private Image roleIcon;
    private Image settingsIcon;
    private Image moveCard;
    private Image settingsWindowImage;
    private Image returnLobbyButtonImage;
    private Image closeIcon;
    private Image checkImage;
    private Image checkMarkImage;
    private Image cherryBlossom;
    private Image darkImage;
    private BufferedImage[][] Card = new BufferedImage[12][4];
    private BufferedImage[][] rotateCard = new BufferedImage[12][4];
    private JPanel panel;
    private JFrame frame;
    private String playerName = AppPreferences.getString("playerName", "PlayerName");
    private int experiment = AppPreferences.getInt("経験値", 0);
    private int cardNumber = AppPreferences.getInt("playerIcon", 0);
    private String originalOpponentPlayerName = "Computer";
    private int opponentExperience = 0;
    private int opponentIconNumber = 0;
    private double moveX = 0;
    private double moveY = 0;
    private int cardHeight;
    private int cardWidth;
    private int textX = -1000;
    private int moveRectX = -6000;
    private int textTurnX = -1000;
    private int moveRectTurnX = -6000;
    private boolean isMoveCard = false;
    private boolean stopMovement = false;
    private boolean stopTurnMovement = false;
    private boolean isSettingOpen = false;
    private boolean checkMarkVisible1;
    private boolean checkMarkVisible2;
    private boolean isReturnLobby = false;
    private boolean isMyTurn = true;
    private boolean darkenScreen = false;
    private boolean isHandSelect = false;
    private boolean isFieldSelect = false;
    private boolean isCardOpen = false;
    private boolean isStarted = false;
    private boolean isBoardSelect = false;
    private boolean isHandSelected = false;
    private boolean isKOIKOI = false;
    private Integer cardSize = 0;
    private Integer currentCardSize = 0;
    private List<Integer> boardList1;
    private List<List<Integer>> currentBoardList = Arrays.asList(Arrays.asList(0, 1, 2, 3, -1, -1, -1, -1 , -1), Arrays.asList(4, 5, 6, 7, -1, -1, -1, -1, -1, -1));
    private List<Integer> currentPlayerHandList = Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15);
    private List<Integer> currentOpponentHandList = Arrays.asList(16, 17, 18, 19, 20, 21, 22, 23);
    private int[] cardState = new int[48];
    private List<List<Integer>> boardList = Arrays.asList(Arrays.asList(0, 1, 2, 3, -1, -1, -1, -1 , -1), Arrays.asList(4, 5, 6, 7, -1, -1, -1, -1, -1, -1));
    private List<Integer> playerHandList = Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15);
    private List<Integer> opponentHandList = Arrays.asList(16, 17, 18, 19, 20, 21, 22, 23);
    private List<List<Boolean>> flushBoardListBoolean = Arrays.asList(Arrays.asList(false, false, false, false, false, false, false, false, false), Arrays.asList(false, false, false, false, false, false, false, false, false));
    private List<Boolean> flushPlayerHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<List<Boolean>> boardListBoolean = Arrays.asList(Arrays.asList(false, false, false, false, false, false, false, false, false), Arrays.asList(false, false, false, false, false, false, false, false, false));
    private List<Boolean> playerHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<Boolean> opponentHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<List<Boolean>> currentBoardListBoolean = Arrays.asList(Arrays.asList(false, false, false, false, false, false, false, false, false), Arrays.asList(false, false, false, false, false, false, false, false, false));
    private List<Boolean> currentPlayerHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<Boolean> currentOpponentHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<Integer> gokou = Arrays.asList(0, 8, 28, 40, 44);
    private List<Integer> tane = Arrays.asList(4, 12, 16, 20, 24, 29, 32, 36, 41);
    private List<Integer> tann = Arrays.asList(1, 5, 9, 13, 17, 21, 25, 33, 37, 42);
    private List<Integer> kasu = Arrays.asList(2, 3, 6, 7, 10, 11, 14, 15, 18, 19, 22, 23, 26, 27, 30, 31, 34, 35, 38, 39, 43, 45, 46, 47);
    private List<Boolean> isGokou = Arrays.asList(false, false, false, false, false);
    private List<Boolean> isTane = Arrays.asList(false, false, false, false, false, false, false, false, false);
    private List<Boolean> isTann = Arrays.asList(false, false, false, false, false, false, false, false, false, false);
    private List<Boolean> isKasu = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);
    private List<Boolean> opponentIsGokou = Arrays.asList(false, false, false, false, false);
    private List<Boolean> opponentIsTane = Arrays.asList(false, false, false, false, false, false, false, false, false);
    private List<Boolean> opponentIsTann = Arrays.asList(false, false, false, false, false, false, false, false, false, false);
    private List<Boolean> opponentIsKasu = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);
    private ArrayList<Ball> balls = new ArrayList<>();
    private Random random = new Random();
    private GameClient client;
    private float alpha = 0.0f;
    private double deltaX;
    private double deltaY;
    private double targetMoveX;
    private double targetMoveY;
    private int cardRemain = 48;
    private int playerHandSelect;
    private int steps = 4;
    private Rectangle[][] boardRectangles = new Rectangle[2][9];
    private Rectangle[] playerHandRectangles = new Rectangle[8];
    private Rectangle settingsButtonBounds;
    private Rectangle closeButtonBounds;
    private Rectangle returnLobbyButtonBounds;
    private Rectangle checkBox1Bounds;
    private Rectangle checkBox2Bounds;
    private Rectangle KOIKOIButtonBounds;
    private Rectangle ENDButtonBounds;
    private JSlider slider1;
    private JSlider slider2;
    private Integer turn;
    private Integer cardNumbers = 0;
    private Integer playerId = 0;
    private static class Ball {
        int x, y;
        int dx, dy;
        Ball(int x, int y, int dx, int dy) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }
    }
    private GameController gameController;
    /* コンストラクタでOnlineGameの設定を行います。*/
    public OnlineGame(JFrame HanahudaFrame, GameController controller, GameClient gameClient) {
        /* クライアントの設定*/
        client = gameClient;
        gameController = controller;
        /* フェチ処理*/
        try{
            client.fetchGameState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* ウィンドウの基本設定 */
        frame = HanahudaFrame;
        /* カード情報を保存する*/
        for(int i = 0;i < 48; i++){
            cardState[i] = 0;
        }
        /* 対戦相手の情報を読み込む*/
        getOpponentInfo();
        /* カード情報の初期化*/
        updateCard();
        /* 画像を読み込む*/
        stage = new ImageIcon("画像/戦闘背景.png").getImage();
        cardBack = new ImageIcon("札絵柄/花札裏面.jpg").getImage();
        settingsIcon = new ImageIcon("画像/設定アイコン.png").getImage();
        settingsWindowImage = new ImageIcon("画像/設定ウィンドウ.png").getImage();
        returnLobbyButtonImage = new ImageIcon("画像/タイトルに戻る.png").getImage();
        closeIcon = new ImageIcon("画像/バツアイコン.png").getImage();
        roleIcon = new ImageIcon("画像/役表アイコン.png").getImage();
        checkImage = new ImageIcon("画像/チェック欄.png").getImage();
        checkMarkImage = new ImageIcon("画像/チェックマーク.png").getImage();
        cherryBlossom = new ImageIcon("画像/桜.png").getImage();
        darkImage = new ImageIcon("画像/うっすら.png").getImage();
        /* 1月1.pngから12月4.pngまでを読み込む*/
        for(int month = 0; month < 12; month++){
            for(int day = 0; day < 4; day++) {
                try{
                    String filePath = "札絵柄/" + (month + 1) + "月" + (day + 1) + ".png";
                    Card[month][day] = ImageIO.read(new File(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /* 1月1.pngから12月4.pngまでを読み込む*/
        for(int month = 0; month < 12; month++){
            for(int day = 0; day < 4; day++) {
                try{
                    String filePath = "札絵柄反対向き/" + (month + 1) + "月" + (day + 1) + ".png";
                    rotateCard[month][day] = ImageIO.read(new File(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /* 札の大きさを計算する*/
        cardWidth = cardBack.getWidth(frame);
        cardHeight = cardBack.getHeight(frame);
        /* パネルの作成とアニメーション設定*/
        panel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Graphics2D g2 = (Graphics2D) g;
                /*FadeInOut用白い画像*/
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                /* 背景の壁の描画*/
                g.drawImage(stage, 0, 0, frame.getWidth(), frame.getHeight(), this);
                /* 山札の描画*/
                for(int i = 0; i < cardRemain; i++){
                    g.drawImage(cardBack, 540 - i, 540 - cardHeight / 2 - i, frame);
                }
                /* プレイヤー情報の表示*/
                g.drawImage(Card[cardNumber / 4][cardNumber % 4], 20, 880, frame);
                Font currentFont = g.getFont();
                Font largerFont = currentFont.deriveFont(currentFont.getSize2D() * 5);
                g.setFont(largerFont);
                g.drawString(playerName, 30 + cardWidth, 950);
                g.drawString(String.format("Lv.%d",experiment / 50), 30 + cardWidth, 1010);
                /* 相手のプレイヤー情報の表示*/
                g.drawImage(Card[opponentIconNumber / 4][opponentIconNumber % 4], 1580 - cardWidth, 220 - cardHeight, frame);
                g.setFont(largerFont);
                g.drawString(originalOpponentPlayerName, 1590, 290 - cardHeight);
                g.drawString(String.format("Lv.%d",opponentExperience / 50), 1590, 350 - cardHeight);
                /* 場のカードの表示*/
                for(int i = 0; i <= cardSize; i++){
                    if(currentBoardListBoolean.get(i % 2).get(i / 2)) {
                        g.drawImage(Card[currentBoardList.get(i % 2).get(i / 2) / 4][currentBoardList.get(i % 2).get(i / 2) % 4], 660 + (i / 2 + 1) * (600 / (((currentCardSize + 1) / 2) + 1)), 540 - cardHeight / 2 + 110 * (i % 2 == 0 ? -1 : 1), frame);
                    }
                }
                /* 手札カードの表示*/
                for(int i = 0; i < 8; i++) {
                    if(currentPlayerHandListBoolean.get(i)) {
                        g.drawImage(Card[currentPlayerHandList.get(i) / 4][currentPlayerHandList.get(i) % 4], 520 + i * 120, 860, frame);
                    }
                }
                /* 相手カードの表示*/
                for(int i = 0; i < 8; i++) {
                    if(currentOpponentHandListBoolean.get(i)) {
                        g.drawImage(cardBack, 480 + i * 120, 80, frame);
                    }
                }
                /* 場のカードの表示*/
                for(int i = 0; i <= cardSize; i++){
                    if(boardListBoolean.get(i % 2).get(i / 2)) {
                        g.drawImage(Card[boardList.get(i % 2).get(i / 2) / 4][boardList.get(i % 2).get(i / 2) % 4], 660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 540 - cardHeight / 2 + 110 * (i % 2 == 0 ? -1 : 1), frame);
                    }
                }
                /* 手札カードの表示*/
                for(int i = 0; i < 8; i++) {
                    if(playerHandListBoolean.get(i)) {
                        g.drawImage(Card[playerHandList.get(i) / 4][playerHandList.get(i) % 4], 520 + i * 120, 860, frame);
                    }
                }
                /* 相手カードの表示*/
                for(int i = 0; i < 8; i++) {
                    if(opponentHandListBoolean.get(i)) {
                        g.drawImage(cardBack, 480 + i * 120, 80, frame);
                    }
                }
                /* 取り札の表示*/
                for(int i = 0; i < isGokou.size(); i++) {
                    if(isGokou.get(i)) {
                        g.drawImage(Card[gokou.get(i) / 4][gokou.get(i) % 4], 1500 + i * 70, 320, frame);
                    }
                }
                for(int i = 0; i < isTane.size(); i++) {
                    if(isTane.get(i)) {
                        g.drawImage(Card[tane.get(i) / 4][tane.get(i) % 4], 1500 + i * 35, 490, frame);
                    }
                }
                for(int i = 0; i < isTann.size(); i++) {
                    if(isTann.get(i)) {
                        g.drawImage(Card[tann.get(i) / 4][tann.get(i) % 4], 1500 + i * 32, 660, frame);
                    }
                }
                for(int i = 0; i < isKasu.size(); i++) {
                    if(isKasu.get(i)) {
                        g.drawImage(Card[kasu.get(i) / 4][kasu.get(i) % 4], 1500 + i * 12, 830, frame);
                    }
                }
                /* 相手の取り札の表示*/
                for(int i = 0; i < opponentIsGokou.size(); i++) {
                    if(opponentIsGokou.get(i)) {
                        g.drawImage(rotateCard[gokou.get(i) / 4][gokou.get(i) % 4], 300 - i * 70, 600, frame);
                    }
                }
                for(int i = 0; i < opponentIsTane.size(); i++) {
                    if(opponentIsTane.get(i)) {
                        g.drawImage(rotateCard[tane.get(i) / 4][tane.get(i) % 4], 300 - i * 35, 430, frame);
                    }
                }
                for(int i = 0; i < opponentIsTann.size(); i++) {
                    if(opponentIsTann.get(i)) {
                        g.drawImage(rotateCard[tann.get(i) / 4][tann.get(i) % 4], 300 - i * 32, 260, frame);
                    }
                }
                for(int i = 0; i < opponentIsKasu.size(); i++) {
                    if(opponentIsKasu.get(i)) {
                        g.drawImage(rotateCard[kasu.get(i) / 4][kasu.get(i) % 4], 300 - i * 12, 90, frame);
                    }
                }
                /* 動くカードの表示*/
                if(isMoveCard) {
                    g.drawImage(moveCard, (int)moveX, (int)moveY, frame);
                }
                /* 画面を薄暗くする*/
                if(darkenScreen && isStarted) {
                    g.drawImage(darkImage, 0, 0, frame);
                }
                /* 手札カードの表示*/
                for(int i = 0; i < 8; i++) {
                    if(playerHandListBoolean.get(i) && isHandSelect) {
                        g.drawImage(Card[playerHandList.get(i) / 4][playerHandList.get(i) % 4], 520 + i * 120, 860, frame);
                    }
                }
                /* 場のカードの表示*/
                for(int i = 0; i < cardSize; i++){
                    if(flushBoardListBoolean.get(i % 2).get(i / 2) && isFieldSelect) {
                        g.drawImage(Card[boardList.get(i % 2).get(i / 2) / 4][boardList.get(i % 2).get(i / 2) % 4], 660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 540 - cardHeight / 2 + 110 * (i % 2 == 0 ? -1 : 1), frame);
                    }
                }
                /* めくられたカードの表示*/
                if(isCardOpen){
                    g.drawImage(Card[playerHandList.get(playerHandList.size()-1)/4][playerHandList.get(playerHandList.size()-1)%4], 540 - cardRemain, 540 - cardHeight / 2 - cardRemain, frame);
                }
                /* 手札を光らせる*/
                for (int i = 0; i < flushPlayerHandListBoolean.size(); i++) {
                    if (flushPlayerHandListBoolean.get(i) && isHandSelect) {
                        Rectangle rect = playerHandRectangles[i];
                        float opacity = (float) Math.abs(Math.sin(System.currentTimeMillis() * 0.001));
                        g2.setColor(new Color(1.0f, 1.0f, 0.0f));
                        Stroke oldStroke = g2.getStroke();
                        g2.setStroke(new BasicStroke(5));
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                        g2.draw(rect);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        g2.setStroke(oldStroke);
                    }
                }
                /* 場を光らせる*/
                for (int i = 0; i < cardSize; i++) {
                    if (flushBoardListBoolean.get(i % 2).get(i / 2) && isFieldSelect) {
                        Rectangle rect = boardRectangles[i % 2][i / 2];
                        float opacity = (float) Math.abs(Math.sin(System.currentTimeMillis() * 0.001));
                        g2.setColor(new Color(0.0f, 1.0f, 1.0f));
                        Stroke oldStroke = g2.getStroke();
                        g2.setStroke(new BasicStroke(5));
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                        g2.draw(rect);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        g2.setStroke(oldStroke);
                    }
                }
                /* ターン表示*/
                if(!isMyTurn){
                    float opacity = (float) Math.abs(Math.sin(System.currentTimeMillis() * 0.001));
                    g2.setColor(new Color(1.0f, 1.0f, 1.0f));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    if(darkenScreen && isStarted)g2.drawString("あいてがプレイ中", 760, 550);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                /* ターン表示の裏*/
                if(isMyTurn){
                    g.setColor(Color.BLUE);
                    g.fillRect(moveRectTurnX, 440, 1920 * 3, 200);
                }
                else {
                    g.setColor(Color.RED);
                    g.fillRect(-3000-moveRectTurnX, 440, 1920 * 3, 200);
                }
                g.setColor(Color.BLACK);
                /* ターン表示*/
                if(isMyTurn)g.drawString("あなたのターン", textTurnX - 100, 550);
                else g.drawString("あいてのターン", 1800-textTurnX, 550);
                /* 対戦開始の合図の裏*/
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(moveRectX, 440, 1920 * 3, 200);
                g.setColor(Color.BLACK);
                /* 対戦開始の合図*/
                g.drawString("第一局", textX, 520);
                g.drawString("対戦開始", textX, 580);
                /* こいこいの表示*/
                if(isKOIKOI){
                    g.drawString("こいこいしない", 420, 550);
                    g.drawString("こいこいする", 1220, 550);
                }
                /* 設定ボタンアイコンの表示*/
                int settingsWidth = (int) (settingsIcon.getWidth(null) * 0.1);
                int settingsHeight = (int) (settingsIcon.getHeight(null) * 0.1);
                g.drawImage(settingsIcon, frame.getWidth() - settingsWidth - 10, 10, settingsWidth, settingsHeight, frame);
                /* 役表示アイコンの表示*/
                int roleWidth = (int) (roleIcon.getWidth(null) * 0.1);
                int roleHeight = (int) (roleIcon.getHeight(null) * 0.1);
                g.drawImage(roleIcon, frame.getWidth() - roleWidth - 10, 140, roleWidth, roleHeight, frame);
                /* 設定ウィンドウの表示*/
                if(isSettingOpen){
                    int settingsWindowWidth = settingsWindowImage.getWidth(null);
                    int settingsWindowHeight = settingsWindowImage.getHeight(null);
                    g.drawImage(settingsWindowImage, 0, 0, frame);
                    g.drawImage(closeIcon, frame.getWidth() - (int)(settingsWindowWidth * 0.05) - 10, 50, (int)(settingsWindowWidth * 0.03), (int)(settingsWindowHeight * 0.05), frame);
                    int returnLobbyButtonWidth = returnLobbyButtonImage.getWidth(null);
                    int returnLobbyButtonHeight = returnLobbyButtonImage.getHeight(null);
                    g.drawImage(returnLobbyButtonImage, frame.getWidth() * 3 / 8, frame.getHeight() * 3 / 4, frame.getWidth() / 4, frame.getHeight() / 8, frame);
                    g.drawImage(checkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    g.drawImage(checkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    if (checkMarkVisible1) {
                        g.drawImage(checkMarkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    }
                    if (checkMarkVisible2) {
                        g.drawImage(checkMarkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    }
                }
                /* ボールの描画*/
                for (Ball ball : balls) {
                    g.drawImage(cherryBlossom, ball.x, ball.y, 30, 30, frame);
                }
            }
        };  
        frame.setContentPane(panel);
        frame.revalidate();
        /* ターン更新タイマー*/
        Timer updateTurnTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    client.fetchGameState();
                    turn = gameController.getCurrentTurn();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if(turn % 2 + 1 == playerId){
                    if(!isMyTurn){
                        darkenScreen = false;
                        isCardOpen = false;
                        isMyTurn = true;
                        isBoardSelect = false;
                        isHandSelected = false;
                        List<Integer> takeOpponentCard;
                        if(playerId == 2) takeOpponentCard = gameController.getPlayer1Captures();
                        else takeOpponentCard = gameController.getPlayer2Captures();
                        for(int i=0;i<takeOpponentCard.size();i++){
                            cardState[takeOpponentCard.get(i)] = 5;
                        }
                        updateState();
                        
                    }
                }
                else{
                    if(isMyTurn){
                        isMyTurn = false;
                        darkenScreen = true;
                    }
                }
            }
        });
        updateTurnTimer.start();
        /* フェードイン用タイマー*/
        Timer fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.06f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    ((Timer) e.getSource()).stop();
                    dealCards();
                }
                panel.repaint();
            }
        });
        fadeTimer.start();
        /* 当たり判定の作成*/
        for(int i = 0;i < cardSize; i++) {
            boardRectangles[i % 2][i / 2] = new Rectangle(660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 540 - cardHeight / 2 + 110 * (i % 2 == 0 ? -1 : 1), cardWidth, cardHeight);
        }
        for(int i = 0;i < 8; i++) {
            playerHandRectangles[i] = new Rectangle(520 + i * 120, 860, cardWidth, cardHeight);
        }
        settingsButtonBounds = new Rectangle(
            frame.getWidth() - (int) (settingsIcon.getWidth(null) * 0.1) - 10, 
            10,
            (int) (settingsIcon.getWidth(null) * 0.1), 
            (int) (settingsIcon.getHeight(null) * 0.1)
        );
        closeButtonBounds = new Rectangle(
            frame.getWidth() - (int) (settingsWindowImage.getWidth(null) * 0.05) - 10,
            50,
            (int) (settingsWindowImage.getWidth(null) * 0.03), 
            (int) (settingsWindowImage.getHeight(null) * 0.05)
        );
        returnLobbyButtonBounds = new Rectangle(
            frame.getWidth() * 3 / 8, 
            frame.getHeight() * 3 / 4, 
            frame.getWidth() / 4, 
            frame.getHeight() / 8
        );
        checkBox1Bounds = new Rectangle(frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16);
        checkBox2Bounds = new Rectangle(frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16);
        ENDButtonBounds = new Rectangle(420, 440, 400, 100);
        KOIKOIButtonBounds = new Rectangle(1220, 440, 400, 100);
        /* スライダーの値をPreferencesから取得*/
        int slider1Value = AppPreferences.getInt("BGM", 50);
        int slider2Value = AppPreferences.getInt("SE", 50);
        /* チェックマークの値をPreferencesから取得*/
        checkMarkVisible1 = AppPreferences.getBoolean("checkMarkVisible1", true);
        checkMarkVisible2 = AppPreferences.getBoolean("checkMarkVisible2", true);
        /* クリックイベントを設定*/
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 桜の描画*/
                for (int i = 0; i < 3; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double speed = 5 + random.nextDouble() * 4;
                    int dx = (int) (Math.cos(angle) * speed);
                    int dy = (int) (Math.sin(angle) * speed);
                    balls.add(new Ball(e.getX(), e.getY(), dx, dy));
                }
                boolean fieldSelected = !isFieldSelect;
                /* プレイヤーの場の選択*/
                for(int i = 0;i < boardList1.size(); i++){
                    if(!isSettingOpen && flushBoardListBoolean.get(i % 2).get(i / 2) && isFieldSelect && boardRectangles[i % 2][i / 2].contains(e.getPoint())) {
                        isFieldSelect = false;
                        fieldSelected = true;
                        try {
                            if(!isCardOpen){
                                client.playCard(playerHandList.get(playerHandSelect), playerId, boardList.get(i % 2).get(i / 2));
                                client.fetchGameState();
                            }else{
                                client.playCard(playerHandList.get(playerHandList.size()-1), playerId, boardList.get(i % 2).get(i / 2));
                                client.fetchGameState();
                            }
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }
                        if(!isCardOpen)cardState[playerHandList.get(playerHandSelect)] = 4;
                        else cardState[playerHandList.get(playerHandList.size()-1)] = 4;
                        int counter = 0;
                        for(int j = 0;j < cardSize; j++){
                            if(flushBoardListBoolean.get(j % 2).get(j / 2))counter++;
                        }
                        if(counter==3){
                            for(int j = 0;j < cardSize; j++){
                                if(flushBoardListBoolean.get(j % 2).get(j / 2))cardState[boardList.get(j % 2).get(j / 2)] = 4;
                            }
                        }
                        else{
                            cardState[boardList.get(i % 2).get(i / 2)] = 4;
                        }
                        if(!isCardOpen)playerSelected(playerHandSelect, i);
                        else boardSelect(playerHandList.get(playerHandList.size()-1), i);
                    }
                }
                /* プレイヤーの手札選択*/
                for(int i = 0;i < playerHandListBoolean.size(); i++){
                    if(!isSettingOpen && playerHandListBoolean.get(i) && isHandSelect && playerHandRectangles[i].contains(e.getPoint())) {
                        isHandSelect = false;
                        playerHandSelect = i;
                        if(flushPlayerHandListBoolean.get(i)) {
                            isFieldSelect = true;
                            updateFlushField(playerHandList.get(playerHandSelect));
                        }
                        else{
                            try {
                                client.playCard(playerHandList.get(playerHandSelect), playerId, -1);
                                client.fetchGameState();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            cardState[playerHandList.get(playerHandSelect)] = 4;
                            playerSelected(playerHandSelect, -1);
                        }
                    }
                }
                /* 設定ボタンの座標とクリック位置の確認*/
                if (!isSettingOpen && settingsButtonBounds.contains(e.getPoint())) {
                    /* 設定ボタンがクリックされた場合*/
                    isSettingOpen = true;
                    /* スライダーの値をPreferencesから取得*/
                    int slider1Value = AppPreferences.getInt("BGM", 50);
                    int slider2Value = AppPreferences.getInt("SE", 50);
                    /* チェックマークの値をPreferencesから取得*/
                    checkMarkVisible1 = AppPreferences.getBoolean("checkMarkVisible1", true);
                    checkMarkVisible2 = AppPreferences.getBoolean("checkMarkVisible2", true);
                    slider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, slider1Value);
                    slider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, slider2Value);
                    panel.setLayout(null);
                    slider1.setBounds(frame.getWidth() / 2 + 40, frame.getHeight() / 2 - 75, 500, 150);
                    slider2.setBounds(frame.getWidth() / 2 + 40, frame.getHeight() / 2 - 275, 500, 150);
                    slider1.setMajorTickSpacing(25);
                    slider2.setMajorTickSpacing(25);
                    slider1.setMinorTickSpacing(5);
                    slider2.setMinorTickSpacing(5);
                    slider1.setBackground(new Color(0, 0, 0, 0));
                    slider2.setBackground(new Color(0, 0, 0, 0));
                    slider1.setOpaque(false);
                    slider2.setOpaque(false);
                    panel.add(slider1);
                    panel.add(slider2);
                    panel.revalidate();
                    panel.repaint();
                    slider1.addChangeListener(e1 -> {AppPreferences.putInt("BGM", slider1.getValue());});
                    slider1.addChangeListener(e1 -> {AppPreferences.putInt("SE", slider1.getValue());});
                }
                else if(isSettingOpen && !isReturnLobby && closeButtonBounds.contains(e.getPoint())) {
                    /* 設定が閉じられた場合、アニメーションを再開する準備 */
                    isSettingOpen = false;
                    panel.remove(slider1);
                    panel.remove(slider2);
                    panel.revalidate();
                    panel.repaint();
                }
                else if(isSettingOpen && returnLobbyButtonBounds.contains(e.getPoint())){
                    /* タイトルに戻るボタンが押された場合*/
                    isReturnLobby = true;
                    try {
                        controller.getGameClient().disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    C_Lobby LobbyPanel = new C_Lobby(frame);
                }
                else if (isSettingOpen && checkBox1Bounds.contains(e.getPoint())) {
                    /* チェックボックス1がクリックされた場合*/
                    checkMarkVisible1 = !checkMarkVisible1;
                    AppPreferences.putBoolean("checkMarkVisible1", checkMarkVisible1);
                    panel.repaint();
                }
                else if (isSettingOpen && checkBox2Bounds.contains(e.getPoint())) {
                    /* チェックボックス2がクリックされた場合*/
                    checkMarkVisible2 = !checkMarkVisible2;
                    AppPreferences.putBoolean("checkMarkVisible2", checkMarkVisible2);
                    panel.repaint();
                }
                else if(!isSettingOpen && isFieldSelect && !fieldSelected && !isCardOpen) {
                    isFieldSelect = false;
                    isHandSelect = true;
                    for(int i = 0;i < boardList1.size();i++){
                        flushBoardListBoolean.get(i % 2).set(i / 2, false);
                    }
                }
                else if (isKOIKOI && KOIKOIButtonBounds.contains(e.getPoint())) {
                    try{
                        client.sendNextTurnMessage("KOIKOI");
                    } catch (Exception ev){
                        ev.printStackTrace();
                    }
                    isKOIKOI = false;
                }
                else if (isKOIKOI && ENDButtonBounds.contains(e.getPoint())) {
                    try{
                        client.sendNextTurnMessage("END");
                    } catch (Exception ev){
                        ev.printStackTrace();
                    }
                    isKOIKOI = false;
                    try {
                        //controller.getGameClient().disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    C_Lobby LobbyPanel = new C_Lobby(frame);
                }
            }
        });
        /* タイマーイベント処理*/
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Iterator<Ball> iterator = balls.iterator();
                while (iterator.hasNext()) {
                    Ball ball = iterator.next();
                    ball.x += ball.dx;
                    ball.y += ball.dy;
                    if (ball.x < -960 || ball.x > frame.getWidth() + 960 ||
                        ball.y < -540 || ball.y > frame.getHeight() + 540) {
                        iterator.remove();
                    }
                }
                panel.repaint();
            }
        });
        timer.start();
    }
    /* 取り札のカードの座標を取り出す*/
    public int takenMyCardPositionX(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                return 1500 + i * 70;
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                return 1500 + i * 35;
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                return 1500 + i * 32;
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                return 1500 + i * 12;
            }
        }
        return -1;
    }

    public int takenMyCardPositionY(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                return 320;
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                return 490;
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                return 660;
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                return 830;
            }
        }
        return -1;
    }
    /* カード取った*/
    public void takenMyCard(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                isGokou.set(i, true);
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                isTane.set(i, true);
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                isTann.set(i, true);
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                isKasu.set(i, true);
            }
        }
    }
    /* カード取った*/
    public void takenOpponentCard(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                opponentIsGokou.set(i, true);
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                opponentIsTane.set(i, true);
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                opponentIsTann.set(i, true);
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                opponentIsKasu.set(i, true);
            }
        }
    }
    /* 山札からカードをめくる*/
    public void cardOpen() {
        int cntnumber = 0;
        for(int i=0;i<cardSize;i++){
            if(playerHandList.get(playerHandList.size()-1) / 4 == boardList.get(i % 2).get(i / 2) / 4 && boardListBoolean.get(i % 2).get(i / 2)){
                flushBoardListBoolean.get(i % 2).set(i / 2, true);
                cntnumber++;
            }
        }
        if(cntnumber == 0){
            isCardOpen = false;
            moveX = 540 - cardRemain;
            moveY = 540 - cardRemain;
            cardRemain--;
            moveCard = Card[playerHandList.get(playerHandList.size() - 1) / 4][playerHandList.get(playerHandList.size() - 1) % 4];
            targetMoveX = 660 + (cardSize / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
            targetMoveY = 540 - cardHeight / 2 + 110 * (cardSize % 2 == 0 ? -1 : 1);
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            int[] currentSteps = {0};
            try{
                client.playCard(playerHandList.get(playerHandList.size() - 1), playerId, -1);
                client.fetchGameState();
            } catch (Exception ev) {
                ev.printStackTrace();
            }
            Timer firstTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    boardList.get(cardSize % 2).set(cardSize / 2, playerHandList.get(playerHandList.size() - 1));
                    boardListBoolean.get(cardSize % 2).set(cardSize / 2, true);
                    updateState();
                }
                panel.repaint();
            });
            firstTimer.start();
            isMoveCard = true;
        }
        else{
            darkenScreen = true;
            isCardOpen = true;
            isFieldSelect = true;
        }
    }
    /* カードのUIを更新する*/
    public void updateState() {
        for(int i=0;i<18;i++){
            currentBoardListBoolean.get(i % 2).set(i / 2, boardListBoolean.get(i % 2).get(i / 2));
            currentBoardList.get(i % 2).set(i / 2, boardList.get(i % 2).get(i / 2));
        }
        currentOpponentHandListBoolean = new ArrayList<>();
        for (boolean b : opponentHandListBoolean) {
            currentOpponentHandListBoolean.add(b);
        }
        currentPlayerHandListBoolean = new ArrayList<>();
        for (boolean b : playerHandListBoolean) {
            currentPlayerHandListBoolean.add(b);
        }
        currentOpponentHandList = new ArrayList<>(opponentHandList);
        currentPlayerHandList = new ArrayList<>(playerHandList);
        currentCardSize = cardSize;
        updateCard();
        for(int i=0;i<18;i++){
            boardListBoolean.get(i % 2).set(i / 2, false);
        }
        for(int i=0;i<8;i++){
            opponentHandListBoolean.set(i, false);
        }
        for(int i=0;i<8;i++){
            playerHandListBoolean.set(i, false);
        }
        for(int i=0;i<18;i++){
            flushBoardListBoolean.get(i % 2).set(i / 2, false);
        }
        int[] cnt = {0};
        Timer stateTimer = new Timer(16, e -> {
            ((Timer) e.getSource()).stop();
            do{
                cardNumbers = -1;
                if(cnt[0] <= currentCardSize){
                    if(currentBoardListBoolean.get(cnt[0] % 2).get(cnt[0] / 2)){
                        cardNumbers = currentBoardList.get(cnt[0] % 2).get(cnt[0] / 2);
                        moveX = 660 + (cnt[0] / 2 + 1) * (600 / (((currentCardSize + 1) / 2) + 1));
                        moveY = 540 - cardHeight / 2 + 110 * (cnt[0] % 2 == 0 ? -1 : 1);
                        currentBoardListBoolean.get(cnt[0] % 2).set(cnt[0] / 2, false);
                    }
                }
                else if(cnt[0] - currentCardSize - 1 < currentPlayerHandList.size()) {
                    if(currentPlayerHandListBoolean.get(cnt[0] - currentCardSize - 1)){
                        cardNumbers = currentPlayerHandList.get(cnt[0] - currentCardSize - 1);
                        moveX = 520 + (cnt[0] - currentCardSize - 1) * 120;
                        moveY = 860;
                        currentPlayerHandListBoolean.set(cnt[0] - currentCardSize - 1, false);
                    }
                }
                else if(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size() < currentOpponentHandList.size()){
                    if(currentOpponentHandListBoolean.get(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size())){
                        cardNumbers = currentOpponentHandList.get(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size());
                        moveX = 480 + (cnt[0] - currentCardSize - 1 - currentPlayerHandList.size()) * 120;
                        moveY = 80;
                        currentOpponentHandListBoolean.set(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size(), false);
                    }
                }
                else {
                    try{
                        client.fetchGameState();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    updateRectangles();
                    List<Integer> player1Captures = gameController.getPlayer1Captures();
                    List<Integer> player2Captures = gameController.getPlayer2Captures();
                    for(int i=0;i<player1Captures.size();i++){
                        if(playerId == 1) takenMyCard(player1Captures.get(i));
                        if(playerId == 2) takenOpponentCard(player1Captures.get(i));
                    }
                    for(int i=0;i<player2Captures.size();i++){
                        if(playerId == 1) takenOpponentCard(player2Captures.get(i));
                        if(playerId == 2) takenMyCard(player2Captures.get(i));
                    }
                    if(!isCardOpen && !isBoardSelect && isHandSelected){
                        /*手札から選択*/
                        isCardOpen = true;
                        isBoardSelect = true;
                        cardRemain--;
                        cardOpen();
                    }
                    else if(!isCardOpen && !isBoardSelect && !isHandSelected){
                        /*相手側*/
                        darkenScreen = true;
                        isHandSelected = true;
                        if(isMyTurn){
                            isHandSelect = true;
                        }
                        if(cardSize > 0)boardListBoolean.get((cardSize - 1) % 2).set((cardSize - 1) / 2, true);
                        updatePlayerHandFlush();
                    }
                    else{
                        /*二回目*/
                        isKOIKOI = gameController.getIsKoikoi();
                    }
                    break;
                }
                if(cardNumbers == -1){
                    cnt[0]++;
                    continue;
                }
                else if(cardState[cardNumbers] == 3)moveCard = cardBack;
                else moveCard = Card[cardNumbers / 4][cardNumbers % 4];
                for(int j = 0;j < cardSize; j++){
                    if(boardList.get(j % 2).get(j / 2) == cardNumbers) {
                        targetMoveX = 660 + (j / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
                        targetMoveY = 540 - cardHeight / 2 + 110 * (j % 2 == 0 ? -1 : 1);
                    }
                }
                for(int j = 0;j < playerHandList.size(); j++){
                    if(playerHandList.get(j) == cardNumbers) {
                        targetMoveX = 520 + j * 120;
                        targetMoveY = 860;
                    }
                }
                for(int j = 0;j < opponentHandList.size(); j++){
                    if(opponentHandList.get(j) == cardNumbers) {
                        targetMoveX = 480 + j * 120;
                        targetMoveY = 80;
                    }
                }
                for(int j = 0; j < gokou.size(); j++) {
                    if(cardState[cardNumbers] == 4 && gokou.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 70;
                        targetMoveY = 320;
                    }
                }
                for(int j = 0; j < tane.size(); j++) {
                    if(cardState[cardNumbers] == 4 && tane.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 35;
                        targetMoveY = 460;
                    }
                }
                for(int j = 0; j < tann.size(); j++) {
                    if(cardState[cardNumbers] == 4 && tann.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 32;
                        targetMoveY = 690;
                    }
                }
                for(int j = 0; j < kasu.size(); j++) {
                    if(cardState[cardNumbers] == 4 && kasu.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 12;
                        targetMoveY = 830;
                    }
                }
                for(int j = 0; j < gokou.size(); j++) {
                    if(cardState[cardNumbers] == 5 && gokou.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 70;
                        targetMoveY = 600;
                    }
                }
                for(int j = 0; j < tane.size(); j++) {
                    if(cardState[cardNumbers] == 5 && tane.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 35;
                        targetMoveY = 430;
                    }
                }
                for(int j = 0; j < tann.size(); j++) {
                    if(cardState[cardNumbers] == 5 && tann.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 32;
                        targetMoveY = 240;
                    }
                }
                for(int j = 0; j < kasu.size(); j++) {
                    if(cardState[cardNumbers] == 5 && kasu.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 12;
                        targetMoveY = 90;
                    }
                }
                deltaX = (targetMoveX - moveX) / (double)steps;
                deltaY = (targetMoveY - moveY) / (double)steps;
                if(moveX == targetMoveX && moveY == targetMoveY){
                    for(int j = 0;j < cardSize; j++){
                        if(boardList.get(j % 2).get(j / 2) == cardNumbers) {
                            boardListBoolean.get(j % 2).set(j / 2, true);
                        }
                    }
                    for(int j = 0;j < playerHandList.size(); j++){
                        if(playerHandList.get(j) == cardNumbers) {
                            playerHandListBoolean.set(j, true);
                        }
                    }
                    for(int j = 0;j < opponentHandList.size(); j++){
                        if(opponentHandList.get(j) == cardNumbers) {
                            opponentHandListBoolean.set(j, true);
                        }
                    }
                    if(cardState[cardNumbers] == 4)takenMyCard(cardNumbers);
                    if(cardState[cardNumbers] == 5)takenOpponentCard(cardNumbers);
                }
            }while(moveX == targetMoveX && moveY == targetMoveY);
            if(cardNumbers!=-1) {
                int[] currentSteps = {0};
                Timer timer = new Timer(16, ex -> {
                    moveX += deltaX;
                    moveY += deltaY;
                    currentSteps[0]++;
                    if (currentSteps[0] == steps) {
                        moveX = targetMoveX;
                        moveY = targetMoveY;
                        ((Timer) ex.getSource()).stop();
                        isMoveCard = false;
                        for(int j = 0;j < cardSize; j++){
                            if(boardList.get(j % 2).get(j / 2) == cardNumbers) {
                                boardListBoolean.get(j % 2).set(j / 2, true);
                            }
                        }
                        for(int j = 0;j < playerHandList.size(); j++){
                            if(playerHandList.get(j) == cardNumbers) {
                                playerHandListBoolean.set(j, true);
                            }
                        }
                        for(int j = 0;j < opponentHandList.size(); j++){
                            if(opponentHandList.get(j) == cardNumbers) {
                                opponentHandListBoolean.set(j, true);
                            }
                        }
                        if(cardState[cardNumbers] == 4)takenMyCard(cardNumbers);
                        if(cardState[cardNumbers] == 5)takenOpponentCard(cardNumbers);
                        cnt[0]++;
                        ((Timer) e.getSource()).start();
                    }
                    panel.repaint();
                });
                timer.start();
                isMoveCard = true;
            }
        });
        stateTimer.start();
    }
    /* 当たり判定を更新*/
    public void updateRectangles(){
        for(int i = 0;i < cardSize; i++) {
            if(i + 2 >= cardSize || (i + 2 < cardSize && boardList.get(i % 2).get(i / 2) / 4 != boardList.get(i % 2).get(i / 2 + 1) / 4))boardRectangles[i % 2][i / 2] = new Rectangle(660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 540 - cardHeight / 2 + 110 * (i % 2 == 0 ? -1 : 1), cardWidth, cardHeight);
            else boardRectangles[i % 2][i / 2] = new Rectangle(660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 540 - cardHeight / 2 + 110 * (i % 2 == 0 ? -1 : 1), Math.min(cardWidth,600 / (((cardSize + 1) / 2) + 1)), cardHeight);
        }
    }
    /* プレイヤーが山札の上と場のカードを選んだ*/
    public void boardSelect(int cardId, int boardSelected) {
        darkenScreen = false;
        isCardOpen = false;
        moveCard = Card[cardId / 4][cardId % 4];
        moveX = 520 - cardRemain;
        moveY = 520 - cardRemain;
        cardRemain--;
        int[] currentSteps = {0};
        targetMoveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
        targetMoveY = 540 - cardHeight / 2 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
        deltaX = (targetMoveX - moveX) / (double)steps;
        deltaY = (targetMoveY - moveY) / (double)steps;
        Timer thirdTimer = new Timer(16, ex -> {
            moveX += deltaX;
            moveY += deltaY;
            currentSteps[0]++;
            if (currentSteps[0] == steps) {
                moveX = targetMoveX;
                moveY = targetMoveY;
                ((Timer) ex.getSource()).stop();
                takenMyCard(boardList.get(boardSelected % 2).get(boardSelected / 2));
                updateState();
            }
            panel.repaint();
        });
        Timer secondTimer = new Timer(16, ex -> {
            moveX += deltaX;
            moveY += deltaY;
            currentSteps[0]++;
            if (currentSteps[0] == steps) {
                moveX = targetMoveX;
                moveY = targetMoveY;
                ((Timer) ex.getSource()).stop();
                takenMyCard(cardId);
                boardListBoolean.get(boardSelected % 2).set(boardSelected / 2, false);
                moveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
                moveY = 540 - cardHeight / 2 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
                moveCard = Card[boardList.get(boardSelected % 2).get(boardSelected / 2) / 4][boardList.get(boardSelected % 2).get(boardSelected / 2) % 4];
                targetMoveX = takenMyCardPositionX(boardList.get(boardSelected % 2).get(boardSelected / 2));
                targetMoveY = takenMyCardPositionY(boardList.get(boardSelected % 2).get(boardSelected / 2));
                deltaX = (targetMoveX - moveX) / (double)steps;
                deltaY = (targetMoveY - moveY) / (double)steps;
                currentSteps[0] = 0;
                thirdTimer.start();
            }
            panel.repaint();
        });
        Timer firstTimer = new Timer(16, ex -> {
            moveX += deltaX;
            moveY += deltaY;
            currentSteps[0]++;
            if (currentSteps[0] == steps) {
                moveX = targetMoveX;
                moveY = targetMoveY;
                ((Timer) ex.getSource()).stop();
                targetMoveX = takenMyCardPositionX(cardId);
                targetMoveY = takenMyCardPositionY(cardId);
                deltaX = (targetMoveX - moveX) / (double)steps;
                deltaY = (targetMoveY - moveY) / (double)steps;
                currentSteps[0] = 0;
                secondTimer.start();
            }
            panel.repaint();
        });
        firstTimer.start();
        isMoveCard = true;
    }
    /* プレイヤーが手札と場のカードを選んだ*/
    public void playerSelected(int playerHandSelected, int boardSelected) {
        darkenScreen = false;
        playerHandListBoolean.set(playerHandSelected, false);
        moveCard = Card[playerHandList.get(playerHandSelected) / 4][playerHandList.get(playerHandSelected) % 4];
        moveX = 520 + playerHandSelected * 120;
        moveY = 860;
        int[] currentSteps = {0};
        if(boardSelected == -1){
            targetMoveX = 660 + (cardSize / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
            targetMoveY = 540 - cardHeight / 2 + 110 * (cardSize % 2 == 0 ? -1 : 1);
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            Timer firstTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    boardList.get(cardSize % 2).set(cardSize / 2, playerHandList.get(playerHandSelected));
                    boardListBoolean.get(cardSize % 2).set(cardSize / 2, true);
                    updateState();
                }
                panel.repaint();
            });
            firstTimer.start();
            isMoveCard = true;
        }
        else{
            targetMoveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
            targetMoveY = 540 - cardHeight / 2 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            Timer thirdTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    takenMyCard(boardList.get(boardSelected % 2).get(boardSelected / 2));
                    updateState();
                }
                panel.repaint();
            });
            Timer secondTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    takenMyCard(playerHandList.get(playerHandSelected));
                    boardListBoolean.get(boardSelected % 2).set(boardSelected / 2, false);
                    moveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
                    moveY = 540 - cardHeight / 2 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
                    moveCard = Card[boardList.get(boardSelected % 2).get(boardSelected / 2) / 4][boardList.get(boardSelected % 2).get(boardSelected / 2) % 4];
                    targetMoveX = takenMyCardPositionX(boardList.get(boardSelected % 2).get(boardSelected / 2));
                    targetMoveY = takenMyCardPositionY(boardList.get(boardSelected % 2).get(boardSelected / 2));
                    deltaX = (targetMoveX - moveX) / (double)steps;
                    deltaY = (targetMoveY - moveY) / (double)steps;
                    currentSteps[0] = 0;
                    thirdTimer.start();
                }
                panel.repaint();
            });
            Timer firstTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    targetMoveX = takenMyCardPositionX(playerHandList.get(playerHandSelected));
                    targetMoveY = takenMyCardPositionY(playerHandList.get(playerHandSelected));
                    deltaX = (targetMoveX - moveX) / (double)steps;
                    deltaY = (targetMoveY - moveY) / (double)steps;
                    currentSteps[0] = 0;
                    secondTimer.start();
                }
                panel.repaint();
            });
            firstTimer.start();
            isMoveCard = true;
        }
    }
    /* ゲームの情報を更新する*/
    public void updateCard() {
        boardList1 = gameController.getField();
        cardSize = boardList1.size();
        for(int i=0;i < cardSize;i++){
            boardList.get(i%2).set(i/2, boardList1.get(i));
            cardState[boardList1.get(i)] = 1;
        }
        playerHandList = gameController.getPlayer1Hands();
        for(int i=0;i<playerHandList.size();i++){
            cardState[playerHandList.get(i)] = 2;
        }
        opponentHandList = gameController.getPlayer2Hands();
        for(int i=0;i<opponentHandList.size();i++){
            cardState[opponentHandList.get(i)] = 3;
        }
    }
    /* 光らせる場のカードを更新する*/
    public void updateFlushField(int id) {
        for(int i = 0;i < boardList1.size(); i++){
            if(boardList.get(i % 2).get(i / 2) / 4 == id / 4 && boardListBoolean.get(i % 2).get(i / 2)){
                flushBoardListBoolean.get(i % 2).set(i / 2, true);
            }
        }
    }
    /* 光らせる手札を更新する*/
    public void updatePlayerHandFlush() {
        if(!isMyTurn) return;
        for(int i=0;i<8;i++){
            flushPlayerHandListBoolean.set(i, false);
        }
        for(int i=0;i<playerHandList.size();i++){
            for(int j=0;j<cardSize;j++){
                if(playerHandList.get(i) / 4 == boardList.get(j % 2).get(j / 2) / 4 && playerHandListBoolean.get(i)) flushPlayerHandListBoolean.set(i, true);
            }
        }
    }
    /* カードを配る*/
    public void dealCards(){
        int[] count = {0};
        moveCard = cardBack;
        /* 自分のターンかどうかを表示させる*/
        Timer turnTimer = new Timer(16, e -> {
            if(!stopTurnMovement)textTurnX += 80;
            if(!stopTurnMovement)moveRectTurnX += 160;
            if(textTurnX >= 860) {
                stopTurnMovement = true;
                Timer stopTimer = new Timer(1000, ev -> {
                    stopTurnMovement = false;
                });
                stopTimer.setRepeats(false);
                stopTimer.start();
            }
            panel.repaint();
            if(textTurnX >= 4000) {
                ((Timer)e.getSource()).stop();
                updatePlayerHandFlush();
                darkenScreen = true;
                isStarted = true;
                if(isMyTurn){
                    isHandSelect = true;
                }
            }
        });
        /* 対戦開始を表示する*/
        Timer startTimer = new Timer(16, e -> {
            if(!stopMovement)textX += 80;
            if(!stopMovement)moveRectX += 160;
            if(textX >= 860) {
                stopMovement = true;
                Timer stopTimer = new Timer(1000, ev -> {
                    stopMovement = false;
                });
                stopTimer.setRepeats(false);
                stopTimer.start();
            }
            panel.repaint();
            if(textX >= 4000) {
                ((Timer)e.getSource()).stop();
                playerId = gameController.getPlayerId();
                if(turn % 2 + 1 == playerId) isMyTurn = true;
                else isMyTurn = false;
                if(isMyTurn) isHandSelected = true;
                turnTimer.start();
            }
        });
        /* 相手の手札を配る*/
        Timer opponentHandDealCardsTimer = new Timer(16, e -> {
            targetMoveX = 480 + count[0] * 120;
            targetMoveY = 80;
            moveX = 540 - cardRemain;
            moveY = 540 - cardHeight / 2 - cardRemain;
            int[] currentSteps = {0};
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            ((Timer) e.getSource()).stop();
            Timer innerTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    opponentHandListBoolean.set(count[0] - 1, true);
                    if (count[0] == 8) {
                        count[0] = 0;
                        startTimer.start();
                    } else {
                        ((Timer) e.getSource()).start();
                    }
                }
                panel.repaint();
            });
            innerTimer.start();
            isMoveCard = true;
            cardRemain--;
            count[0]++;
        });
        /* プレイヤーの手札を配る*/
        Timer playerHandDealCardsTimer = new Timer(16, e -> {
            targetMoveX = 520 + count[0] * 120;
            targetMoveY = 860;
            moveX = 540 - cardRemain;
            moveY = 540 - cardHeight / 2 - cardRemain;
            int[] currentSteps = {0};
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            ((Timer) e.getSource()).stop();
            Timer innerTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    playerHandListBoolean.set(count[0] - 1, true);

                    if (count[0] == 8) {
                        count[0] = 0;
                        opponentHandDealCardsTimer.start();
                    } else {
                        ((Timer) e.getSource()).start();
                    }
                }
                panel.repaint();
            });

            innerTimer.start();
            isMoveCard = true;
            cardRemain--;
            count[0]++;
        });
        /* 場のカードを配る*/
        Timer fieldDealCardsTimer = new Timer(16, e -> {
            targetMoveX = 540 + (count[0] / 2 + 1) * 120;
            targetMoveY = 540 - cardHeight / 2 + 110 * (count[0] % 2 == 0 ? -1 : 1);
            moveX = 540 - cardRemain;
            moveY = 540 - cardHeight / 2 - cardRemain;
            int[] currentSteps = {0};
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            ((Timer) e.getSource()).stop();
            Timer innerTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    boardListBoolean.get((count[0] - 1) % 2).set((count[0] - 1) / 2, true);

                    if (count[0] == 8) {
                        count[0] = 0;
                        playerHandDealCardsTimer.start();
                    } else {
                        ((Timer) e.getSource()).start();
                    }
                }
                panel.repaint();
            });
            innerTimer.start();
            isMoveCard = true;
            cardRemain--;
            count[0]++;
        });
        fieldDealCardsTimer.start();
    }
    /* 対戦相手の情報の取得*/
    private void getOpponentInfo() {
        PlayerInfo opponentInfo = gameController.getOpponentInfo();
        originalOpponentPlayerName = opponentInfo.getPlayerName();
        opponentIconNumber = opponentInfo.getIconNum();
        opponentExperience = opponentInfo.getLevel();
    }
}