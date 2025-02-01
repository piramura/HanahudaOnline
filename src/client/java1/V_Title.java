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

/* JPanelを継承したV_Titleクラスを定義します。*/
public class V_Title extends JPanel {
    /* privateで宣言します。*/
    private ImageLoader imageLoader = new ImageLoader();
    private JPanel panel;
    /* コンストラクタでV_Titleの設定を行います。*/
    public V_Title(M_Title m_Title) {
        /* パネルの作成とアニメーション設定*/
        this.panel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                /* 画面中央に向かってズームする*/
                g2d.translate(960, 540);
                g2d.scale(m_Title.getScaleFactor(), m_Title.getScaleFactor());
                g2d.translate(-960, -540);
                /* 背景と扉の描画*/
                g.drawImage(imageLoader.getWall(), 0, 0, this);
                if(m_Title.getIsStartVisible()){
                    g.drawImage(imageLoader.getLeftDoor(), 0, 0, this);
                    g.drawImage(imageLoader.getRightDoor(), 0, 0, this);
                } else {
                    /* ドアが開くアニメーション*/
                    int offsetX = (int) (m_Title.getZoomFactor() * 480);
                    g.drawImage(imageLoader.getLeftDoor(), -offsetX, 0, this);
                    g.drawImage(imageLoader.getRightDoor(), offsetX, 0, this);
                }
                /* 「画面をタッチしてゲームスタート」の表示*/
                if(m_Title.getIsStartVisible()) {
                    float opacity = (float) Math.abs(Math.sin(System.currentTimeMillis() * 0.001));
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    g2d.drawImage(imageLoader.getStartButton(), 0, 0, this);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                /* 設定ボタンアイコンの表示*/
                g.drawImage(imageLoader.getSettingIcon(), 1835, 10, 77, 77, this);
                /* 設定ウィンドウの表示*/
                if(m_Title.getIsSettingOpen()){
                    g.drawImage(imageLoader.getSettingWindow(), 0, 0, this);
                    g.drawImage(imageLoader.getCloseIcon(), 1810, 50, 54, 54, this);
                    g.drawImage(imageLoader.getGameOverButton(), 720, 810, 480, 135, this);
                    g.drawImage(imageLoader.getCheckBox(), 775, 515, 80, 80, this);
                    g.drawImage(imageLoader.getCheckBox(), 775, 315, 80, 80, this);
                    if (m_Title.getIsCheckMarkVisibleBGM()) {
                        g.drawImage(imageLoader.getCheckMark(), 775, 315, 80, 80, this);
                    }
                    if (m_Title.getIsCheckMarkVisibleSE()) {
                        g.drawImage(imageLoader.getCheckMark(), 775, 515, 80, 80, this);
                    }
                }
                /* ボールの描画*/
                for (Ball ball : m_Title.getBalls()) {
                    g.drawImage(imageLoader.getCherryBlossom(), ball.x, ball.y, 30, 30, this);
                }
            }
        };
        /* panelのレイアウト*/
        panel.setLayout(null);
        /* panelの描画を常に更新*/
        Timer updateTimer = new Timer(16, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.repaint();
            }
        });
        updateTimer.start();
    }
    public JPanel getPanel() {
        return panel;
    }
}
