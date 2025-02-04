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

/* JPanelを継承したV_OnlineGameクラスを定義します。*/
class V_OnlineGame extends JPanel {
    /* privateで宣言します。*/
    private C_OnlineGame c_OnlineGame;
    private M_OnlineGame m_OnlineGame;
    private ImageLoader imageLoader;
    private JPanel panel;
    /* コンストラクタでV_OnlineGameの設定を行います。*/
    public V_OnlineGame(M_OnlineGame m_OnlineGame, ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        this.m_OnlineGame = m_OnlineGame;
        /* パネルの作成とアニメーション設定*/
        panel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Graphics2D g2 = (Graphics2D) g;
                /*FadeInOut用白い画像*/
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, m_OnlineGame.getAlpha()));
                /* 背景の壁の描画*/
                g.drawImage(imageLoader.getStage(), 0, 0, this);
                /* 山札の描画*/
                for(int i = 0; i < m_OnlineGame.getCardRemain(); i++){
                    g.drawImage(imageLoader.getCardBack(), 540 - i, 460 - i, this);
                }
                /* プレイヤー情報の表示*/
                g.drawImage(imageLoader.getCard(m_OnlineGame.getCardNumber()), 20, 880, this);
                Font currentFont = g.getFont();
                Font largerFont = currentFont.deriveFont(currentFont.getSize2D() * 5);
                g.setFont(largerFont);
                g.drawString(m_OnlineGame.getPlayerName(), 145, 950);
                g.drawString(String.format("Lv.%d",m_OnlineGame.getExperiment() / 50), 145, 1010);
                /* 相手のプレイヤー情報の表示*/
                g.drawImage(imageLoader.getCard(m_OnlineGame.getOpponentIconNumber()), 1465, 65, this);
                g.setFont(largerFont);
                g.drawString(m_OnlineGame.getOpponentPlayerName(), 1590, 135);
                g.drawString(String.format("Lv.%d",m_OnlineGame.getOpponentExperience() / 50), 1590, 195);
                /* 場のカードの表示*/
                for(int i = 0; i <= m_OnlineGame.getCardSize(); i++){
                    if(m_OnlineGame.getCurrentBoardListBoolean().get(i % 2).get(i / 2)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getCurrentBoardList().get(i % 2).get(i / 2)), 660 + (i / 2 + 1) * (600 / (((m_OnlineGame.getCurrentCardSize() + 1) / 2) + 1)), 460 + 110 * (i % 2 == 0 ? -1 : 1), this);
                    }
                }
                /* 手札カードの表示*/
                for(int i = 0; i < m_OnlineGame.getCurrentPlayerHandList().size(); i++) {
                    if(m_OnlineGame.getCurrentPlayerHandListBoolean().get(i)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getCurrentPlayerHandList().get(i)), 520 + i * 120, 860, this);
                    }
                }
                /* 相手カードの表示*/
                for(int i = 0; i < m_OnlineGame.getCurrentOpponentHandList().size(); i++) {
                    if(m_OnlineGame.getCurrentOpponentHandListBoolean().get(i)) {
                        g.drawImage(imageLoader.getCardBack(), 480 + i * 120, 80, this);
                    }
                }
                /* 場のカードの表示*/
                for(int i = 0; i <= m_OnlineGame.getCardSize(); i++){
                    if(m_OnlineGame.getBoardListBoolean().get(i % 2).get(i / 2)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getBoardList().get(i % 2).get(i / 2)), 660 + (i / 2 + 1) * (600 / (((m_OnlineGame.getCardSize() + 1) / 2) + 1)), 460 + 110 * (i % 2 == 0 ? -1 : 1), this);
                    }
                }
                /* 手札カードの表示*/
                for(int i = 0; i < m_OnlineGame.getPlayerHandList().size(); i++) {
                    if(m_OnlineGame.getPlayerHandListBoolean().get(i)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getPlayerHandList().get(i)), 520 + i * 120, 860, this);
                    }
                }
                /* 相手カードの表示*/
                for(int i = 0; i < m_OnlineGame.getOpponentHandList().size(); i++) {
                    if(m_OnlineGame.getOpponentHandListBoolean().get(i)) {
                        g.drawImage(imageLoader.getCardBack(), 480 + i * 120, 80, this);
                    }
                }
                /* 取り札の表示*/
                for(int i = 0; i < m_OnlineGame.getIsGokou().size(); i++) {
                    if(m_OnlineGame.getIsGokou().get(i)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getGokou().get(i)), 1500 + i * 70, 320, this);
                    }
                }
                for(int i = 0; i < m_OnlineGame.getIsTane().size(); i++) {
                    if(m_OnlineGame.getIsTane().get(i)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getTane().get(i)), 1500 + i * 35, 490, this);
                    }
                }
                for(int i = 0; i < m_OnlineGame.getIsTann().size(); i++) {
                    if(m_OnlineGame.getIsTann().get(i)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getTann().get(i)), 1500 + i * 32, 660, this);
                    }
                }
                for(int i = 0; i < m_OnlineGame.getIsKasu().size(); i++) {
                    if(m_OnlineGame.getIsKasu().get(i)) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getKasu().get(i)), 1500 + i * 12, 830, this);
                    }
                }
                /* 相手の取り札の表示*/
                for(int i = 0; i < m_OnlineGame.getOpponentIsGokou().size(); i++) {
                    if(m_OnlineGame.getOpponentIsGokou().get(i)) {
                        g.drawImage(imageLoader.getRotateCard(m_OnlineGame.getGokou().get(i)), 300 - i * 70, 600, this);
                    }
                }
                for(int i = 0; i < m_OnlineGame.getOpponentIsTane().size(); i++) {
                    if(m_OnlineGame.getOpponentIsTane().get(i)) {
                        g.drawImage(imageLoader.getRotateCard(m_OnlineGame.getTane().get(i)), 300 - i * 35, 430, this);
                    }
                }
                for(int i = 0; i < m_OnlineGame.getOpponentIsTann().size(); i++) {
                    if(m_OnlineGame.getOpponentIsTann().get(i)) {
                        g.drawImage(imageLoader.getRotateCard(m_OnlineGame.getTann().get(i)), 300 - i * 32, 260, this);
                    }
                }
                for(int i = 0; i < m_OnlineGame.getOpponentIsKasu().size(); i++) {
                    if(m_OnlineGame.getOpponentIsKasu().get(i)) {
                        g.drawImage(imageLoader.getRotateCard(m_OnlineGame.getKasu().get(i)), 300 - i * 12, 90, this);
                    }
                }
                /* 動くカードの表示*/
                if(m_OnlineGame.getIsMoveCard()) {
                    g.drawImage(imageLoader.getMoveCard(), (int)m_OnlineGame.getMoveX(), (int)m_OnlineGame.getMoveY(), this);
                }
                /* 画面を薄暗くする*/
                if(m_OnlineGame.getDarkenScreen() && m_OnlineGame.getIsStarted()) {
                    g.drawImage(imageLoader.getDarkenScreen(), 0, 0, this);
                }
                /* 手札カードの表示*/
                for(int i = 0; i < m_OnlineGame.getPlayerHandList().size(); i++) {
                    if(m_OnlineGame.getPlayerHandListBoolean().get(i) && m_OnlineGame.getIsHandSelect()) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getPlayerHandList().get(i)), 520 + i * 120, 860, this);
                    }
                }
                /* 場のカードの表示*/
                for(int i = 0; i < m_OnlineGame.getCardSize(); i++){
                    if(m_OnlineGame.getFlushBoardListBoolean().get(i % 2).get(i / 2) && m_OnlineGame.getIsFieldSelect()) {
                        g.drawImage(imageLoader.getCard(m_OnlineGame.getBoardList().get(i % 2).get(i / 2)), 660 + (i / 2 + 1) * (600 / (((m_OnlineGame.getCardSize() + 1) / 2) + 1)), 460 + 110 * (i % 2 == 0 ? -1 : 1), this);
                    }
                }
                /* めくられたカードの表示*/
                if(m_OnlineGame.getIsCardOpen()){
                    g.drawImage(imageLoader.getCard(m_OnlineGame.getPlayerHandList().get(m_OnlineGame.getPlayerHandList().size()-1)), 540 - m_OnlineGame.getCardRemain(), 460 - m_OnlineGame.getCardRemain(), this);
                }
                /* 手札を光らせる*/
                for (int i = 0; i < m_OnlineGame.getFlushPlayerHandListBoolean().size(); i++) {
                    if (m_OnlineGame.getFlushPlayerHandListBoolean().get(i) && m_OnlineGame.getIsHandSelect()) {
                        Rectangle rect = m_OnlineGame.getPlayerHandRectangles()[i];
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
                for (int i = 0; i < m_OnlineGame.getCardSize(); i++) {
                    if (m_OnlineGame.getFlushBoardListBoolean().get(i % 2).get(i / 2) && m_OnlineGame.getIsFieldSelect()) {
                        Rectangle rect = m_OnlineGame.getBoardRectangles()[i % 2][i / 2];
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
                if(!m_OnlineGame.getIsMyTurn()){
                    float opacity = (float) Math.abs(Math.sin(System.currentTimeMillis() * 0.001));
                    g2.setColor(new Color(1.0f, 1.0f, 1.0f));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    if(m_OnlineGame.getDarkenScreen() && m_OnlineGame.getIsStarted())g2.drawString("あいてがプレイ中", 760, 550);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                /* ターン表示の裏*/
                if(m_OnlineGame.getIsMyTurn()){
                    g.setColor(Color.BLUE);
                    g.fillRect(m_OnlineGame.getMoveRectTurnX(), 440, 1920 * 3, 200);
                }
                else {
                    g.setColor(Color.RED);
                    g.fillRect(-3000-m_OnlineGame.getMoveRectTurnX(), 440, 1920 * 3, 200);
                }
                g.setColor(Color.BLACK);
                /* ターン表示*/
                if(m_OnlineGame.getIsMyTurn())g.drawString("あなたのターン", m_OnlineGame.getTextTurnX() - 100, 550);
                else g.drawString("あいてのターン", 1800-m_OnlineGame.getTextTurnX(), 550);
                /* 対戦開始の合図の裏*/
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(m_OnlineGame.getMoveRectX(), 440, 1920 * 3, 200);
                g.setColor(Color.BLACK);
                /* 対戦開始の合図*/
                g.drawString("第一局", m_OnlineGame.getTextX(), 520);
                g.drawString("対戦開始", m_OnlineGame.getTextX(), 580);
                /* こいこいの表示*/
                if(m_OnlineGame.getIsKOIKOI()){
                    g.drawString("勝負", 420, 550);
                    g.drawString("こいこい", 1220, 550);
                }
                /* 設定ボタンアイコンの表示*/
                g.drawImage(imageLoader.getSettingIcon(), 1835, 10, 77, 77, this);
                /* 役表示アイコンの表示*/
                g.drawImage(imageLoader.getRoleIcon(), 1835, 140, 77, 77, this);
                /* 設定ウィンドウの表示*/
                if(m_OnlineGame.getIsSettingOpen()){
                    g.drawImage(imageLoader.getSettingWindow(), 0, 0, this);
                    g.drawImage(imageLoader.getCloseIcon(), 1810, 50, 54, 54, this);
                    g.drawImage(imageLoader.getReturnLobbyButton(), 720, 810, 480, 135, this);
                    g.drawImage(imageLoader.getCheckBox(), 775, 515, 80, 80, this);
                    g.drawImage(imageLoader.getCheckBox(), 775, 315, 80, 80, this);
                    if (m_OnlineGame.getCheckMarkVisibleBGM()) {
                        g.drawImage(imageLoader.getCheckMark(), 775, 315, 80, 80, this);
                    }
                    if (m_OnlineGame.getCheckMarkVisibleSE()) {
                        g.drawImage(imageLoader.getCheckMark(), 775, 515, 80, 80, this);
                    }
                }
                /* ボールの描画*/
                for (Ball ball : m_OnlineGame.getBalls()) {
                    g.drawImage(imageLoader.getCherryBlossom(), ball.x, ball.y, 30, 30, this);
                }
            }
        };
        /* タイマーイベント処理*/
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.repaint();
            }
        });
        timer.start();
    }
    public JPanel getPanel() {
        return panel;
    }
}