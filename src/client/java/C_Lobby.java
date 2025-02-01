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

/* JPanelを継承したC_Lobbyクラスを定義します。*/
class C_Lobby extends JPanel {
    /* privateで宣言します。*/
    private M_Lobby m_Lobby;
    private V_Lobby v_Lobby;
    private JFrame frame;
    private JPanel panel = new JPanel();
    private JLabel timeLabel;
    private Rectangle[] imageRectangles = new Rectangle[48];
    /* コンストラクタでLobbyの設定を行います。*/
    public C_Lobby(JFrame HanahudaFrame) {
        /* ウィンドウの基本設定 */
        frame = HanahudaFrame;
        /* M_Titleの設定*/
        m_Lobby = new M_Lobby(this);
        /* V_Titleの設定*/
        this.v_Lobby = new V_Lobby(m_Lobby);
        /* panelの更新*/
        this.panel = v_Lobby.getPanel();
        /* frameの更新*/
        frame.setContentPane(panel);
        frame.revalidate();
        /* 画像を配置するためのRectangle配列を作成*/
        for (int i = 0; i < 48; i++) {
            imageRectangles[i] = new Rectangle(800 + (i / 8) * 114, 80 + (i % 8) * 153, 114, 153);
        }
        /* クリックイベントを設定*/
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 画面がクリックされた場合*/
                m_Lobby.generateRandomBalls(e.getX(), e.getY());
                /* 設定ボタンの座標とクリック位置の確認*/
                if (!m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && m_Lobby.getSettingButtonBounds().contains(e.getPoint())) {
                    m_Lobby.openSetting();
                }
                else if(m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && !m_Lobby.getIsReturnTitle() && m_Lobby.getCloseButtonBounds().contains(e.getPoint())) {
                    /* 設定が閉じる*/
                    m_Lobby.closeSetting();
                }
                else if(!m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && m_Lobby.getPlayerIconBounds().contains(e.getPoint())) {
                    /* playerアイコンがクリックされた場合*/
                    m_Lobby.setIsPlayerIcon(true);
                }
                else if(!m_Lobby.getIsSettingOpen() && m_Lobby.getIsPlayerIcon() && m_Lobby.getCloseButtonBounds().contains(e.getPoint())) {
                    m_Lobby.setIsPlayerIcon(false);
                }
                else if(!m_Lobby.getIsSettingOpen() && m_Lobby.getIsPlayerIcon()) {
                    for(int i = 0; i < 48; i++) {
                        if(m_Lobby.getImageRectangles(i).contains(e.getPoint())) {
                            AppPreferences.putInt("プレイヤーアイコン", i);
                        }
                    }
                }
                else if(m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && !m_Lobby.getIsSession() && m_Lobby.getReturnTitleButtonBounds().contains(e.getPoint())){
                    /* タイトルに戻るボタンが押された場合*/
                    m_Lobby.returnTitle();
                }
                else if (m_Lobby.getCheckBoxBGMBounds().contains(e.getPoint())) {
                    /* チェックボックスBGMがクリックされた場合*/
                    m_Lobby.clickCheckBoxBGM();
                }
                else if (m_Lobby.getCheckBoxSEBounds().contains(e.getPoint())) {
                    /* チェックボックスSEがクリックされた場合*/
                    m_Lobby.clickCheckBoxSE();
                }
                else if (!m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && m_Lobby.getRoleIconBounds().contains(e.getPoint())) {
                    /* 役表表示ボタンがクリックされた場合*/
                    System.out.println("役表表示");
                }
                else if (m_Lobby.getAlpha() == 1 && !m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && !m_Lobby.getIsSession() && m_Lobby.getOnlineButtonBounds().contains(e.getPoint())) {
                    /* オンライン対戦ボタンがクリックされた場合*/
                    m_Lobby.Online();
                }
                else if (m_Lobby.getAlpha() == 1 && !m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && !m_Lobby.getIsSession() && m_Lobby.getComputerButtonBounds().contains(e.getPoint())) {
                    /* コンピュータ対戦ボタンがクリックされた場合*/
                    System.out.println("コンピュータ対戦");
                    m_Lobby.Computer();
                    System.out.println("コンピュータ対戦");
                }
                else if (m_Lobby.getAlpha() == 1 && !m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && !m_Lobby.getIsSession() && m_Lobby.getHowToPlayButtonBounds().contains(e.getPoint())) {
                    /* 遊び方ボタンがクリックされた場合*/
                    System.out.println("遊び方");
                }
                else if (!m_Lobby.getIsSettingOpen() && !m_Lobby.getIsPlayerIcon() && m_Lobby.getIsSession() && m_Lobby.getCloseSessionButtonBounds().contains(e.getPoint())) {
                    /* セッション切断ボタンがクリックされた場合*/
                    m_Lobby.closeSession();
                }
            }
        });
    }

    /* timeLabelを加える*/
    public void setTimeLabel() {
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 64));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setBounds(860, 0, 200, 100);
        panel.add(timeLabel);
        panel.setLayout(null);
    }
    /* タイマーの時間を反映*/
    public void setTimeText(int minutes, int seconds) {
        timeLabel.setText(String.format("%d:%02d", minutes, seconds));
    }
    /* タイマーの削除*/
    public void removeTimeLabel() {
        panel.remove(timeLabel);
    }

    public void moveOnlineGame(GameController gameController, GameClient client) {
        OnlineGame onlineGamePanel = new OnlineGame(frame, gameController, client);
    }

    public void moveTitle() {
        C_Title c_TitlePanel = new C_Title(frame);
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

    public JTextField getPlayerNameInputField() {
        return v_Lobby.getPlayerNameInputField();
    }
}
