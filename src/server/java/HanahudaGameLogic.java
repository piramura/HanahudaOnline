import java.util.ArrayList;
import java.util.List;

public class HanahudaGameLogic {
    private Game game;
    private int currentPlayerIndex;
    private int playCount = 0; // 現在のターンでのプレイ回数

    public void  resetGame() {
        Deck deck = new Deck();
        deck.shuffle();
        game = new Game(deck,this);
        setupPlayers();//プレイヤーに初期の手札を配る処理を実行する。
        setupField();//山札から場に8枚のカードを配置する処理を行う。
        currentPlayerIndex = 0;
        System.out.println("ゲーム開始！ 配札完了！");
    }
    // プレイヤーをセットアップ
    private void setupPlayers() {
        for (int i = 0; i < 2; i++) {
            Player player = new Player();
            game.addPlayer(player);
            for (int j = 0; j < 8; j++) {
                player.addCardToHand(game.getDeck().draw());
            }
        }
    }
    public Game getGame(){
        return game;
    }

    // 場のカードをセットアップ
    private void setupField() {
        while (true) {
            game.getField().clear();
            for (int i = 0; i < 8; i++) {
                game.getField().addCard(game.getDeck().draw());
            }
            //同じ月のカードが4枚以上あるかチェック
            if (!hasFourSameMonthCards(game.getField().getCards())) {
                break; // 問題がなければループを抜ける
            }
            // 問題がある場合はデッキを再シャッフル
            System.out.println("フィールドに同じ月のカードが4枚以上あります。再セットアップを行います。");
            game.getDeck().shuffle();
        }
    }
    private boolean hasFourSameMonthCards(List<Card> cards) {
        int[] monthCounts = new int[12]; // 月ごとのカウント用配列（0～11のインデックス）
    
        for (Card card : cards) {
            int month = (card.getId() - 1) / 4; // IDを4で割り、0～11の月を取得
            monthCounts[month]++;
            if (monthCounts[month] >= 4) {
                return true;
            }
        }
    
        return false; // 問題なし
    }
    /*
    // プレイヤーのアクション（カードを出すなど）を処理し、ゲームの進行を制御。
    // 入力検証、カードのプレイ、ルールの適用を行う。
    public String processPlayerAction(int playerIndex, String action) {
        if (!isCurrentPlayer(playerIndex)) {
            return "エラー: あなたのターンではありません。";
        }
        String[] parts = validateAction(action);
        if (parts == null) {
            return "エラー: 不正なコマンド形式です。";
        }
        String result = "エラー：けっか判定できません。";
        try {
            //インデックスが不正ならばここでエラー出る。
            int cardIndex = Integer.parseInt(parts[1]);//いまは１～４８のすうじだからこいつがどこにあるかさがしてそれが手札なら値を返す。
            result = handleCardPlay(playerIndex, cardIndex);
        } catch (IllegalArgumentException e) {
            return "エラー: " + e.getMessage();
        }
        // ➡️ 役判定を追加
        RoleResult roleResult = RoleChecker.checkRules(game.getPlayers().get(playerIndex));
        System.out.println(roleResult);
        if (action.startsWith("DECLARE_END")) {
            game.getPlayers().get(playerIndex).declareEnd();
            return "プレイヤー " + (playerIndex + 1) + " がゲームを終了しました。";
        }
        
        if (isGameFinished()) {
            return determineWinner();
            //ゲーム終了
        }

        return result + "\n次のターンはプレイヤー " + (currentPlayerIndex + 1) + " です。";
    }
    */
    public String processPlayerAction(int playerIndex, int handCardId, int fieldCardId) {
    
            String playResult = handleCardPlay(playerIndex, handCardId, fieldCardId);
            playCount++;
            if (playCount >= 2) {
                // 2回プレイしたら返信を作成
                // 役判定
                RoleResult roleResult = RoleChecker.checkRules(game.getPlayers().get(playerIndex));
                if (!roleResult.getAchievedRoles().isEmpty()) {
                    System.out.println("役が成立しました: " + roleResult.getAchievedRoles());

                    // 「こいこい」待機状態にする
                    game.setKoiKoi(playerIndex, true); // プレイヤーを「こいこい中」に設定

                    return "KOIKOI_WAITING: 役が成立しました。「こいこい」を選択してください。\n" +
                        "役: " + roleResult.getAchievedRoles() + "\n" +
                        "得点: " + roleResult.getTotalScore();
                }
                // // ゲーム終了判定は別の処理で
                // if (isGameFinished()) {
                //     return "GAME_END: ゲーム終了！勝者: " + determineWinner();
                // }

                // 次のターンに進む
                //nextTurn();
                return "NEXT_TURN: プレイヤー " + ((currentPlayerIndex + 1)%2) + " のターンです。\n" + playResult;
            }else{
                return "CONTINUE_TURN: プレイヤー " + (playerIndex + 1) + " のターンが継続中です。\n" + playResult;
            }
    }
    
    private String askKoiKoi(int playerIndex, RoleResult roleResult) {
        Player player = game.getPlayers().get(playerIndex);
    
        // プレイヤーに「こいこい」を選択させる
        System.out.println("プレイヤー " + (playerIndex + 1) + " は次の役を達成しました: " + roleResult.getAchievedRoles());
        System.out.println("合計得点: " + roleResult.getTotalScore() + "点");
    
        // 「こいこい」するか確認（仮の処理: 本来はユーザー入力を待つ）
        boolean koiKoi = Math.random() < 0.5; // 50%で「こいこい」選択（デモ用）
    
        if (koiKoi) {
            System.out.println("プレイヤー " + (playerIndex + 1) + " は「こいこい」を選択しました！");
            return "プレイヤー " + (playerIndex + 1) + " が「こいこい」を選択しました。次のターンに進みます。";
        } else {
            System.out.println("プレイヤー " + (playerIndex + 1) + " は「こいこい」を選択しませんでした。ゲーム終了です。");
            game.getPlayers().get(playerIndex).declareEnd();
            return determineWinner();
        }
    }
    
    public String handleCardPlay(int playerIndex, int cardId,int fieldcard) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        Player player = game.getPlayers().get(playerIndex);
        // 手札内のインデックスを取得
        int handIndex = findCardIndexInHand(player, cardId);
        System.out.println("DEBUG: カードID " + cardId + " は手札のインデックス " + handIndex + " にあります。");

        // 手札からカードをプレイ
        Card playedCard = player.playCard(handIndex);
        //playするだけで実質的には与えられたfieldcardを使うように変更
        game.getField().processPlayedCard(player, playedCard,fieldcard); // Field に委譲
        Card drawnCard = game.getDeck().draw();
        if (drawnCard != null) {
            game.getField().processDrawnCard(player,drawnCard); // Field に委譲
        }

        nextTurn();
        return "プレイヤー " + (playerIndex + 1) + " がカードをプレイしました。\n";
    }
    public void continueGame(){
        //ゲーム続けるから空の処理
    }
    public String determineWinner() {
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        RoleResult result1 = RoleChecker.checkRules(player1);
        RoleResult result2 = RoleChecker.checkRules(player2);

        System.out.println(result1);
        System.out.println(result2);

        int score1 = result1.getTotalScore();
        int score2 = result2.getTotalScore();
        String result = "ゲーム終了！\n";
        result += "プレイヤー 1 の得点: " + score1 + "\n";
        result += "プレイヤー 2 の得点: " + score2 + "\n";

        if (score1 > score2) {
            result += "プレイヤー 1 の勝利！";
        } else if (score2 > score1) {
            result += "プレイヤー 2 の勝利！";
        } else {
            result += "引き分け！";
        }
        return result;
    }

    private int findCardIndexInHand(Player player, int cardId) {
        ArrayList<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {//工夫ポイント
            if (hand.get(i).getId() == cardId) {
                return i; // 一致した場合、そのインデックスを返す
            }
        }
        throw new IllegalArgumentException("指定されたカードIDが手札にありません: " + cardId);
    }

    private String[] validateAction(String action) {//プレイヤーからの入力を検証し、適切な形式かをチェックする。
        if (!action.startsWith("PLAY_CARD:")) {
            return null;
        }
        String[] parts = action.split(":");
        if (parts.length < 2 || !parts[1].matches("\\d+")) {
            return null;
        }
        return parts;
    }

    private boolean isCurrentPlayer(int playerIndex) {
        return playerIndex == (currentPlayerIndex % 2);
    }
    //これらはパブリックで管理される。
    public void nextTurn() {
        if(!isGameFinished()){
            playCount = 0; // プレイ回数をリセット
            currentPlayerIndex = currentPlayerIndex + 1;
            game.setNowTurn(currentPlayerIndex);
        }else{
            endGame();
        }
        
    }
    public void endGame(){
        //ゲーム終了処理
        //リザルトを返してゲームリセット
    }

    public boolean isGameFinished() {
        //こいこい判定する必要性ある？こいこいするときは全部ゲームセッションマネージャーがやってくれる。
        if(game.isBothHandEmpty()){
            return true;
        }else{
            return false;
        }
    }
    
}
