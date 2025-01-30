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

/* JPanelを継承したLobbyクラスを定義します。*/
class Lobby extends JPanel implements ActionListener {
    /* privateで宣言します。*/
    private Image wallImage;
    private Image rightDoorImage;
    private Image leftDoorImage;
    private Image lobbyImage;
    private Image settingsWindowImage;
    private Image closeIcon;
    private Image settingsIcon;
    private Image returnTItleButtonImage;
    private Image checkImage;
    private Image checkMarkImage;
    private Image cherryBlossom;
    private Image OnlineButton;
    private Image ComputerButton;
    private Image HowtoplayButton;
    private Image roleIcon;
    private Image playerIcon;
    private Image blackWindow;
    private Image playerInfo;
    private BufferedImage[][] Card = new BufferedImage[12][4];
    private boolean isSettingOpen = false;
    private boolean isPlayerIcon = false;
    private boolean checkMarkVisible1;
    private boolean checkMarkVisible2;
    private boolean isReturnTitle = false;
    private boolean isSession = false;
    private boolean doorsOpen = false;
    private boolean isPlayerInfo = false;
    private boolean stopMovement = false;
    private int movex = -1000;
    private int moveRectx = -3000;
    private float doorSize = 2.0f;
    private float wallSize = 2.4f;
    private float alpha = 0.0f;
    private float zoomFactor = 0.1f;
    private float scaleFactor = 1.0f;
    private JPanel panel;
    private JLabel timeLabel;
    private Timer timer;
    private Timer uiTimer;
    private Rectangle settingsButtonBounds;
    private Rectangle closeButtonBounds;
    private Rectangle closeSessionButtonBounds;
    private Rectangle returnTItleButtonBounds;
    private Rectangle OnlineButtonBounds;
    private Rectangle ComputerButtonBounds;
    private Rectangle HowtoplayButtonBounds;
    private Rectangle playerIconBounds;
    private Rectangle roleIconBounds;
    private Rectangle checkBox1Bounds;
    private Rectangle checkBox2Bounds;
    private Rectangle[] imageRectangles = new Rectangle[48];
    private JSlider slider1;
    private JSlider slider2;
    private ArrayList<Ball> balls = new ArrayList<>();
    private Random random = new Random();
    private JTextField playerNameInputField;
    private String originalOpponentPlayerName = "Computer";
    private int opponentIconNumber = 0;
    private int opponentExperience = 0;
    private boolean gameStarted = false; //ゲーム開始フラグ追加
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
    private JFrame frame;
    private GameController gameController;
    private GameClient client;
    /* コンストラクタでLobbyの設定を行います。*/
    public Lobby(JFrame HanahudaFrame) {
        /* ウィンドウの基本設定 */
        frame = HanahudaFrame;        
        /* タイマーでアニメーションを管理*/
        timer = new Timer(16, this);
        timer.start();
        /* 画像を読み込む*/
        wallImage = new ImageIcon("画像/壁.png").getImage();
        rightDoorImage = new ImageIcon("画像/右扉.png").getImage();
        leftDoorImage = new ImageIcon("画像/左扉.png").getImage();
        lobbyImage = new ImageIcon("画像/ロビー.png").getImage();
        settingsIcon = new ImageIcon("画像/設定アイコン.png").getImage();
        closeIcon = new ImageIcon("画像/バツアイコン.png").getImage();
        settingsWindowImage = new ImageIcon("画像/設定ウィンドウ.png").getImage();
        returnTItleButtonImage = new ImageIcon("画像/タイトルに戻る.png").getImage();
        checkImage = new ImageIcon("画像/チェック欄.png").getImage();
        checkMarkImage = new ImageIcon("画像/チェックマーク.png").getImage();
        cherryBlossom = new ImageIcon("画像/桜.png").getImage();
        OnlineButton = new ImageIcon("画像/オンライン対戦.png").getImage();
        ComputerButton = new ImageIcon("画像/コンピュータ対戦.png").getImage();
        HowtoplayButton = new ImageIcon("画像/遊び方.png").getImage();
        roleIcon = new ImageIcon("画像/役表アイコン.png").getImage();
        blackWindow = new ImageIcon("画像/黒背景.png").getImage();
        playerInfo = new ImageIcon("画像/プレイヤー情報.png").getImage();
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
        int playerSelectedIcon = AppPreferences.getInt("playerIcon", 0);
        playerIcon = new ImageIcon(Card[playerSelectedIcon / 4][playerSelectedIcon % 4]).getImage();
        /* プレイヤー名の初期値を取得*/
        String initialPlayerName = AppPreferences.getString("playerName", "player");
        /* 入力フィールドの初期化*/
        playerNameInputField = new JTextField(initialPlayerName, 10);
        playerNameInputField.setFont(new Font("Arial", Font.BOLD, 16));
        ((AbstractDocument) playerNameInputField.getDocument()).setDocumentFilter(new ConditionalAlphabetOnlyFilter());
        playerNameInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                savePlayerName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                savePlayerName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                savePlayerName();
            }
            private void savePlayerName() {
                String newPlayerName = playerNameInputField.getText();
                AppPreferences.putString("playerName", newPlayerName);
            }
        });
        /* フェードイン用タイマー*/
        Timer fadeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.06f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                panel.repaint();
            }
        });
        fadeTimer.start();
        /* パネルの作成とアニメーション設定*/
        panel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                /* 画面中央に向かってズームする*/
                int centerX = frame.getWidth()/ 2;
                int centerY = frame.getHeight()/ 2;
                g2d.translate(centerX, centerY);
                g2d.scale(scaleFactor, scaleFactor);
                g2d.translate(-centerX, -centerY);
                /* 背景の壁の描画*/
                g.drawImage(wallImage, (int)((-frame.getWidth() * (wallSize - 1)) / 2), 0, (int)(frame.getWidth() * wallSize), frame.getHeight(), frame);
                if(!doorsOpen){
                    g.drawImage(rightDoorImage, (int)((frame.getWidth() * (doorSize - 1))), 0, frame.getWidth(), frame.getHeight(), frame);
                    g.drawImage(leftDoorImage, (int)((-frame.getWidth() * (doorSize - 1))), 0, frame.getWidth(), frame.getHeight(), frame);
                } else {
                    /* ドアが開くアニメーション*/
                    int offsetX = (int) (zoomFactor * frame.getWidth() / 4);
                    g.drawImage(leftDoorImage, -offsetX, 0, this);
                    g.drawImage(rightDoorImage, frame.getWidth() - rightDoorImage.getWidth(null) + offsetX, 0, this);
                }
                /* 対戦時のプレイヤーを識別するための長方形を用意する*/
                g.setColor(Color.BLUE);
                g.fillRect(moveRectx - 2920, 640, 1920 * 3, 200);
                g.setColor(Color.BLACK);
                /* 対戦時のプレイヤーアイコンの表示 */
                int playerIconWidth = (int) (playerIcon.getWidth(null));
                int playerIconHeight = (int) (playerIcon.getHeight(null));
                int iconX = 420 - playerIconWidth / 2;
                int iconY = 740 - playerIconHeight / 2;
                g.drawImage(playerIcon, iconX + movex, iconY, playerIconWidth, playerIconHeight, frame);
                /* 対戦時のプレイヤー名の表示*/
                String playerName = AppPreferences.getString("playerName", "playerName");
                playerName += String.format(" Lv.%d", AppPreferences.getInt("経験値", 0) / 50);
                Font currentFont = g.getFont();
                Font largerFont = currentFont.deriveFont(currentFont.getSize2D() * 5);
                g.setFont(largerFont);
                FontMetrics metrics = g.getFontMetrics(largerFont);
                int textX = iconX + playerIconWidth + 10;
                int textY = iconY + (playerIconHeight / 2) + (metrics.getAscent() / 2) - metrics.getDescent();
                g.drawString(playerName, textX + movex, textY);
                /* 対戦時の相手のプレイヤーを識別するための長方形を用意する*/
                g.setColor(Color.RED);
                g.fillRect(- moveRectx - 1000, 240, 1920 * 3, 200);
                g.setColor(Color.BLACK);
                /* 対戦時の相手のプレイヤーアイコンの表示 */
                int opponentIconX = 1140 - playerIconWidth / 2;
                int opponentIconY = 340 - playerIconHeight / 2;
                g.drawImage(Card[opponentIconNumber / 4][opponentIconNumber % 4], opponentIconX - movex, opponentIconY, playerIconWidth, playerIconHeight, frame);
                /* 対戦時の相手のプレイヤー名の表示*/
                String opponentPlayerName = originalOpponentPlayerName;
                opponentPlayerName += String.format(" Lv.%d", opponentExperience / 50);
                Font opponentLargerFont = g.getFont();
                g.setFont(opponentLargerFont);
                FontMetrics opponentMetrics = g.getFontMetrics(opponentLargerFont);
                int opponentTextX = opponentIconX + playerIconWidth + 10;
                int opponentTextY = opponentIconY + (playerIconHeight / 2) + (opponentMetrics.getAscent() / 2) - opponentMetrics.getDescent();
                g.drawString(opponentPlayerName, opponentTextX - movex, opponentTextY);
                /*FadeInOut用白い画像*/
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                /* Lobbyの表示*/
                g.drawImage(lobbyImage, 0, 0, frame.getWidth(), frame.getHeight(), frame);
                /* 設定ボタンアイコンの表示*/
                int settingsWidth = (int) (settingsIcon.getWidth(null) * 0.1);
                int settingsHeight = (int) (settingsIcon.getHeight(null) * 0.1);
                g.drawImage(settingsIcon, frame.getWidth() - settingsWidth - 10, 10, settingsWidth, settingsHeight, frame);
                /* 役表示アイコンの表示*/
                int roleWidth = (int) (roleIcon.getWidth(null) * 0.1);
                int roleHeight = (int) (roleIcon.getHeight(null) * 0.1);
                g.drawImage(roleIcon, frame.getWidth() - roleWidth - 10, 100, roleWidth, roleHeight, frame);
                /* プレイヤーアイコンの表示*/
                g.drawImage(playerIcon, frame.getWidth()/32, 10, playerIconWidth, playerIconHeight, frame);
                g.drawImage(playerInfo, frame.getWidth()/32 * 3, 10, playerInfo.getWidth(null) / 4, playerInfo.getHeight(null) / 4, frame);
                /* インプットフィールドの表示*/
                playerNameInputField.setBounds(frame.getWidth()/32 + playerIconWidth + 10, 50, 200, 30);
                if(!frame.isAncestorOf(playerNameInputField)) {
                    frame.add(playerNameInputField);
                }
                /* 経験値を取得し、レベルを計算*/
                int ex = AppPreferences.getInt("経験値", 0);
                int lvl = ex / 50;
                /* レベルを表示するテキストを作成*/
                JLabel levelLabel = new JLabel("Lv. " + lvl);
                levelLabel.setBounds(frame.getWidth() / 32 + playerIconWidth + 10, 90, 200, 30);
                levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
                levelLabel.setForeground(Color.WHITE);
                if (!frame.isAncestorOf(levelLabel)) {
                    frame.add(levelLabel);
                }
                /* 緑色の進捗バー（経験値に基づくバー）*/
                JPanel greenBar = new JPanel();
                int greenBarWidth = 400 * (ex % 50) / 50;
                greenBar.setBounds(frame.getWidth() / 32 + playerIconWidth + 10, 130, greenBarWidth, 20);
                greenBar.setBackground(Color.GREEN);
                frame.add(greenBar);
                /* 黒い横長のバー（背景となるバー）*/
                JPanel blackBar = new JPanel();
                blackBar.setBounds(frame.getWidth() / 32 + playerIconWidth + 10, 130, 400, 20);
                blackBar.setBackground(Color.BLACK);
                frame.add(blackBar);
                /* プレイヤー名と表示するテキストを作成*/
                JLabel playerNameLabel = new JLabel("PlayerName");
                playerNameLabel.setBounds(frame.getWidth() / 32 + playerIconWidth + 10, 10, 200, 30);
                playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                playerNameLabel.setForeground(Color.WHITE);
                if (!frame.isAncestorOf(playerNameLabel)) {
                    frame.add(playerNameLabel);
                }
                /* Session接続中の黒背景の表示*/
                if (isSession) {
                    g.drawImage(blackWindow, 840, -10, 220, 100, frame);
                    g.drawImage(closeIcon, 1030, 5, 20, 20, frame);
                }
                /* オンライン対戦ボタンの表示*/
                g.drawImage(OnlineButton, frame.getWidth()/16*9, frame.getHeight()/16*3 - 60, frame.getWidth()/8*3, frame.getHeight()/16*3, frame);
                /* コンピュータ対戦ボタンの表示*/
                g.drawImage(ComputerButton, frame.getWidth()/16*9, frame.getHeight()/8*3 - 60, frame.getWidth()/8*3, frame.getHeight()/16*3, frame);
                /* 遊び方ボタンの表示*/
                g.drawImage(HowtoplayButton, frame.getWidth()/16*9, frame.getHeight()/16*9 - 60, frame.getWidth()/8*3, frame.getHeight()/16*3, frame);
                /* 設定ウィンドウの表示*/
                if(isSettingOpen){
                    int settingsWindowWidth = settingsWindowImage.getWidth(null);
                    int settingsWindowHeight = settingsWindowImage.getHeight(null);
                    g.drawImage(settingsWindowImage, 0, 0, frame);
                    g.drawImage(closeIcon, frame.getWidth() - (int)(settingsWindowWidth * 0.05) - 10, 50, (int)(settingsWindowWidth * 0.03), (int)(settingsWindowHeight * 0.05), frame);
                    int returnTItleButtonWidth = returnTItleButtonImage.getWidth(null);
                    int returnTItleButtonHeight = returnTItleButtonImage.getHeight(null);
                    g.drawImage(returnTItleButtonImage, frame.getWidth() * 3 / 8, frame.getHeight() * 3 / 4, frame.getWidth() / 4, frame.getHeight() / 8, frame);
                    g.drawImage(checkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    g.drawImage(checkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    if (checkMarkVisible1) {
                        g.drawImage(checkMarkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    }
                    if (checkMarkVisible2) {
                        g.drawImage(checkMarkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16, frame);
                    }
                }
                /* プレイヤーアイコンの表示*/
                if(isPlayerIcon){
                    int settingsWindowWidth = settingsWindowImage.getWidth(null);
                    int settingsWindowHeight = settingsWindowImage.getHeight(null);
                    g.drawImage(blackWindow, 0, 0, frame);
                    g.drawImage(closeIcon, frame.getWidth() - (int)(settingsWindowWidth * 0.05) - 10, 50, (int)(settingsWindowWidth * 0.03), (int)(settingsWindowHeight * 0.05), frame);
                    /* playerIconの一覧の表示 */
                    int columns = 8;
                    int rows = 6;
                    int xStart = 800;
                    int yStart = 80;
                    for (int i = 0; i < 48; i++) {
                        int row = i / columns;
                        int col = i % columns;
                        int monthIndex = i / 4;
                        int dayIndex = i % 4;
                        int imageWidth = Card[monthIndex][dayIndex].getWidth(frame);
                        int imageHeight = Card[monthIndex][dayIndex].getHeight(frame);
                        int x = xStart + col * imageWidth;
                        int y = yStart + row * imageHeight;
                        g.drawImage(Card[monthIndex][dayIndex], x, y, imageWidth, imageHeight, frame);
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
        /* 各ボタンの位置とサイズを計算して、クリック判定用のRectangleを作成*/
        settingsButtonBounds = new Rectangle(
            frame.getWidth() - (int) (settingsIcon.getWidth(null) * 0.1) - 10, 
            10,
            (int) (settingsIcon.getWidth(null) * 0.1), 
            (int) (settingsIcon.getHeight(null) * 0.1)
        );
        roleIconBounds = new Rectangle(
            frame.getWidth() - (int) (roleIcon.getWidth(null) * 0.1) - 10, 
            100,
            (int) (roleIcon.getWidth(null) * 0.1), 
            (int) (roleIcon.getHeight(null) * 0.1)
        );
        closeButtonBounds = new Rectangle(
            frame.getWidth() - (int) (settingsWindowImage.getWidth(null) * 0.05) - 10,
            50,
            (int) (settingsWindowImage.getWidth(null) * 0.03), 
            (int) (settingsWindowImage.getHeight(null) * 0.05)
        );
        closeSessionButtonBounds = new Rectangle(1030, 5, 20, 20);
        OnlineButtonBounds = new Rectangle(
            frame.getWidth()/16*9,
            frame.getHeight()/16*3 - 60,
            frame.getWidth()/8*3,
            frame.getHeight()/16*3
        );
        ComputerButtonBounds = new Rectangle(
            frame.getWidth()/16*9,
            frame.getHeight()/8*3 - 60,
            frame.getWidth()/8*3,
            frame.getHeight()/16*3
        );
        HowtoplayButtonBounds = new Rectangle(
            frame.getWidth()/16*9,
            frame.getHeight()/16*9 - 60,
            frame.getWidth()/8*3,
            frame.getHeight()/16*3
        );
        returnTItleButtonBounds = new Rectangle(
            frame.getWidth() * 3 / 8, 
            frame.getHeight() * 3 / 4, 
            frame.getWidth() / 4, 
            frame.getHeight() / 8
        );
        playerIconBounds = new Rectangle(
            frame.getWidth()/32,
            10,
            playerIcon.getWidth(null),
            playerIcon.getHeight(null)
        );
        checkBox1Bounds = new Rectangle(frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16);
        checkBox2Bounds = new Rectangle(frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16);
        /* 画像を配置するためのRectangle配列を作成*/
        int columns = 8;
        int rows = 6;
        int xStart = 800;
        int yStart = 80;
        for (int i = 0; i < 48; i++) {
            int row = i / columns;
            int col = i % columns;
            int monthIndex = i / 4;
            int dayIndex = i % 4;
            int imageWidth = Card[monthIndex][dayIndex].getWidth(frame);
            int imageHeight = Card[monthIndex][dayIndex].getHeight(frame);
            int x = xStart + col * imageWidth;
            int y = yStart + row * imageHeight;
            imageRectangles[i] = new Rectangle(x, y, imageWidth, imageHeight);
        }
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
                /* 設定ボタンの座標とクリック位置の確認*/
                if (!isSettingOpen && !isPlayerIcon && settingsButtonBounds.contains(e.getPoint())) {
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
                else if(isSettingOpen && !isPlayerIcon && !isReturnTitle && closeButtonBounds.contains(e.getPoint())) {
                    /* 設定が閉じられた場合、アニメーションを再開する準備 */
                    isSettingOpen = false;
                    panel.remove(slider1);
                    panel.remove(slider2);
                    panel.revalidate();
                    panel.repaint();
                }
                else if(!isSettingOpen && !isPlayerIcon && playerIconBounds.contains(e.getPoint())) {
                    /* playerアイコンがクリックされた場合*/
                    isPlayerIcon = true;
                }
                else if(!isSettingOpen && isPlayerIcon && closeButtonBounds.contains(e.getPoint())) {
                    isPlayerIcon = false;
                }
                else if(!isSettingOpen && isPlayerIcon) {
                    for(int i = 0; i < imageRectangles.length; i++) {
                        if(imageRectangles[i].contains(e.getPoint())) {
                            AppPreferences.putInt("playerIcon", i);
                            playerIcon = new ImageIcon(Card[i / 4][i % 4]).getImage();
                            panel.repaint();
                            break;
                        }
                    }
                }
                else if(isSettingOpen && !isPlayerIcon && returnTItleButtonBounds.contains(e.getPoint()) && !isSession){
                    /* タイトルに戻るボタンが押された場合*/
                    isReturnTitle = true;
                    /* フェードアウト用タイマー*/
                    Timer fadeTimer = new Timer(10, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            alpha -= 0.02f;
                            if (alpha <= 0.0f) {
                                alpha = 0.0f;
                                ((Timer) e.getSource()).stop();
                                Timer wallTimer = new Timer(10, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        wallSize -= 0.1f;
                                        if (wallSize <= 1) {
                                            wallSize = 1;
                                            ((Timer) e.getSource()).stop();
                                            Timer doorTimer = new Timer(10, new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    doorSize -= 0.02f;
                                                    if(doorSize <= 1){
                                                        doorSize = 1;
                                                        ((Timer) e.getSource()).stop();
                                                        Title titlePanel = new Title(frame);
                                                    }
                                                    panel.repaint();
                                                }
                                            });
                                            doorTimer.start();
                                        }
                                        panel.repaint();
                                    }
                                });
                                wallTimer.start();
                            }
                            panel.repaint();
                        }
                    });
                    fadeTimer.start();
                }
                else if (isSettingOpen && !isPlayerIcon && checkBox1Bounds.contains(e.getPoint())) {
                    /* チェックボックス1がクリックされた場合*/
                    checkMarkVisible1 = !checkMarkVisible1;
                    AppPreferences.putBoolean("checkMarkVisible1", checkMarkVisible1);
                    panel.repaint();
                }
                else if (isSettingOpen && !isPlayerIcon && checkBox2Bounds.contains(e.getPoint())) {
                    /* チェックボックス2がクリックされた場合*/
                    checkMarkVisible2 = !checkMarkVisible2;
                    AppPreferences.putBoolean("checkMarkVisible2", checkMarkVisible2);
                    panel.repaint();
                }
                else if (!isSettingOpen && !isPlayerIcon && roleIconBounds.contains(e.getPoint())) {
                    /* 役表表示ボタンがクリックされた場合*/
                    System.out.println("役表表示");
                }
                else if (alpha == 1 && !isSettingOpen && !isPlayerIcon && OnlineButtonBounds.contains(e.getPoint()) && !isSession) {
                    /* オンライン対戦ボタンがクリックされた場合*/
                    String newPlayerName = playerNameInputField.getText();
                    AppPreferences.putString("playerName", newPlayerName);
                    gameController = new GameController();
                    client = new GameClient("https://hanahudaonline.onrender.com");
                    //client = new GameClient("http://localhost:10030");
                    client.setGameController(gameController);
                    gameController.setGameClient(client);
                    gameController.startOnlineMatch();
                    startTimer();
                }
                else if (alpha == 1 && !isSettingOpen && !isPlayerIcon && ComputerButtonBounds.contains(e.getPoint()) && !isSession) {
                    /* コンピュータ対戦ボタンがクリックされた場合*/
                    String newPlayerName = playerNameInputField.getText();
                    AppPreferences.putString("playerName", newPlayerName);
                    System.out.println("コンピュータ対戦");
                }
                else if (!isSettingOpen && !isPlayerIcon && HowtoplayButtonBounds.contains(e.getPoint())) {
                    /* 遊び方ボタンがクリックされた場合*/
                    System.out.println("遊び方");
                }
                else if (closeSessionButtonBounds.contains(e.getPoint()) && isSession) {
                    /* セッション切断ボタンがクリックされた場合*/
                    isSession = false;
                    try {
                        client.disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    /* タイマーでラベルを更新し、ゲームが開始されたら処理を切り替える*/
    private void startTimer() {
        /* セッション中*/
        isSession = true;
        /* 時間表示ラベルを作成する*/
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 64));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setBounds(860, 0, 200, 100);
        frame.add(timeLabel);
        panel.setLayout(null);

        uiTimer = new Timer(100, e -> {
            int elapsedSeconds = gameController.getElapsedTime();
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            System.out.println(String.format("%d:%02d", minutes, seconds));
            panel.revalidate();
            panel.repaint();
            timeLabel.setText(String.format("%d:%02d", minutes, seconds));
            if(gameController.getIsActive()) isSession = false;
            if (!isSession) {
                frame.remove(timeLabel);
                frame.revalidate();
                frame.repaint();
                ((Timer) e.getSource()).stop();
            }
            try {
                if (!gameStarted && client.isGameStarted()) {//追加
                    ((Timer) e.getSource()).stop();
                    System.out.println("ゲームが開始されました!");
                    gameStarted = true;//追加
                    client.sendPlayerInfo(AppPreferences.getString("playerName", "player"), AppPreferences.getInt("playerIcon", 0), AppPreferences.getInt("経験値", 0) / 50);
                    /* フェードアウト用タイマー*/
                    Timer fadeTimer = new Timer(10, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            alpha -= 0.02f;
                            if (alpha <= 0.0f) {
                                alpha = 0.0f;
                                ((Timer) e.getSource()).stop();
                                Timer wallTimer = new Timer(10, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        wallSize -= 0.1f;
                                        if (wallSize <= 1) {
                                            wallSize = 1;
                                            ((Timer) e.getSource()).stop();
                                            Timer doorTimer = new Timer(10, new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    doorSize -= 0.02f;
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
                                                        Timer delayTimer = new Timer(3000, new ActionListener() {
                                                            @Override
                                                            public void actionPerformed(ActionEvent e) {
                                                                ((Timer) e.getSource()).stop();
                                                                /* ドアが開くアニメーション*/
                                                                doorsOpen = true;
                                                                Timer doorOpenTimer = new Timer(10, new ActionListener() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        zoomFactor += 0.04f;
                                                                        if (zoomFactor >= 0.8f) {
                                                                            ((Timer) e.getSource()).stop();
                                                                            startZoomAnimation();
                                                                        }
                                                                        panel.repaint();
                                                                    }
                                                                });
                                                                doorOpenTimer.start();
                                                            }
                                                        });
                                                        delayTimer.setRepeats(false);
                                                        delayTimer.start();
                                                    }
                                                    panel.repaint();
                                                }
                                            });
                                            doorTimer.start();
                                        }
                                        panel.repaint();
                                    }
                                });
                                wallTimer.start();
                            }
                            panel.repaint();
                        }
                    });
                    fadeTimer.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                ((Timer) e.getSource()).stop();
            }
        });
        uiTimer.start();
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
            if(!stopMovement && movex < 0)movex += 20;
            if(!stopMovement && movex >= 0)movex += 80;
            if(!stopMovement && movex < 0)moveRectx += 80;
            if(!stopMovement && movex >= 0)moveRectx += 80;
            if(movex >= 0) {
                stopMovement = true;
                Timer stopTimer = new Timer(1000, ev -> {
                    stopMovement = false;
                });
                stopTimer.setRepeats(false);
                stopTimer.start();
            }
            if(movex >= 4000) {
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
                    OnlineGame onlineGamePanel = new OnlineGame(frame, gameController, client);
                }
                panel.repaint();
            }
        });
        zoomTimer.start();
    }
    /* タイマーイベント処理*/
    @Override
    public void actionPerformed(ActionEvent e) {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.x += ball.dx;
            ball.y += ball.dy;
            /* 画面外に出たか確認*/
            if (ball.x < -960 || ball.x > frame.getWidth() + 960 || ball.y < -540 || ball.y > frame.getHeight() + 540) {
                iterator.remove();
            }
        }
        panel.repaint();
    }
    public void setIsSession(boolean isActive) {
        isSession = isActive;
    }
    /* 英小文字・大文字・数字以外の入力を制限し、条件付きで有効化する DocumentFilter*/
    class ConditionalAlphabetOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isInputAllowed(fb.getDocument().getLength(), string)) {
                super.insertString(fb, offset, string, attr);
            }
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isInputAllowed(fb.getDocument().getLength(), text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
        private boolean isInputAllowed(int currentLength, String input) {
            if (isSettingOpen || isPlayerIcon) {
                return false;
            }
            return input != null && input.matches("[a-zA-Z0-9]+") && currentLength + input.length() <= 8;
        }
    }
}
