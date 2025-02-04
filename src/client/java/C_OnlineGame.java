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

/* JPanelを継承したC_OnlineGameクラスを定義します。*/
class C_OnlineGame extends JPanel {
    /* privateで宣言します。*/
    private M_OnlineGame m_OnlineGame;
    private V_OnlineGame v_OnlineGame;
    private JPanel panel = new JPanel();
    private JFrame frame;
    private GameController controller;
    private GameClient client;
    private ImageLoader imageLoader = new ImageLoader();
    /* コンストラクタでC_OnlineGameの設定を行います。*/
    public C_OnlineGame(JFrame HanahudaFrame, GameController gameController, GameClient gameClient) {
        /* ウィンドウの基本設定 */
        frame = HanahudaFrame;
        /* クライアントの設定*/
        client = gameClient;
        controller = gameController;
        /* M_OnlineGameの設定*/
        m_OnlineGame = new M_OnlineGame(this, controller, client, imageLoader);
        /* V_OnlineGameの設定*/
        this.v_OnlineGame = new V_OnlineGame(m_OnlineGame, imageLoader);
        /* panelの更新*/
        this.panel = v_OnlineGame.getPanel();
        frame.setContentPane(panel);
        frame.revalidate();
        /* クリックイベントを設定*/
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 桜の描画*/
                m_OnlineGame.generateRandomBalls(e.getX(), e.getY());
                /* プレイヤーの場の選択*/
                for(int i = 0;i < m_OnlineGame.getBoardList1().size(); i++){
                    if(!m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getFlushBoardListBoolean().get(i % 2).get(i / 2) && m_OnlineGame.getIsFieldSelect() && m_OnlineGame.getBoardRectangles()[i % 2][i / 2].contains(e.getPoint())) {
                        m_OnlineGame.fieldSelect(i);
                    }
                }
                if(!m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getIsFieldSelect() && !m_OnlineGame.getIsCardOpen()) {
                    /* プレイヤーの選択状態の解除*/
                    m_OnlineGame.backSelect();
                }
                /* プレイヤーの手札選択*/
                for(int i = 0;i < m_OnlineGame.getPlayerHandListBoolean().size(); i++){
                    if(!m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getPlayerHandListBoolean().get(i) && m_OnlineGame.getIsHandSelect() && m_OnlineGame.getPlayerHandRectangles()[i].contains(e.getPoint())) {
                        m_OnlineGame.handSelect(i);
                    }
                }
                /* 設定ボタンの座標とクリック位置の確認*/
                if (!m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getSettingButtonBounds().contains(e.getPoint())) {
                    /* 設定ボタンがクリックされた場合*/
                    m_OnlineGame.openSetting();
                }
                else if(m_OnlineGame.getIsSettingOpen() && !m_OnlineGame.getIsReturnLobby() && m_OnlineGame.getCloseButtonBounds().contains(e.getPoint())) {
                    /* 設定が閉じられた場合、アニメーションを再開する準備 */
                    m_OnlineGame.closeSetting();
                }
                else if(m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getReturnLobbyButtonBounds().contains(e.getPoint())){
                    /* ロビーに戻るボタンが押された場合*/
                    m_OnlineGame.returnLobby();
                }
                else if (m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getCheckBoxBGMBounds().contains(e.getPoint())) {
                    /* チェックボックスBGMがクリックされた場合*/
                    m_OnlineGame.clickCheckBoxBGM();
                }
                else if (m_OnlineGame.getIsSettingOpen() && m_OnlineGame.getCheckBoxSEBounds().contains(e.getPoint())) {
                    /* チェックボックスSEがクリックされた場合*/
                    m_OnlineGame.clickCheckBoxSE();
                }
                else if (m_OnlineGame.getIsKOIKOI() && m_OnlineGame.getKOIKOIButtonBounds().contains(e.getPoint())) {
                    /* こいこい*/
                    m_OnlineGame.KOIKOI();
                }
                else if (m_OnlineGame.getIsKOIKOI() && m_OnlineGame.getENDButtonBounds().contains(e.getPoint())) {
                    /* 勝負*/
                    m_OnlineGame.END();
                }
            }
        });
    }

    public void moveLobby() {
        C_Lobby c_LobbyPanel = new C_Lobby(frame);
    }

    public void addSlider(JSlider slider1, JSlider slider2) {
        this.panel.add(slider1);
        this.panel.add(slider2);
    }

    public void removeSlider(JSlider slider1, JSlider slider2) {
        this.panel.remove(slider1);
        this.panel.remove(slider2);
    }
}