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

/* JPanelを継承したTitleクラスを定義します。*/
public class Title extends JPanel implements ActionListener {
    /* privateで宣言します。*/
    private Image wallImage;
    private Image rightDoorImage;
    private Image leftDoorImage;
    private Image startImage;
    private Image settingsWindowImage;
    private Image closeIcon;
    private Image settingsIcon;
    private Image gameOverButtonImage;
    private Image checkImage;
    private Image checkMarkImage;
    private Image cherryBlossom;
    private boolean startImageVisible = true;
    private boolean isdoorOpen = false;
    private boolean doorsOpen = false;
    private boolean isSettingOpen = false;
    private boolean checkMarkVisible1;
    private boolean checkMarkVisible2;
    private float zoomFactor = 1.0f;
    private float scaleFactor = 1.0f;
    private Timer fadeInOutTimer;
    private Timer timer;
    private JPanel panel;
    private Rectangle settingsButtonBounds;
    private Rectangle closeButtonBounds;
    private Rectangle gameOverButtonBounds;
    private Rectangle checkBox1Bounds;
    private Rectangle checkBox2Bounds;
    private JSlider slider1;
    private JSlider slider2;
    private static Clip clip;
    private ArrayList<Ball> balls = new ArrayList<>();
    private Random random = new Random();
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
    /* コンストラクタでTitleの設定を行います。*/
    public Title(JFrame HanahudaFrame) {
        /* ウィンドウの基本設定 */
        frame = HanahudaFrame;
        /* 画像を読み込む*/
        wallImage = new ImageIcon("画像/壁.png").getImage();
        rightDoorImage = new ImageIcon("画像/右扉.png").getImage();
        leftDoorImage = new ImageIcon("画像/左扉.png").getImage();
        startImage = new ImageIcon("画像/画面をタッチしてゲームスタート.png").getImage();
        settingsIcon = new ImageIcon("画像/設定アイコン.png").getImage();
        closeIcon = new ImageIcon("画像/バツアイコン.png").getImage();
        settingsWindowImage = new ImageIcon("画像/設定ウィンドウ.png").getImage();
        gameOverButtonImage = new ImageIcon("画像/ゲーム終了.png").getImage();
        checkImage = new ImageIcon("画像/チェック欄.png").getImage();
        checkMarkImage = new ImageIcon("画像/チェックマーク.png").getImage();
        cherryBlossom = new ImageIcon("画像/桜.png").getImage();
        /* タイマーでアニメーションを管理*/
        timer = new Timer(16, this);
        timer.start();
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
                /* 背景と扉の描画*/
                g.drawImage(wallImage, 0, 0, this);
                if(!doorsOpen){
                    g.drawImage(leftDoorImage, 0, 0, this);
                    g.drawImage(rightDoorImage, frame.getWidth() - rightDoorImage.getWidth(null), 0, this);
                } else {
                    /* ドアが開くアニメーション*/
                    int offsetX = (int) (zoomFactor * frame.getWidth() / 4);
                    g.drawImage(leftDoorImage, -offsetX, 0, this);
                    g.drawImage(rightDoorImage, frame.getWidth() - rightDoorImage.getWidth(null) + offsetX, 0, this);
                }
                /* 「画面をタッチしてゲームスタート」の表示*/
                if(startImageVisible) {
                    float opacity = (float) Math.abs(Math.sin(System.currentTimeMillis() * 0.001));
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    g2d.drawImage(startImage, 0, 0, this);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                /* 設定ボタンアイコンの表示*/
                if(startImageVisible) {
                    int settingsWidth = (int) (settingsIcon.getWidth(null) * 0.1);
                    int settingsHeight = (int) (settingsIcon.getHeight(null) * 0.1);
                    g.drawImage(settingsIcon, frame.getWidth() - settingsWidth - 10, 10, settingsWidth, settingsHeight, this);
                }
                /* 設定ウィンドウの表示*/
                if(isSettingOpen){
                    int settingsWindowWidth = settingsWindowImage.getWidth(null);
                    int settingsWindowHeight = settingsWindowImage.getHeight(null);
                    g.drawImage(settingsWindowImage, 0, 0, this);
                    g.drawImage(closeIcon, frame.getWidth() - (int)(settingsWindowWidth * 0.05) - 10, 50, (int)(settingsWindowWidth * 0.03), (int)(settingsWindowHeight * 0.05), this);
                    int gameOverButtonWidth = gameOverButtonImage.getWidth(null);
                    int gameOverButtonHeight = gameOverButtonImage.getHeight(null);
                    g.drawImage(gameOverButtonImage, frame.getWidth() * 3 / 8, frame.getHeight() * 3 / 4, frame.getWidth() / 4, frame.getHeight() / 8, this);
                    g.drawImage(checkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16, this);
                    g.drawImage(checkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16, this);
                    if (checkMarkVisible1) {
                        g.drawImage(checkMarkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16, this);
                    }
                    if (checkMarkVisible2) {
                        g.drawImage(checkMarkImage, frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16, this);
                    }
                }
                /* ボールの描画*/
                for (Ball ball : balls) {
                    g.drawImage(cherryBlossom, ball.x, ball.y, 30, 30, this);
                }
            }
        };
        frame.setContentPane(panel);
        frame.revalidate();
        /* 「画面をタッチしてゲームスタート」のフェードイン/フェードアウトアニメーション*/
        fadeInOutTimer = new Timer(16, e -> {
            panel.repaint();
        });
        fadeInOutTimer.start();
        /* 各ボタンの位置とサイズを計算して、クリック判定用のRectangleを作成*/
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

        gameOverButtonBounds = new Rectangle(
            frame.getWidth() * 3 / 8, 
            frame.getHeight() * 3 / 4, 
            frame.getWidth() / 4, 
            frame.getHeight() / 8
        );
        checkBox1Bounds = new Rectangle(frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 25, frame.getWidth() / 24, frame.getHeight() / 16);
        checkBox2Bounds = new Rectangle(frame.getWidth() / 2 - checkImage.getWidth(null) / 4, frame.getHeight() / 2 - 225, frame.getWidth() / 24, frame.getHeight() / 16);
        /* スライダーの値をPreferencesから取得*/
        int slider1Value = AppPreferences.getInt("slider1", 50);
        int slider2Value = AppPreferences.getInt("slider2", 50);
        /* チェックマークの値をPreferencesから取得*/
        checkMarkVisible1 = AppPreferences.getBoolean("checkMarkVisible1", true);
        checkMarkVisible2 = AppPreferences.getBoolean("checkMarkVisible2", true);
        /* クリックイベントを設定*/
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!isdoorOpen || isSettingOpen){
                    for (int i = 0; i < 3; i++) {
                        double angle = random.nextDouble() * 2 * Math.PI;
                        double speed = 5 + random.nextDouble() * 4;
                        int dx = (int) (Math.cos(angle) * speed);
                        int dy = (int) (Math.sin(angle) * speed);
                        balls.add(new Ball(e.getX(), e.getY(), dx, dy));
                    }
                }
                /* 設定ボタンの座標とクリック位置の確認*/
                if (!isSettingOpen && settingsButtonBounds.contains(e.getPoint())) {
                    /* 設定ボタンがクリックされた場合*/
                    isSettingOpen = true;
                    /* 設定が開かれた場合、ドアの開閉アニメーションが動かないようにする。*/
                    isdoorOpen = true;
                    /* スライダーの値をPreferencesから取得*/
                    int slider1Value = AppPreferences.getInt("slider1", 50);
                    int slider2Value = AppPreferences.getInt("slider2", 50);
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
                    slider1.addChangeListener(e1 -> {AppPreferences.putInt("slider1", slider1.getValue());});
                    slider2.addChangeListener(e1 -> {AppPreferences.putInt("slider2", slider2.getValue());});
                }
                else if(isSettingOpen && closeButtonBounds.contains(e.getPoint())) {
                    /* 設定が閉じられた場合、ドアの開閉アニメーションを再開する準備 */
                    isSettingOpen = false;
                    isdoorOpen = false;
                    panel.remove(slider1);
                    panel.remove(slider2);
                    panel.revalidate();
                    panel.repaint();
                }
                else if(!isdoorOpen && !isSettingOpen){
                    /* それ以外のエリアがクリックされた場合*/
                    startImageVisible = false;
                    doorsOpen = true;
                    zoomFactor = 0.1f;
                    scaleFactor = 1.0f;
                    isdoorOpen = true;
                    /* ドアが開くアニメーション*/
                    Timer doorOpenTimer = new Timer(16, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            zoomFactor += 0.06f;
                            if (zoomFactor >= 0.8f) {
                                ((Timer) e.getSource()).stop();
                                startZoomAnimation();
                            }
                            panel.repaint();
                        }
                    });
                    doorOpenTimer.start();
                } else if(isSettingOpen && gameOverButtonBounds.contains(e.getPoint())){
                    /* ゲーム終了ボタンがクリックされたかを判定*/
                    System.exit(0);
                }
                else if (checkBox1Bounds.contains(e.getPoint())) {
                    /* チェックボックス1がクリックされた場合*/
                    checkMarkVisible1 = !checkMarkVisible1;
                    AppPreferences.putBoolean("checkMarkVisible1", checkMarkVisible1);
                    panel.repaint();
                }
                else if (checkBox2Bounds.contains(e.getPoint())) {
                    /* チェックボックス2がクリックされた場合*/
                    checkMarkVisible2 = !checkMarkVisible2;
                    AppPreferences.putBoolean("checkMarkVisible2", checkMarkVisible2);
                    panel.repaint();
                }
            }
        });
    }
    /* ズームアニメーションの開始*/
    private void startZoomAnimation() {
        Timer zoomTimer = new Timer(16, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleFactor += 0.06f;
                if(scaleFactor >= 4.0f){
                    ((Timer)e.getSource()).stop();
                    /* Lobbyクラスに移動します*/
                    Lobby lobbyPanel = new Lobby(frame);
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
        repaint();
    }
}
