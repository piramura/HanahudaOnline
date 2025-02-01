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

/* JFrameを継承したHanahudaクラスを定義します。*/
public class Hanahuda extends JFrame {
    /* ウィンドウの基本設定 */
    private JFrame frame = new JFrame();
    /* コンストラクタでHanahudaの設定を行います。*/
    public Hanahuda(){
        frame.setTitle("Hanahuda");
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.NORMAL);
        frame.setSize(1920, 1080);
        frame.setLocation(0, 0);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        C_Title TitlePanel = new C_Title(frame);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Hanahuda HanahudaFrame = new Hanahuda();
        });
    }
}