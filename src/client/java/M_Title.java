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

/* JPanelを継承したM_Titleクラスを定義します。*/
public class M_Title extends JPanel {
    private C_Title c_Title;
    private JPanel panel;
    private boolean isStartVisible = true;
    private boolean isDoorOpen = false;
    private boolean doorsOpen = false;
    private boolean isSettingOpen = false;
    private boolean isCheckMarkVisibleBGM = AppPreferences.getBoolean("BGM", true);
    private boolean isCheckMarkVisibleSE = AppPreferences.getBoolean("SE", true);
    private Rectangle settingButtonBounds = new Rectangle();
    private Rectangle closeButtonBounds = new Rectangle();
    private Rectangle gameOverButtonBounds = new Rectangle();
    private Rectangle checkBoxBGMBounds = new Rectangle();
    private Rectangle checkBoxSEBounds = new Rectangle();
    private float zoomFactor = 1.0f;
    private float scaleFactor = 1.0f;
    private JSlider slider1;
    private JSlider slider2;
    private ArrayList<Ball> balls = new ArrayList<>();
    private Random random = new Random();
    /* コンストラクタでM_Titleの設定を行います。*/
    public M_Title(C_Title c_Title) {
        /* 初期化*/
        this.c_Title = c_Title;
        /* 各ボタンの位置とサイズを計算して、クリック判定用のRectangleを作成*/
        this.settingButtonBounds = new Rectangle(1835, 10, 77, 77);
        this.closeButtonBounds = new Rectangle(1810, 50, 54, 54);
        this.gameOverButtonBounds = new Rectangle(720, 810, 480, 135);
        this.checkBoxBGMBounds = new Rectangle(775, 315, 80, 80);
        this.checkBoxSEBounds = new Rectangle(775, 515, 80, 80);
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
        c_Title.addSlider(slider1, slider2);
    }

    /* 設定を閉じる*/
    public void closeSetting() {
        this.isSettingOpen = false;
        c_Title.removeSlider(slider1, slider2);
    }

    /* ドアを開くアニメーション*/
    public void openDoor() {
        this.isStartVisible = false;
        this.zoomFactor = 0.1f;
        this.scaleFactor = 1.0f;
        this.isDoorOpen = true;
        Timer doorOpenTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomFactor += 0.06f;
                if (zoomFactor >= 0.8f) {
                    ((Timer) e.getSource()).stop();
                    startZoomAnimation();
                }
            }
        });
        doorOpenTimer.start();
    }

    /* ズームアニメーションの開始*/
    private void startZoomAnimation() {
        Timer zoomTimer = new Timer(16, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleFactor += 0.06f;
                if(scaleFactor >= 4.0f){
                    ((Timer)e.getSource()).stop();
                    c_Title.moveLobby();
                }
            }
        });
        zoomTimer.start();
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
    /* それぞれのメソッドは対応する変数を返す */
    public boolean getIsStartVisible() {
        return this.isStartVisible;
    }

    public boolean getIsDoorOpen() {
        return this.isDoorOpen;
    }

    public boolean getIsSettingOpen() {
        return this.isSettingOpen;
    }

    public boolean getIsCheckMarkVisibleBGM() {
        return this.isCheckMarkVisibleBGM;
    }

    public boolean getIsCheckMarkVisibleSE() {
        return this.isCheckMarkVisibleSE;
    }

    public Rectangle getSettingButtonBounds() {
        return this.settingButtonBounds;
    }

    public Rectangle getCloseButtonBounds() {
        return this.closeButtonBounds;
    }

    public Rectangle getGameOverButtonBounds() {
        return this.gameOverButtonBounds;
    }

    public Rectangle getCheckBoxBGMBounds() {
        return this.checkBoxBGMBounds;
    }

    public Rectangle getCheckBoxSEBounds() {
        return this.checkBoxSEBounds;
    }

    public float getZoomFactor() {
        return this.zoomFactor;
    }

    public float getScaleFactor() {
        return this.scaleFactor;
    }

    public JSlider getSlider1() {
        return this.slider1;
    }

    public JSlider getSlider2() {
        return this.slider2;
    }

    public ArrayList<Ball> getBalls() {
        return this.balls;
    }

    public Random getRandom() {
        return this.random;
    }
}