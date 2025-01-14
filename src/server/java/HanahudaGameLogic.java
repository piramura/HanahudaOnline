import java.util.ArrayList;
import java.util.List;
public class HanahudaGameLogic {
    private Game game;
    private int currentPlayerIndex;

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
        for (int i = 0; i < 8; i++) {
            game.getField().addCard(game.getDeck().draw());
        }
    }

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
    

    

    public String handleCardPlay(int playerIndex, int cardId) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        Player player = game.getPlayers().get(playerIndex);
        // 手札内のインデックスを取得
        int handIndex = findCardIndexInHand(player, cardId);
        System.out.println("DEBUG: カードID " + cardId + " は手札のインデックス " + handIndex + " にあります。");

        // 手札からカードをプレイ
        Card playedCard = player.playCard(handIndex);

        game.getField().processPlayedCard(player, playedCard); // Field に委譲
        Card drawnCard = game.getDeck().draw();
        if (drawnCard != null) {
            game.getField().processDrawnCard(player, drawnCard); // Field に委譲
        }

        nextTurn();
        return "プレイヤー " + (playerIndex + 1) + " がカードをプレイしました。\n";
    }
    private String determineWinner() {
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

    private void nextTurn() {
        currentPlayerIndex = currentPlayerIndex + 1;
        game.setNowTurn(currentPlayerIndex);
    }

    public boolean isGameFinished() {
        boolean bothHandsEmpty = game.getPlayers().stream().allMatch(player -> player.getHand().isEmpty());
        boolean anyPlayerDeclaredEnd = game.getPlayers().stream().anyMatch(Player::hasDeclaredEnd);
        // 両方のプレイヤーの手札が空、またはどちらかが役を成立させ「こいこい」をしない場合
        return bothHandsEmpty || anyPlayerDeclaredEnd;
    }
    
    public void playComputerTurn() {
        Player computer = game.getPlayers().get(currentPlayerIndex);
        ArrayList<Card> hand = computer.getHand();
        Field field = game.getField();

        Card bestCard = null;
        int maxScore = 0;

        for (Card card : hand) {
            int potentialScore = calculatePotentialScore(card, field);
            if (potentialScore > maxScore) {
                maxScore = potentialScore;
                bestCard = card;
            }
        }

        if (bestCard != null) {
            System.out.println("コンピュータがカードをプレイしました: " + bestCard);
            handleCardPlay(currentPlayerIndex, bestCard.getId());
        } else {
            System.out.println("コンピュータがランダムなカードをプレイしました。");
            handleCardPlay(currentPlayerIndex, hand.get(0).getId());
        }
    }

    private int calculatePotentialScore(Card card, Field field) {
        int score = 0;
        for (Card fieldCard : field.getCards()) {
            if (fieldCard.getMonth() == card.getMonth()) {
                score += fieldCard.getRole().stream().mapToInt(Card.Role::getScore).sum();
            }
        }
        return score;
    }
}
