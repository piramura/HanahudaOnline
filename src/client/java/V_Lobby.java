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

/* JPanelを継承したV_Lobbyクラスを定義します。*/
class V_Lobby extends JPanel {
    /* privateで宣言します。*/
    private ImageLoader imageLoader = new ImageLoader();
    private JTextField playerNameInputField;
    private JPanel panel;
    /* コンストラクタでV_Lobbyの設定を行います。*/
    public V_Lobby(M_Lobby m_Lobby) {
        /* プレイヤー名の初期値を取得*/
        String initialPlayerName = AppPreferences.getString("プレイヤー名", "player");
        /* 入力フィールドの初期化*/
        playerNameInputField = new JTextField(initialPlayerName, 10);
        playerNameInputField.setFont(new Font("Arial", Font.BOLD, 16));
        ((AbstractDocument) playerNameInputField.getDocument()).setDocumentFilter(new ConditionalAlphabetOnlyFilter(m_Lobby.getIsSettingOpen(), m_Lobby.getIsPlayerIcon()));
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
                AppPreferences.putString("プレイヤー名", newPlayerName);
            }
        });
        /* パネルの作成*/
        panel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                /* 画面中央に向かってズームする*/
                g2d.translate(960, 540);
                g2d.scale(m_Lobby.getScaleFactor(), m_Lobby.getScaleFactor());
                g2d.translate(-960, -540);
                /* 背景の壁の描画*/
                g.drawImage(imageLoader.getWall(), (int)((-960 * (m_Lobby.getWallSize() - 1))), 0, (int)(1920 * m_Lobby.getWallSize()), 1080, this);
                if(!m_Lobby.getIsDoorsOpen()){
                    g.drawImage(imageLoader.getRightDoor(), (int)((1920 * (m_Lobby.getDoorSize() - 1))), 0, this);
                    g.drawImage(imageLoader.getLeftDoor(), (int)((-1920 * (m_Lobby.getDoorSize() - 1))), 0, this);
                } else {
                    /* ドアが開くアニメーション*/
                    int offsetX = (int) (m_Lobby.getZoomFactor() * 480);
                    g.drawImage(imageLoader.getLeftDoor(), -offsetX, 0, this);
                    g.drawImage(imageLoader.getRightDoor(), offsetX, 0, this);
                }
                /* 対戦時のプレイヤーを識別するための長方形を用意する*/
                g.setColor(Color.BLUE);
                g.fillRect(m_Lobby.getMoveRectX() - 2920, 640, 5760, 200);
                g.setColor(Color.BLACK);
                /* 対戦時のプレイヤーアイコンの表示 */
                g.drawImage(imageLoader.getCard(AppPreferences.getInt("プレイヤーアイコン", 0)), 365 + m_Lobby.getMoveX(), 665, 114, 153, this);
                /* 対戦時のプレイヤー名の表示*/
                String playerName = AppPreferences.getString("プレイヤー名", "player");
                playerName += String.format(" Lv.%d", AppPreferences.getInt("経験値", 0) / 50);
                Font currentFont = g.getFont();
                Font largerFont = currentFont.deriveFont(currentFont.getSize2D() * 5);
                g.setFont(largerFont);
                FontMetrics metrics = g.getFontMetrics(largerFont);
                int textX = 490;
                int textY = 745 + (metrics.getAscent() / 2) - metrics.getDescent();
                g.drawString(playerName, textX + m_Lobby.getMoveX(), textY);
                /* 対戦時の相手のプレイヤーを識別するための長方形を用意する*/
                g.setColor(Color.RED);
                g.fillRect(- m_Lobby.getMoveRectX() - 1000, 240, 5760, 200);
                g.setColor(Color.BLACK);
                /* 対戦時の相手のプレイヤーアイコンの表示 */
                int opponentIconX = 1080;
                int opponentIconY = 260;
                g.drawImage(imageLoader.getCard(m_Lobby.getOpponentIconNumber()), 1100 - m_Lobby.getMoveX(), 265, 114, 153, this);
                /* 対戦時の相手のプレイヤー名の表示*/
                String opponentPlayerName = m_Lobby.getOriginalOpponentPlayerName();
                opponentPlayerName += String.format(" Lv.%d", m_Lobby.getOpponentExperience() / 50);
                Font opponentLargerFont = g.getFont();
                g.setFont(opponentLargerFont);
                FontMetrics opponentMetrics = g.getFontMetrics(opponentLargerFont);
                int opponentTextX = 1224;
                int opponentTextY = 335 + (opponentMetrics.getAscent() / 2) - opponentMetrics.getDescent();
                g.drawString(opponentPlayerName, opponentTextX - m_Lobby.getMoveX(), opponentTextY);
                /*FadeInOut用白い画像*/
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, m_Lobby.getAlpha()));
                /* Lobbyの表示*/
                g.drawImage(imageLoader.getLobby(), 0, 0, this);
                /* 設定ボタンアイコンの表示*/
                g.drawImage(imageLoader.getSettingIcon(), 1835, 10, 77, 77, this);
                /* 役表示アイコンの表示*/
                g.drawImage(imageLoader.getRoleIcon(), 1835, 100, 77, 77, this);
                /* プレイヤーアイコンの表示*/
                g.drawImage(imageLoader.getCard(AppPreferences.getInt("プレイヤーアイコン", 0)), 60, 10, 114, 153, this);
                g.drawImage(imageLoader.getPlayerInfo(), 180, 10, 450, 153, this);
                /* インプットフィールドの表示*/
                playerNameInputField.setBounds(185, 50, 200, 30);
                if(!this.isAncestorOf(playerNameInputField)) {
                    this.add(playerNameInputField);
                }
                /* 経験値を取得し、レベルを計算*/
                int ex = AppPreferences.getInt("経験値", 0);
                int lvl = ex / 50;
                /* レベルを表示するテキストを作成*/
                JLabel levelLabel = new JLabel("Lv. " + lvl);
                levelLabel.setBounds(185, 90, 200, 30);
                levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
                levelLabel.setForeground(Color.WHITE);
                if (!this.isAncestorOf(levelLabel)) {
                    this.add(levelLabel);
                }
                /* 緑色の進捗バー（経験値に基づくバー）*/
                JPanel greenBar = new JPanel();
                int greenBarWidth = 8 * (ex % 50);
                greenBar.setBounds(185, 130, greenBarWidth, 20);
                greenBar.setBackground(Color.GREEN);
                this.add(greenBar);
                /* 黒い横長のバー（背景となるバー）*/
                JPanel blackBar = new JPanel();
                blackBar.setBounds(185, 130, 400, 20);
                blackBar.setBackground(Color.BLACK);
                this.add(blackBar);
                /* プレイヤー名と表示するテキストを作成*/
                JLabel playerNameLabel = new JLabel("PlayerName");
                playerNameLabel.setBounds(185, 10, 200, 30);
                playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                playerNameLabel.setForeground(Color.WHITE);
                if (!this.isAncestorOf(playerNameLabel)) {
                    this.add(playerNameLabel);
                }
                /* Session接続中の黒背景の表示*/
                if (m_Lobby.getIsSession()) {
                    g.drawImage(imageLoader.getBlackWindow(), 840, -10, 220, 100, this);
                    g.drawImage(imageLoader.getCloseIcon(), 1030, 5, 20, 20, this);
                }
                /* オンライン対戦ボタンの表示*/
                g.drawImage(imageLoader.getOnlineButton(), 1080, 125, 700, 200, this);
                /* コンピュータ対戦ボタンの表示*/
                g.drawImage(imageLoader.getComputerButton(), 1080, 325, 700, 200, this);
                /* 遊び方ボタンの表示*/
                g.drawImage(imageLoader.getHowToPlayButton(), 1080, 525, 700, 200, this);
                /* 設定ウィンドウの表示*/
                if(m_Lobby.getIsSettingOpen()){
                    g.drawImage(imageLoader.getSettingWindow(), 0, 0, this);
                    g.drawImage(imageLoader.getCloseIcon(), 1810, 50, 54, 54, this);
                    g.drawImage(imageLoader.getReturnTitleButton(), 720, 810, 480, 135, this);
                    g.drawImage(imageLoader.getCheckBox(), 775, 515, 80, 80, this);
                    g.drawImage(imageLoader.getCheckBox(), 775, 315, 80, 80, this);
                    if (m_Lobby.getIsCheckMarkVisibleBGM()) {
                        g.drawImage(imageLoader.getCheckMark(), 775, 315, 80, 80, this);
                    }
                    if (m_Lobby.getIsCheckMarkVisibleSE()) {
                        g.drawImage(imageLoader.getCheckMark(), 775, 515, 80, 80, this);
                    }
                }
                /* プレイヤーアイコンの表示*/
                if(m_Lobby.getIsPlayerIcon()) {
                    g.drawImage(imageLoader.getBlackWindow(), 0, 0, this);
                    g.drawImage(imageLoader.getCloseIcon(), 1810, 50, 54, 54, this);
                    /* playerIconの一覧の表示 */
                    for (int i = 0; i < 48; i++) {
                        g.drawImage(imageLoader.getCard(i), 800 + (i % 8) * 114, 80 + (i / 8) * 153, 114, 153, this);
                    }
                }
                /* ボールの描画*/
                for (Ball ball : m_Lobby.getBalls()) {
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
        return this.panel;
    }

    public JTextField getPlayerNameInputField() {
        return playerNameInputField;
    }
}
