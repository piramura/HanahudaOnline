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

/* JPanelを継承したM_OnlineGameクラスを定義します。*/
class M_OnlineGame extends JPanel {
    /* privateで宣言します。*/
    private String playerName = AppPreferences.getString("プレイヤー名", "player");
    private String opponentPlayerName = "Computer";
    private int experiment = AppPreferences.getInt("経験値", 0);
    private int opponentExperience = 0;
    private int cardNumber = AppPreferences.getInt("プレイヤーアイコン", 0);
    private int opponentIconNumber = 0;
    private int textX = -1000;
    private int moveRectX = -6000;
    private int textTurnX = -1000;
    private int moveRectTurnX = -6000;
    private int cardSize = 0;
    private int currentCardSize = 0;
    private int cardRemain = 48;
    private int playerHandSelect = 0;
    private int steps = 4;
    private int turn = 0;
    private int cardNumbers = 0;
    private int playerId = 0;
    private boolean isMoveCard = false;
    private boolean stopMovement = false;
    private boolean stopTurnMovement = false;
    private boolean isSettingOpen = false;
    private boolean checkMarkVisibleBGM = false;
    private boolean checkMarkVisibleSE = false;
    private boolean isReturnLobby = false;
    private boolean isMyTurn = true;
    private boolean darkenScreen = false;
    private boolean isHandSelect = false;
    private boolean isFieldSelect = false;
    private boolean isCardOpen = false;
    private boolean isStarted = false;
    private boolean isBoardSelect = false;
    private boolean isHandSelected = false;
    private boolean isKOIKOI = false;
    private List<Integer> boardList1 = new ArrayList<>();
    private List<List<Integer>> currentBoardList = Arrays.asList(Arrays.asList(0, 1, 2, 3, -1, -1, -1, -1 , -1), Arrays.asList(4, 5, 6, 7, -1, -1, -1, -1, -1, -1));
    private List<Integer> currentPlayerHandList = Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15);
    private List<Integer> currentOpponentHandList = Arrays.asList(16, 17, 18, 19, 20, 21, 22, 23);
    private int[] cardState = new int[48];
    private List<List<Integer>> boardList = Arrays.asList(Arrays.asList(0, 1, 2, 3, -1, -1, -1, -1 , -1), Arrays.asList(4, 5, 6, 7, -1, -1, -1, -1, -1, -1));
    private List<Integer> playerHandList = Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15);
    private List<Integer> opponentHandList = Arrays.asList(16, 17, 18, 19, 20, 21, 22, 23);
    private List<List<Boolean>> flushBoardListBoolean = Arrays.asList(Arrays.asList(false, false, false, false, false, false, false, false, false), Arrays.asList(false, false, false, false, false, false, false, false, false));
    private List<Boolean> flushPlayerHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<List<Boolean>> boardListBoolean = Arrays.asList(Arrays.asList(false, false, false, false, false, false, false, false, false), Arrays.asList(false, false, false, false, false, false, false, false, false));
    private List<Boolean> playerHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<Boolean> opponentHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<List<Boolean>> currentBoardListBoolean = Arrays.asList(Arrays.asList(false, false, false, false, false, false, false, false, false), Arrays.asList(false, false, false, false, false, false, false, false, false));
    private List<Boolean> currentPlayerHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<Boolean> currentOpponentHandListBoolean = Arrays.asList(false, false, false, false, false, false, false, false);
    private List<Integer> gokou = Arrays.asList(0, 8, 28, 40, 44);
    private List<Integer> tane = Arrays.asList(4, 12, 16, 20, 24, 29, 32, 36, 41);
    private List<Integer> tann = Arrays.asList(1, 5, 9, 13, 17, 21, 25, 33, 37, 42);
    private List<Integer> kasu = Arrays.asList(2, 3, 6, 7, 10, 11, 14, 15, 18, 19, 22, 23, 26, 27, 30, 31, 34, 35, 38, 39, 43, 45, 46, 47);
    private List<Boolean> isGokou = Arrays.asList(false, false, false, false, false);
    private List<Boolean> isTane = Arrays.asList(false, false, false, false, false, false, false, false, false);
    private List<Boolean> isTann = Arrays.asList(false, false, false, false, false, false, false, false, false, false);
    private List<Boolean> isKasu = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);
    private List<Boolean> opponentIsGokou = Arrays.asList(false, false, false, false, false);
    private List<Boolean> opponentIsTane = Arrays.asList(false, false, false, false, false, false, false, false, false);
    private List<Boolean> opponentIsTann = Arrays.asList(false, false, false, false, false, false, false, false, false, false);
    private List<Boolean> opponentIsKasu = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);
    private ArrayList<Ball> balls = new ArrayList<>();
    private Random random = new Random();
    private float alpha = 0.0f;
    private double deltaX;
    private double deltaY;
    private double moveX = 0;
    private double moveY = 0;
    private double targetMoveX;
    private double targetMoveY;
    private Rectangle[][] boardRectangles = new Rectangle[2][9];
    private Rectangle[] playerHandRectangles = new Rectangle[8];
    private Rectangle settingButtonBounds = new Rectangle();
    private Rectangle closeButtonBounds = new Rectangle();
    private Rectangle returnLobbyButtonBounds = new Rectangle();
    private Rectangle checkBoxBGMBounds = new Rectangle();
    private Rectangle checkBoxSEBounds = new Rectangle();
    private Rectangle KOIKOIButtonBounds = new Rectangle();
    private Rectangle ENDButtonBounds = new Rectangle();
    private Rectangle roleIconBounds = new Rectangle();
    private JSlider slider1;
    private JSlider slider2;
    private ImageLoader imageLoader;
    private GameController controller;
    private GameClient client;
    private C_OnlineGame c_OnlineGame;
    /* コンストラクタでM_OnlineGameの設定を行います。*/
    public M_OnlineGame(C_OnlineGame c_OnlineGame, GameController gameController, GameClient gameClient, ImageLoader imageLoader) {
        /* 初期設定*/
        this.imageLoader = imageLoader;
        this.client = gameClient;
        this.controller = gameController;
        this.c_OnlineGame = c_OnlineGame;
        /* フェチ処理*/
        try{
            client.fetchGameState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* カード情報を保存する*/
        for(int i = 0;i < 48; i++){
            cardState[i] = 0;
        }
        /* 対戦相手の情報を読み込む*/
        getOpponentInfo();
        /* カード情報の初期化*/
        updateCard();
        /* ターン更新タイマー*/
        Timer updateTurnTimer = new Timer(64, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    client.fetchGameState();
                    turn = gameController.getCurrentTurn();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                /* ターンが切り替わったことを判定*/
                if(turn % 2 + 1 == playerId){
                    if(!isMyTurn){
                        darkenScreen = false;
                        isCardOpen = false;
                        isMyTurn = true;
                        isBoardSelect = false;
                        isHandSelected = false;
                        List<Integer> takeOpponentCard;
                        if(playerId == 2) takeOpponentCard = gameController.getPlayer1Captures();
                        else takeOpponentCard = gameController.getPlayer2Captures();
                        for(int i=0;i<takeOpponentCard.size();i++){
                            cardState[takeOpponentCard.get(i)] = 5;
                        }
                        updateState();
                    }
                }
                else{
                    if(isMyTurn){
                        isMyTurn = false;
                        darkenScreen = true;
                    }
                }
            }
        });
        updateTurnTimer.start();
        /* フェードイン用タイマー*/
        Timer fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.06f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    ((Timer) e.getSource()).stop();
                    dealCards();
                }
            }
        });
        fadeTimer.start();
        /* 当たり判定の作成*/
        for(int i = 0;i < cardSize; i++) {
            boardRectangles[i % 2][i / 2] = new Rectangle(660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 460 + 110 * (i % 2 == 0 ? -1 : 1), 114, 153);
        }
        for(int i = 0;i < 8; i++) {
            playerHandRectangles[i] = new Rectangle(520 + i * 120, 860, 114, 153);
        }
        this.settingButtonBounds = new Rectangle(1835, 10, 77, 77);
        this.roleIconBounds = new Rectangle(1835, 140, 77, 77);
        this.closeButtonBounds = new Rectangle(1810, 50, 54, 54);
        this.returnLobbyButtonBounds = new Rectangle(720, 810, 480, 135);
        this.checkBoxBGMBounds = new Rectangle(775, 315, 80, 80);
        this.checkBoxSEBounds = new Rectangle(775, 515, 80, 80);
        this.ENDButtonBounds = new Rectangle(420, 440, 400, 100);
        this.KOIKOIButtonBounds = new Rectangle(1220, 440, 400, 100);
        /* スライダーの値をPreferencesから取得*/
        int slider1Value = AppPreferences.getInt("BGM音量", 50);
        int slider2Value = AppPreferences.getInt("SE音量", 50);
        /* チェックマークの値をPreferencesから取得*/
        checkMarkVisibleBGM = AppPreferences.getBoolean("BGM", true);
        checkMarkVisibleSE = AppPreferences.getBoolean("SE", true);
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
    /* 取り札のカードの座標を取り出す*/
    public int takenMyCardPositionX(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                return 1500 + i * 70;
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                return 1500 + i * 35;
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                return 1500 + i * 32;
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                return 1500 + i * 12;
            }
        }
        return -1;
    }

    public int takenMyCardPositionY(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                return 320;
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                return 490;
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                return 660;
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                return 830;
            }
        }
        return -1;
    }
    /* カード取った*/
    public void takenMyCard(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                isGokou.set(i, true);
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                isTane.set(i, true);
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                isTann.set(i, true);
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                isKasu.set(i, true);
            }
        }
    }
    /* カード取った*/
    public void takenOpponentCard(int cardNum) {
        for(int i = 0; i < gokou.size(); i++) {
            if(gokou.get(i) == cardNum) {
                opponentIsGokou.set(i, true);
            }
        }
        for(int i = 0; i < tane.size(); i++) {
            if(tane.get(i) == cardNum) {
                opponentIsTane.set(i, true);
            }
        }
        for(int i = 0; i < tann.size(); i++) {
            if(tann.get(i) == cardNum) {
                opponentIsTann.set(i, true);
            }
        }
        for(int i = 0; i < kasu.size(); i++) {
            if(kasu.get(i) == cardNum) {
                opponentIsKasu.set(i, true);
            }
        }
    }

    /* 山札からカードをめくる*/
    public void cardOpen() {
        int flushCardCount = 0;
        for(int i=0;i<cardSize;i++){
            if(playerHandList.get(playerHandList.size() - 1) / 4 == boardList.get(i % 2).get(i / 2) / 4 && boardListBoolean.get(i % 2).get(i / 2)){
                flushBoardListBoolean.get(i % 2).set(i / 2, true);
                flushCardCount++;
            }
        }
        if(flushCardCount == 0){
            isCardOpen = false;
            moveX = 540 - cardRemain;
            moveY = 540 - cardRemain;
            cardRemain--;
            imageLoader.setMoveCard(imageLoader.getCard(playerHandList.get(playerHandList.size() - 1)));
            targetMoveX = 660 + (cardSize / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
            targetMoveY = 460 + 110 * (cardSize % 2 == 0 ? -1 : 1);
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            int[] currentSteps = {0};
            try{
                client.playCard(playerHandList.get(playerHandList.size() - 1), playerId, -1);
                client.fetchGameState();
            } catch (Exception ev) {
                ev.printStackTrace();
            }
            Timer firstTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    boardList.get(cardSize % 2).set(cardSize / 2, playerHandList.get(playerHandList.size() - 1));
                    boardListBoolean.get(cardSize % 2).set(cardSize / 2, true);
                    updateState();
                }
            });
            firstTimer.start();
            isMoveCard = true;
        }
        else{
            darkenScreen = true;
            isCardOpen = true;
            isFieldSelect = true;
        }
    }

    /* カードのUIを更新する*/
    public void updateState() {
        for(int i=0;i<18;i++){
            currentBoardListBoolean.get(i % 2).set(i / 2, boardListBoolean.get(i % 2).get(i / 2));
            currentBoardList.get(i % 2).set(i / 2, boardList.get(i % 2).get(i / 2));
        }
        currentOpponentHandListBoolean = new ArrayList<>();
        for (boolean b : opponentHandListBoolean) {
            currentOpponentHandListBoolean.add(b);
        }
        currentPlayerHandListBoolean = new ArrayList<>();
        for (boolean b : playerHandListBoolean) {
            currentPlayerHandListBoolean.add(b);
        }
        currentOpponentHandList = new ArrayList<>(opponentHandList);
        currentPlayerHandList = new ArrayList<>(playerHandList);
        currentCardSize = cardSize;
        updateCard();
        for(int i=0;i<18;i++){
            boardListBoolean.get(i % 2).set(i / 2, false);
        }
        for(int i=0;i<8;i++){
            opponentHandListBoolean.set(i, false);
        }
        for(int i=0;i<8;i++){
            playerHandListBoolean.set(i, false);
        }
        for(int i=0;i<18;i++){
            flushBoardListBoolean.get(i % 2).set(i / 2, false);
        }
        int[] cnt = {0};
        Timer stateTimer = new Timer(16, e -> {
            ((Timer) e.getSource()).stop();
            do{
                cardNumbers = -1;
                if(cnt[0] <= currentCardSize){
                    if(currentBoardListBoolean.get(cnt[0] % 2).get(cnt[0] / 2)){
                        cardNumbers = currentBoardList.get(cnt[0] % 2).get(cnt[0] / 2);
                        moveX = 660 + (cnt[0] / 2 + 1) * (600 / (((currentCardSize + 1) / 2) + 1));
                        moveY = 460 + 110 * (cnt[0] % 2 == 0 ? -1 : 1);
                        currentBoardListBoolean.get(cnt[0] % 2).set(cnt[0] / 2, false);
                    }
                }
                else if(cnt[0] - currentCardSize - 1 < currentPlayerHandList.size()) {
                    if(currentPlayerHandListBoolean.get(cnt[0] - currentCardSize - 1)){
                        cardNumbers = currentPlayerHandList.get(cnt[0] - currentCardSize - 1);
                        moveX = 520 + (cnt[0] - currentCardSize - 1) * 120;
                        moveY = 860;
                        currentPlayerHandListBoolean.set(cnt[0] - currentCardSize - 1, false);
                    }
                }
                else if(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size() < currentOpponentHandList.size()){
                    if(currentOpponentHandListBoolean.get(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size())){
                        cardNumbers = currentOpponentHandList.get(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size());
                        moveX = 480 + (cnt[0] - currentCardSize - 1 - currentPlayerHandList.size()) * 120;
                        moveY = 80;
                        currentOpponentHandListBoolean.set(cnt[0] - currentCardSize - 1 - currentPlayerHandList.size(), false);
                    }
                }
                else {
                    try{
                        client.fetchGameState();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    updateRectangles();
                    List<Integer> player1Captures = controller.getPlayer1Captures();
                    List<Integer> player2Captures = controller.getPlayer2Captures();
                    for(int i=0;i<player1Captures.size();i++){
                        if(playerId == 1) takenMyCard(player1Captures.get(i));
                        if(playerId == 2) takenOpponentCard(player1Captures.get(i));
                    }
                    for(int i=0;i<player2Captures.size();i++){
                        if(playerId == 1) takenOpponentCard(player2Captures.get(i));
                        if(playerId == 2) takenMyCard(player2Captures.get(i));
                    }
                    if(!isCardOpen && !isBoardSelect && isHandSelected){
                        /*手札から選択*/
                        isCardOpen = true;
                        isBoardSelect = true;
                        cardRemain--;
                        cardOpen();
                    }
                    else if(!isCardOpen && !isBoardSelect && !isHandSelected){
                        /*相手側*/
                        darkenScreen = true;
                        isHandSelected = true;
                        if(isMyTurn){
                            isHandSelect = true;
                        }
                        if(cardSize > 0)boardListBoolean.get((cardSize - 1) % 2).set((cardSize - 1) / 2, true);
                        updatePlayerHandFlush();
                    }
                    else{
                        /*二回目*/
                        isKOIKOI = controller.getIsKoikoi();
                    }
                    break;
                }
                if(cardNumbers == -1){
                    cnt[0]++;
                    continue;
                }
                else if(cardState[cardNumbers] == 3)imageLoader.setMoveCard(imageLoader.getCardBack());
                else imageLoader.setMoveCard(imageLoader.getCard(cardNumbers));
                for(int j = 0;j < cardSize; j++){
                    if(boardList.get(j % 2).get(j / 2) == cardNumbers) {
                        targetMoveX = 660 + (j / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
                        targetMoveY = 460 + 110 * (j % 2 == 0 ? -1 : 1);
                    }
                }
                for(int j = 0;j < playerHandList.size(); j++){
                    if(playerHandList.get(j) == cardNumbers) {
                        targetMoveX = 520 + j * 120;
                        targetMoveY = 860;
                    }
                }
                for(int j = 0;j < opponentHandList.size(); j++){
                    if(opponentHandList.get(j) == cardNumbers) {
                        targetMoveX = 480 + j * 120;
                        targetMoveY = 80;
                    }
                }
                for(int j = 0; j < gokou.size(); j++) {
                    if(cardState[cardNumbers] == 4 && gokou.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 70;
                        targetMoveY = 320;
                    }
                }
                for(int j = 0; j < tane.size(); j++) {
                    if(cardState[cardNumbers] == 4 && tane.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 35;
                        targetMoveY = 460;
                    }
                }
                for(int j = 0; j < tann.size(); j++) {
                    if(cardState[cardNumbers] == 4 && tann.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 32;
                        targetMoveY = 690;
                    }
                }
                for(int j = 0; j < kasu.size(); j++) {
                    if(cardState[cardNumbers] == 4 && kasu.get(j) == cardNumbers) {
                        targetMoveX = 1500 + cardNumbers * 12;
                        targetMoveY = 830;
                    }
                }
                for(int j = 0; j < gokou.size(); j++) {
                    if(cardState[cardNumbers] == 5 && gokou.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 70;
                        targetMoveY = 600;
                    }
                }
                for(int j = 0; j < tane.size(); j++) {
                    if(cardState[cardNumbers] == 5 && tane.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 35;
                        targetMoveY = 430;
                    }
                }
                for(int j = 0; j < tann.size(); j++) {
                    if(cardState[cardNumbers] == 5 && tann.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 32;
                        targetMoveY = 240;
                    }
                }
                for(int j = 0; j < kasu.size(); j++) {
                    if(cardState[cardNumbers] == 5 && kasu.get(j) == cardNumbers) {
                        targetMoveX = 300 - cardNumbers * 12;
                        targetMoveY = 90;
                    }
                }
                deltaX = (targetMoveX - moveX) / (double)steps;
                deltaY = (targetMoveY - moveY) / (double)steps;
                if(moveX == targetMoveX && moveY == targetMoveY){
                    for(int j = 0;j < cardSize; j++){
                        if(boardList.get(j % 2).get(j / 2) == cardNumbers) {
                            boardListBoolean.get(j % 2).set(j / 2, true);
                        }
                    }
                    for(int j = 0;j < playerHandList.size(); j++){
                        if(playerHandList.get(j) == cardNumbers) {
                            playerHandListBoolean.set(j, true);
                        }
                    }
                    for(int j = 0;j < opponentHandList.size(); j++){
                        if(opponentHandList.get(j) == cardNumbers) {
                            opponentHandListBoolean.set(j, true);
                        }
                    }
                    if(cardState[cardNumbers] == 4)takenMyCard(cardNumbers);
                    if(cardState[cardNumbers] == 5)takenOpponentCard(cardNumbers);
                }
            }while(moveX == targetMoveX && moveY == targetMoveY);
            if(cardNumbers!=-1) {
                int[] currentSteps = {0};
                Timer timer = new Timer(16, ex -> {
                    moveX += deltaX;
                    moveY += deltaY;
                    currentSteps[0]++;
                    if (currentSteps[0] == steps) {
                        moveX = targetMoveX;
                        moveY = targetMoveY;
                        ((Timer) ex.getSource()).stop();
                        isMoveCard = false;
                        for(int j = 0;j < cardSize; j++){
                            if(boardList.get(j % 2).get(j / 2) == cardNumbers) {
                                boardListBoolean.get(j % 2).set(j / 2, true);
                            }
                        }
                        for(int j = 0;j < playerHandList.size(); j++){
                            if(playerHandList.get(j) == cardNumbers) {
                                playerHandListBoolean.set(j, true);
                            }
                        }
                        for(int j = 0;j < opponentHandList.size(); j++){
                            if(opponentHandList.get(j) == cardNumbers) {
                                opponentHandListBoolean.set(j, true);
                            }
                        }
                        if(cardState[cardNumbers] == 4)takenMyCard(cardNumbers);
                        if(cardState[cardNumbers] == 5)takenOpponentCard(cardNumbers);
                        cnt[0]++;
                        ((Timer) e.getSource()).start();
                    }
                });
                timer.start();
                isMoveCard = true;
            }
        });
        stateTimer.start();
    }

    /* 当たり判定を更新*/
    public void updateRectangles(){
        for(int i = 0;i < cardSize; i++) {
            if(i + 2 >= cardSize || (i + 2 < cardSize && boardList.get(i % 2).get(i / 2) / 4 != boardList.get(i % 2).get(i / 2 + 1) / 4))boardRectangles[i % 2][i / 2] = new Rectangle(660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 460 + 110 * (i % 2 == 0 ? -1 : 1), 114, 153);
            else boardRectangles[i % 2][i / 2] = new Rectangle(660 + (i / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1)), 460 + 110 * (i % 2 == 0 ? -1 : 1), Math.min(114,600 / (((cardSize + 1) / 2) + 1)), 153);
        }
    }

    /* プレイヤーが山札の上と場のカードを選んだ*/
    public void boardSelect(int cardId, int boardSelected) {
        darkenScreen = false;
        isCardOpen = false;
        imageLoader.setMoveCard(imageLoader.getCard(cardId));
        moveX = 520 - cardRemain;
        moveY = 520 - cardRemain;
        cardRemain--;
        int[] currentSteps = {0};
        targetMoveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
        targetMoveY = 460 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
        deltaX = (targetMoveX - moveX) / (double)steps;
        deltaY = (targetMoveY - moveY) / (double)steps;
        Timer thirdTimer = new Timer(16, ex -> {
            moveX += deltaX;
            moveY += deltaY;
            currentSteps[0]++;
            if (currentSteps[0] == steps) {
                moveX = targetMoveX;
                moveY = targetMoveY;
                ((Timer) ex.getSource()).stop();
                takenMyCard(boardList.get(boardSelected % 2).get(boardSelected / 2));
                updateState();
            }
        });
        Timer secondTimer = new Timer(16, ex -> {
            moveX += deltaX;
            moveY += deltaY;
            currentSteps[0]++;
            if (currentSteps[0] == steps) {
                moveX = targetMoveX;
                moveY = targetMoveY;
                ((Timer) ex.getSource()).stop();
                takenMyCard(cardId);
                boardListBoolean.get(boardSelected % 2).set(boardSelected / 2, false);
                moveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
                moveY = 460 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
                imageLoader.setMoveCard(imageLoader.getCard(boardSelected));
                targetMoveX = takenMyCardPositionX(boardList.get(boardSelected % 2).get(boardSelected / 2));
                targetMoveY = takenMyCardPositionY(boardList.get(boardSelected % 2).get(boardSelected / 2));
                deltaX = (targetMoveX - moveX) / (double)steps;
                deltaY = (targetMoveY - moveY) / (double)steps;
                currentSteps[0] = 0;
                thirdTimer.start();
            }
        });
        
        Timer firstTimer = new Timer(16, ex -> {
            moveX += deltaX;
            moveY += deltaY;
            currentSteps[0]++;
            if (currentSteps[0] == steps) {
                moveX = targetMoveX;
                moveY = targetMoveY;
                ((Timer) ex.getSource()).stop();
                targetMoveX = takenMyCardPositionX(cardId);
                targetMoveY = takenMyCardPositionY(cardId);
                deltaX = (targetMoveX - moveX) / (double)steps;
                deltaY = (targetMoveY - moveY) / (double)steps;
                currentSteps[0] = 0;
                secondTimer.start();
            }
        });
        firstTimer.start();
        isMoveCard = true;
    }

    /* プレイヤーが手札と場のカードを選んだ*/
    public void playerSelected(int playerHandSelected, int boardSelected) {
        darkenScreen = false;
        playerHandListBoolean.set(playerHandSelected, false);
        imageLoader.setMoveCard(imageLoader.getCard(playerHandList.get(playerHandSelected)));
        moveX = 520 + playerHandSelected * 120;
        moveY = 860;
        int[] currentSteps = {0};
        if(boardSelected == -1){
            targetMoveX = 660 + (cardSize / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
            targetMoveY = 460 + 110 * (cardSize % 2 == 0 ? -1 : 1);
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            Timer firstTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    boardList.get(cardSize % 2).set(cardSize / 2, playerHandList.get(playerHandSelected));
                    boardListBoolean.get(cardSize % 2).set(cardSize / 2, true);
                    updateState();
                }
            });
            firstTimer.start();
            isMoveCard = true;
        }
        else{
            targetMoveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
            targetMoveY = 460 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            Timer thirdTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    takenMyCard(boardList.get(boardSelected % 2).get(boardSelected / 2));
                    updateState();
                }
            });
            Timer secondTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    takenMyCard(playerHandList.get(playerHandSelected));
                    playerHandListBoolean.set(playerHandSelected, false);
                    moveX = 660 + (boardSelected / 2 + 1) * (600 / (((cardSize + 1) / 2) + 1));
                    moveY = 460 + 110 * (boardSelected % 2 == 0 ? -1 : 1);
                    imageLoader.setMoveCard(imageLoader.getCard(boardList.get(boardSelected % 2).get(boardSelected / 2)));
                    targetMoveX = takenMyCardPositionX(boardList.get(boardSelected % 2).get(boardSelected / 2));
                    targetMoveY = takenMyCardPositionY(boardList.get(boardSelected % 2).get(boardSelected / 2));
                    deltaX = (targetMoveX - moveX) / (double)steps;
                    deltaY = (targetMoveY - moveY) / (double)steps;
                    currentSteps[0] = 0;
                    thirdTimer.start();
                }
            });
            Timer firstTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    targetMoveX = takenMyCardPositionX(playerHandList.get(playerHandSelected));
                    targetMoveY = takenMyCardPositionY(playerHandList.get(playerHandSelected));
                    deltaX = (targetMoveX - moveX) / (double)steps;
                    deltaY = (targetMoveY - moveY) / (double)steps;
                    currentSteps[0] = 0;
                    secondTimer.start();
                }
            });
            firstTimer.start();
            isMoveCard = true;
        }
    }

    /* ゲームの情報を更新する*/
    public void updateCard() {
        boardList1 = controller.getField();
        cardSize = boardList1.size();
        for(int i=0;i < cardSize;i++){
            boardList.get(i%2).set(i/2, boardList1.get(i));
            cardState[boardList1.get(i)] = 1;
        }
        playerHandList = controller.getPlayer1Hands();
        for(int i=0;i<playerHandList.size();i++){
            cardState[playerHandList.get(i)] = 2;
        }
        opponentHandList = controller.getPlayer2Hands();
        for(int i=0;i<opponentHandList.size();i++){
            cardState[opponentHandList.get(i)] = 3;
        }
    }

    /* 光らせる場のカードを更新する*/
    public void updateFlushField(int id) {
        for(int i = 0;i < boardList1.size(); i++){
            if(boardList.get(i % 2).get(i / 2) / 4 == id / 4 && boardListBoolean.get(i % 2).get(i / 2)){
                flushBoardListBoolean.get(i % 2).set(i / 2, true);
            }
        }
    }

    /* 光らせる手札を更新する*/
    public void updatePlayerHandFlush() {
        if(!isMyTurn) return;
        for(int i=0;i<8;i++){
            flushPlayerHandListBoolean.set(i, false);
        }
        for(int i=0;i<playerHandList.size();i++){
            for(int j=0;j<cardSize;j++){
                if(playerHandList.get(i) / 4 == boardList.get(j % 2).get(j / 2) / 4 && playerHandListBoolean.get(i)) flushPlayerHandListBoolean.set(i, true);
            }
        }
    }

    /* カードを配る*/
    public void dealCards(){
        int[] count = {0};
        imageLoader.setMoveCard(imageLoader.getCardBack());
        /* 自分のターンかどうかを表示させる*/
        Timer turnTimer = new Timer(16, e -> {
            if(!stopTurnMovement)textTurnX += 80;
            if(!stopTurnMovement)moveRectTurnX += 160;
            if(textTurnX >= 860) {
                stopTurnMovement = true;
                Timer stopTimer = new Timer(1000, ev -> {
                    stopTurnMovement = false;
                });
                stopTimer.setRepeats(false);
                stopTimer.start();
            }
            if(textTurnX >= 4000) {
                ((Timer)e.getSource()).stop();
                updatePlayerHandFlush();
                darkenScreen = true;
                isStarted = true;
                if(isMyTurn){
                    isHandSelect = true;
                }
            }
        });
        /* 対戦開始を表示する*/
        Timer startTimer = new Timer(16, e -> {
            if(!stopMovement)textX += 80;
            if(!stopMovement)moveRectX += 160;
            if(textX >= 860) {
                stopMovement = true;
                Timer stopTimer = new Timer(1000, ev -> {
                    stopMovement = false;
                });
                stopTimer.setRepeats(false);
                stopTimer.start();
            }
            if(textX >= 4000) {
                ((Timer)e.getSource()).stop();
                playerId = controller.getPlayerId();
                if(turn % 2 + 1 == playerId) isMyTurn = true;
                else isMyTurn = false;
                if(isMyTurn) isHandSelected = true;
                turnTimer.start();
            }
        });
        /* 相手の手札を配る*/
        Timer opponentHandDealCardsTimer = new Timer(16, e -> {
            targetMoveX = 480 + count[0] * 120;
            targetMoveY = 80;
            moveX = 540 - cardRemain;
            moveY = 460 - cardRemain;
            int[] currentSteps = {0};
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            ((Timer) e.getSource()).stop();
            Timer innerTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    opponentHandListBoolean.set(count[0] - 1, true);
                    if (count[0] == 8) {
                        count[0] = 0;
                        startTimer.start();
                    } else {
                        ((Timer) e.getSource()).start();
                    }
                }
            });
            innerTimer.start();
            isMoveCard = true;
            cardRemain--;
            count[0]++;
        });
        /* プレイヤーの手札を配る*/
        Timer playerHandDealCardsTimer = new Timer(16, e -> {
            targetMoveX = 520 + count[0] * 120;
            targetMoveY = 860;
            moveX = 540 - cardRemain;
            moveY = 460 - cardRemain;
            int[] currentSteps = {0};
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            ((Timer) e.getSource()).stop();
            Timer innerTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    playerHandListBoolean.set(count[0] - 1, true);

                    if (count[0] == 8) {
                        count[0] = 0;
                        opponentHandDealCardsTimer.start();
                    } else {
                        ((Timer) e.getSource()).start();
                    }
                }
            });

            innerTimer.start();
            isMoveCard = true;
            cardRemain--;
            count[0]++;
        });
        /* 場のカードを配る*/
        Timer fieldDealCardsTimer = new Timer(16, e -> {
            targetMoveX = 540 + (count[0] / 2 + 1) * 120;
            targetMoveY = 460 + 110 * (count[0] % 2 == 0 ? -1 : 1);
            moveX = 540 - cardRemain;
            moveY = 460 - cardRemain;
            int[] currentSteps = {0};
            deltaX = (targetMoveX - moveX) / (double)steps;
            deltaY = (targetMoveY - moveY) / (double)steps;
            ((Timer) e.getSource()).stop();
            Timer innerTimer = new Timer(16, ex -> {
                moveX += deltaX;
                moveY += deltaY;
                currentSteps[0]++;
                if (currentSteps[0] == steps) {
                    moveX = targetMoveX;
                    moveY = targetMoveY;
                    ((Timer) ex.getSource()).stop();
                    isMoveCard = false;
                    boardListBoolean.get((count[0] - 1) % 2).set((count[0] - 1) / 2, true);
                    if (count[0] == 8) {
                        count[0] = 0;
                        playerHandDealCardsTimer.start();
                    } else {
                        ((Timer) e.getSource()).start();
                    }
                }
            });
            innerTimer.start();
            isMoveCard = true;
            cardRemain--;
            count[0]++;
        });
        fieldDealCardsTimer.start();
    }

    /* 対戦相手の情報の取得*/
    private void getOpponentInfo() {
        PlayerInfo opponentInfo = controller.getOpponentInfo();
        this.opponentPlayerName = opponentInfo.getPlayerName();
        this.opponentIconNumber = opponentInfo.getIconNum();
        this.opponentExperience = opponentInfo.getLevel();
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

    /* 場の選択*/
    public void fieldSelect(int i) {
        isFieldSelect = false;
        try {
            if(!isCardOpen){
                client.playCard(playerHandList.get(playerHandSelect), playerId, boardList.get(i % 2).get(i / 2));
                client.fetchGameState();
            }else{
                client.playCard(playerHandList.get(playerHandList.size()-1), playerId, boardList.get(i % 2).get(i / 2));
                client.fetchGameState();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        if(!isCardOpen)cardState[playerHandList.get(playerHandSelect)] = 4;
        else cardState[playerHandList.get(playerHandList.size()-1)] = 4;
        int counter = 0;
        for(int j = 0;j < cardSize; j++){
            if(flushBoardListBoolean.get(j % 2).get(j / 2))counter++;
        }
        if(counter==3){
            for(int j = 0;j < cardSize; j++){
                if(flushBoardListBoolean.get(j % 2).get(j / 2))cardState[boardList.get(j % 2).get(j / 2)] = 4;
            }
        }
        else{
            cardState[boardList.get(i % 2).get(i / 2)] = 4;
        }
        if(!isCardOpen)playerSelected(playerHandSelect, i);
        else boardSelect(playerHandList.get(playerHandList.size()-1), i);
    }

    /* 手札の選択*/
    public void handSelect(int i) {
        isHandSelect = false;
        playerHandSelect = i;
        if(flushPlayerHandListBoolean.get(i)) {
            isFieldSelect = true;
            updateFlushField(playerHandList.get(playerHandSelect));
        }
        else{
            try {
                client.playCard(playerHandList.get(playerHandSelect), playerId, -1);
                client.fetchGameState();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            cardState[playerHandList.get(playerHandSelect)] = 4;
            playerSelected(playerHandSelect, -1);
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
        c_OnlineGame.addSlider(slider1, slider2);
    }

    /* 設定を閉じる*/
    public void closeSetting() {
        this.isSettingOpen = false;
        c_OnlineGame.removeSlider(slider1, slider2);
    }
    
    /* ロビーに戻る*/
    public void returnLobby() {
        isReturnLobby = true;
        try {
            controller.getGameClient().disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        c_OnlineGame.moveLobby();
    }

    /* チェックボックスの管理*/
    public void clickCheckBoxBGM() {
        this.checkMarkVisibleBGM = !this.checkMarkVisibleBGM;
        AppPreferences.putBoolean("BGM", this.checkMarkVisibleBGM);
    }

    public void clickCheckBoxSE() {
        this.checkMarkVisibleSE = !this.checkMarkVisibleSE;
        AppPreferences.putBoolean("SE", this.checkMarkVisibleSE);
    }

    public void backSelect() {
        isFieldSelect = false;
        isHandSelect = true;
        for(int i = 0;i < boardList1.size();i++){
            flushBoardListBoolean.get(i % 2).set(i / 2, false);
        }
    }

    public void KOIKOI() {
        try{
            client.sendNextTurnMessage("KOIKOI");
        } catch (Exception ev){
            ev.printStackTrace();
        }
        controller.setIsKoikoi(false);
        isKOIKOI = false;
    }

    public void END() {
        try{
            client.sendNextTurnMessage("END");
        } catch (Exception ev){
            ev.printStackTrace();
        }
        controller.setIsKoikoi(false);
        isKOIKOI = false;
        try {
            //controller.getGameClient().disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        c_OnlineGame.moveLobby();
    }
    
    public String getPlayerName() { return playerName; }
    public String getOpponentPlayerName() { return opponentPlayerName; }
    public int getExperiment() { return experiment; }
    public int getOpponentExperience() { return opponentExperience; }
    public int getCardNumber() { return cardNumber; }
    public int getOpponentIconNumber() { return opponentIconNumber; }
    public int getTextX() { return textX; }
    public int getMoveRectX() { return moveRectX; }
    public int getTextTurnX() { return textTurnX; }
    public int getMoveRectTurnX() { return moveRectTurnX; }
    public int getCardSize() { return cardSize; }
    public int getCurrentCardSize() { return currentCardSize; }
    public int getCardRemain() { return cardRemain; }
    public int getPlayerHandSelect() { return playerHandSelect; }
    public int getSteps() { return steps; }
    public int getTurn() { return turn; }
    public int getCardNumbers() { return cardNumbers; }
    public int getPlayerId() { return playerId; }
    public boolean getIsMoveCard() { return isMoveCard; }
    public boolean getStopMovement() { return stopMovement; }
    public boolean getStopTurnMovement() { return stopTurnMovement; }
    public boolean getIsSettingOpen() { return isSettingOpen; }
    public boolean getCheckMarkVisibleBGM() { return checkMarkVisibleBGM; }
    public boolean getCheckMarkVisibleSE() { return checkMarkVisibleSE; }
    public boolean getIsReturnLobby() { return isReturnLobby; }
    public boolean getIsMyTurn() { return isMyTurn; }
    public boolean getDarkenScreen() { return darkenScreen; }
    public boolean getIsHandSelect() { return isHandSelect; }
    public boolean getIsFieldSelect() { return isFieldSelect; }
    public boolean getIsCardOpen() { return isCardOpen; }
    public boolean getIsStarted() { return isStarted; }
    public boolean getIsBoardSelect() { return isBoardSelect; }
    public boolean getIsHandSelected() { return isHandSelected; }
    public boolean getIsKOIKOI() { return isKOIKOI; }
    public List<Integer> getBoardList1() { return boardList1; }
    public List<List<Integer>> getCurrentBoardList() { return currentBoardList; }
    public List<Integer> getCurrentPlayerHandList() { return currentPlayerHandList; }
    public List<Integer> getCurrentOpponentHandList() { return currentOpponentHandList; }
    public int[] getCardState() { return cardState; }
    public List<List<Integer>> getBoardList() { return boardList; }
    public List<Integer> getPlayerHandList() { return playerHandList; }
    public List<Integer> getOpponentHandList() { return opponentHandList; }
    public List<List<Boolean>> getFlushBoardListBoolean() { return flushBoardListBoolean; }
    public List<Boolean> getFlushPlayerHandListBoolean() { return flushPlayerHandListBoolean; }
    public List<List<Boolean>> getBoardListBoolean() { return boardListBoolean; }
    public List<Boolean> getPlayerHandListBoolean() { return playerHandListBoolean; }
    public List<Boolean> getOpponentHandListBoolean() { return opponentHandListBoolean; }
    public List<List<Boolean>> getCurrentBoardListBoolean() { return currentBoardListBoolean; }
    public List<Boolean> getCurrentPlayerHandListBoolean() { return currentPlayerHandListBoolean; }
    public List<Boolean> getCurrentOpponentHandListBoolean() { return currentOpponentHandListBoolean; }
    public ArrayList<Ball> getBalls() { return balls; }
    public Random getRandom() { return random; }
    public float getAlpha() { return alpha; }
    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
    public double getMoveX() { return moveX; }
    public double getMoveY() { return moveY; }
    public double getTargetMoveX() { return targetMoveX; }
    public double getTargetMoveY() { return targetMoveY; }
    public Rectangle[][] getBoardRectangles() { return boardRectangles; }
    public Rectangle[] getPlayerHandRectangles() { return playerHandRectangles; }
    public Rectangle getSettingButtonBounds() { return settingButtonBounds; }
    public Rectangle getCloseButtonBounds() { return closeButtonBounds; }
    public Rectangle getReturnLobbyButtonBounds() { return returnLobbyButtonBounds; }
    public Rectangle getCheckBoxBGMBounds() { return checkBoxBGMBounds; }
    public Rectangle getCheckBoxSEBounds() { return checkBoxSEBounds; }
    public Rectangle getKOIKOIButtonBounds() { return KOIKOIButtonBounds; }
    public Rectangle getENDButtonBounds() { return ENDButtonBounds; }
    public JSlider getSlider1() { return slider1; }
    public JSlider getSlider2() { return slider2; }
    public ImageLoader getImageLoader() { return imageLoader; }
    public GameController getController() { return controller; }
    public GameClient getClient() { return client; }
    public C_OnlineGame getC_OnlineGame() { return c_OnlineGame; }
    public List<Integer> getGokou() { return gokou; }
    public List<Integer> getTane() { return tane; }
    public List<Integer> getTann() { return tann; }
    public List<Integer> getKasu() { return kasu; }
    public List<Boolean> getIsGokou() { return isGokou; }
    public List<Boolean> getIsTane() { return isTane; }
    public List<Boolean> getIsTann() { return isTann; }
    public List<Boolean> getIsKasu() { return isKasu; }
    public List<Boolean> getOpponentIsGokou() { return opponentIsGokou; }
    public List<Boolean> getOpponentIsTane() { return opponentIsTane; }
    public List<Boolean> getOpponentIsTann() { return opponentIsTann; }
    public List<Boolean> getOpponentIsKasu() { return opponentIsKasu; }
}