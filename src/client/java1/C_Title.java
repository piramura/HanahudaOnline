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

/* JPanelを継承したC_Titleクラスを定義します。*/
public class C_Title extends JPanel {
    private M_Title m_Title;
    private V_Title v_Title;
    private JFrame frame;
    private JPanel panel = new JPanel();
    /* コンストラクタでC_Titleの設定を行います。*/
    public C_Title(JFrame HanahudaFrame) {
        /* ウィンドウの基本設定 */
        this.frame = HanahudaFrame;
        /* M_Titleの設定*/
        m_Title = new M_Title(this);
        /* V_Titleの設定*/
        this.v_Title = new V_Title(m_Title);
        /* panelの更新*/
        this.panel = v_Title.getPanel();
        /* frameの更新*/
        this.frame.setContentPane(this.panel);
        this.frame.revalidate();
        /* クリックイベントを設定*/
        this.panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 画面がクリックされた場合*/
                m_Title.generateRandomBalls(e.getX(), e.getY());
                if (!m_Title.getIsSettingOpen() && m_Title.getSettingButtonBounds().contains(e.getPoint())) {
                    /* 設定ボタンがクリックされた場合*/
                    m_Title.openSetting();
                }
                else if(m_Title.getIsSettingOpen() && m_Title.getCloseButtonBounds().contains(e.getPoint())) {
                    /* 設定が閉じる*/
                    m_Title.closeSetting();
                }
                else if(!m_Title.getIsDoorOpen() && !m_Title.getIsSettingOpen()){
                    /* それ以外のエリアがクリックされた場合*/
                    m_Title.openDoor();
                } else if(m_Title.getIsSettingOpen() && m_Title.getGameOverButtonBounds().contains(e.getPoint())){
                    /* ゲーム終了ボタンがクリックされたかを判定*/
                    System.exit(0);
                }
                else if (m_Title.getCheckBoxBGMBounds().contains(e.getPoint())) {
                    /* チェックボックスBGMがクリックされた場合*/
                    m_Title.clickCheckBoxBGM();
                }
                else if (m_Title.getCheckBoxSEBounds().contains(e.getPoint())) {
                    /* チェックボックスSEがクリックされた場合*/
                    m_Title.clickCheckBoxSE();
                }
            }
        });
    }
    /* Lobbyに移動する*/
    public void moveLobby() {
        Lobby lobby = new Lobby(frame);
    }

    public void addSlider(JSlider slider1, JSlider slider2) {
        this.panel.add(slider1);
        this.panel.add(slider2);
        this.panel.revalidate();
        this.panel.repaint();
    }

    public void removeSlider(JSlider slider1, JSlider slider2) {
        this.panel.remove(slider1);
        this.panel.remove(slider2);
        this.panel.revalidate();
        this.panel.repaint();
    }
}