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
import javax.swing.*;
import java.awt.*;

public class ImageLoader {
    private Image wall;
    private Image rightDoor;
    private Image leftDoor;
    private Image startButton;
    private Image settingWindow;
    private Image closeIcon;
    private Image settingIcon;
    private Image gameOverButton;
    private Image checkBox;
    private Image checkMark;
    private Image cherryBlossom;
    private Image lobby;
    private Image returnTitleButton;
    private Image onlineButton;
    private Image computerButton;
    private Image howToPlayButton;
    private Image roleIcon;
    private Image blackWindow;
    private Image playerInfo;

    public ImageLoader() {
        wall = new ImageIcon("画像/壁.png").getImage();
        rightDoor = new ImageIcon("画像/右扉.png").getImage();
        leftDoor = new ImageIcon("画像/左扉.png").getImage();
        startButton = new ImageIcon("画像/画面をタッチしてゲームスタート.png").getImage();
        settingWindow = new ImageIcon("画像/設定ウィンドウ.png").getImage();
        closeIcon = new ImageIcon("画像/バツアイコン.png").getImage();
        settingIcon = new ImageIcon("画像/設定アイコン.png").getImage();
        gameOverButton = new ImageIcon("画像/ゲーム終了.png").getImage();
        checkBox = new ImageIcon("画像/チェック欄.png").getImage();
        checkMark = new ImageIcon("画像/チェックマーク.png").getImage();
        cherryBlossom = new ImageIcon("画像/桜.png").getImage();
        lobby = new ImageIcon("画像/ロビー.png").getImage();
        returnTitleButton = new ImageIcon("画像/タイトルに戻る.png").getImage();
        onlineButton = new ImageIcon("画像/オンライン対戦.png").getImage();
        computerButton = new ImageIcon("画像/コンピュータ対戦.png").getImage();
        howToPlayButton = new ImageIcon("画像/遊び方.png").getImage();
        roleIcon = new ImageIcon("画像/役表アイコン.png").getImage();
        blackWindow = new ImageIcon("画像/黒背景.png").getImage();
        playerInfo = new ImageIcon("画像/プレイヤー情報.png").getImage();
    }
    public Image getWall() {
        return wall;
    }

    public Image getRightDoor() {
        return rightDoor;
    }

    public Image getLeftDoor() {
        return leftDoor;
    }

    public Image getStartButton() {
        return startButton;
    }

    public Image getSettingWindow() {
        return settingWindow;
    }

    public Image getCloseIcon() {
        return closeIcon;
    }

    public Image getSettingIcon() {
        return settingIcon;
    }

    public Image getGameOverButton() {
        return gameOverButton;
    }

    public Image getCheckBox() {
        return checkBox;
    }

    public Image getCheckMark() {
        return checkMark;
    }

    public Image getCherryBlossom() {
        return cherryBlossom;
    }

    public Image getLobby() {
        return lobby;
    }

    public Image getReturnTitleButton() {
        return returnTitleButton;
    }

    public Image getOnlineButton() {
        return onlineButton;
    }

    public Image getComputerButton() {
        return computerButton;
    }

    public Image getHowToPlayButton() {
        return howToPlayButton;
    }

    public Image getRoleIcon() {
        return roleIcon;
    }

    public Image getBlackWindow() {
        return blackWindow;
    }

    public Image getPlayerInfo() {
        return playerInfo;
    }
}
