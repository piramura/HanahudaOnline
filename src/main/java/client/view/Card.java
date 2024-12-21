import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Card extends JLabel {
    private int id; // カードの識別ID
    private String name; // カードの名前または情報
    private boolean isFaceDown; // 表裏状態
    private List<CardClickListener> listeners = new ArrayList<>(); // クリックリスナーリスト

    // IDと名前を受け取るコンストラクタ
    public Card(int id, String name) {
        this(id, name, false); // デフォルトで表向き
    }

    // ID、名前、表裏状態を受け取るコンストラクタ
    public Card(int id, String name, boolean isFaceDown) {
        super(name); // 表示用テキストを設定
        this.id = id;
        this.name = name;
        this.isFaceDown = isFaceDown;

        // 初期表示を表裏に応じて設定
        setText(isFaceDown ? "裏" : name);

        // 見た目の設定
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setPreferredSize(new Dimension(80, 100)); // サイズ調整

        // クリックイベントを追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                notifyListeners(); // クリックリスナーに通知
            }
        });
    }

    // Getterメソッド
    public int getId() {
        return id;
    }

    public boolean isFaceDown() {
        return isFaceDown;
    }

    public String getName() {
        return name;
    }

    // カードを裏返す処理
    public void flip() {
        isFaceDown = !isFaceDown;
        setText(isFaceDown ? "裏" : name);
    }

    // リスナーを追加
    public void addCardClickListener(CardClickListener listener) {
        listeners.add(listener);
    }

    // クリックリスナーに通知
    private void notifyListeners() {
        for (CardClickListener listener : listeners) {
            listener.onCardClick(name); // 名前を通知
        }
    }
}
