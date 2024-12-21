import java.util.*;

public class GameManager {
    private Game game;
    private int currentPlayerIndex = 0;

    public GameManager() {
        game = new Game();
    }

    public void resetGame() {
        game = new Game();  // 新しいゲームインスタンス
        currentPlayerIndex = 0;  // ターン情報を初期化
        System.out.println("ゲームの内部状態がリセットされました。");
    }
    public void startGame() {
        resetGame();  // 内部状態のリセットを一括管理
        game.getDeck().shuffle();  // 山札をシャッフル

        // プレイヤー作成
        for (int i = 0; i < 2; i++) {
            game.addPlayer(new Player());
        }

        // 配札処理
        for (Player player : game.getPlayers()) {
            for (int j = 0; j < 8; j++) {
                player.addCardToHand(game.getDeck().draw());
            }
        }

        // 場札の配置
        for (int i = 0; i < 8; i++) {
            game.getField().addCard(game.getDeck().draw());
        }

        currentPlayerIndex = 0;  // 最初のプレイヤーを設定
        System.out.println("ゲーム開始！ 配札完了！");
    }
    

    public Game getGame(){
        return game;
    }

    // 現在のプレイヤーインデックスを返す
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    public boolean isGameFinished() {
        // スコアでの終了判定
        for (Player player : game.getPlayers()) {
            if (player.chacknumberRules() > 0) {
                return true;  // スコア成立でゲーム終了
            }
        }
        // 全ターン終了の判定
        return game.getDeck().getCards().isEmpty() && currentPlayerIndex >= game.getPlayers().size();//山札なくなってもゲームは終了する
    }
    public String checkRules(int playerIndex) {
        Player player = game.getPlayers().get(playerIndex);
        game.checkRules(player);  // 得点は Game クラス内で処理
        int score = player.chacknumberRules();  // プレイヤーの得点を取得
        if (score > 0) {
            return "プレイヤー " + (playerIndex + 1) + " の得点: " + score + "点 獲得！ 総得点: " + score;
        }
        return "役は成立しませんでした。";
    }
    

    // 現在のゲーム状態を返す
    public String getGameState() {
        StringBuilder sb = new StringBuilder();
        sb.append("場のカード: ").append(game.getField().toString()).append("\n\n");
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);
            sb.append("プレイヤー ").append(i + 1)
              .append("の取り札: ").append(player.getCaptures()).append("\n\n");
        }
        return sb.toString();
    }
    public String getWinnerInfo() {
    int highestScore = 0;
    int winnerIndex = -1;
    for (int i = 0; i < game.getPlayers().size(); i++) {
        int score = game.getPlayers().get(i).chacknumberRules();
        if (score > highestScore) {
            highestScore = score;
            winnerIndex = i;
        }
    }
    if (winnerIndex != -1) {
        return "プレイヤー " + (winnerIndex + 1) + " が勝者です！";
    }
    return "勝者はいません。";
}

    // プレイヤーのアクション処理
    public String processPlayerAction(int playerIndex, String action) {
        if (!isCurrentPlayer(playerIndex)) {
            return "エラー: あなたのターンではありません。";
        }
        // メッセージ形式のバリデーション
        if (!action.startsWith("PLAY_CARD:")) {
            return "エラー: 不正なコマンド形式です。";
        }

        String[] parts = action.split(":");
        if (parts.length < 2) {
            return "エラー: カードインデックスが指定されていません。";
        }

        try {
            int cardIndex = Integer.parseInt(action.split(":")[1]);
            if (cardIndex < 0 || cardIndex >= game.getPlayers().get(playerIndex).getHand().size()) {
                return "エラー: 存在しないカードインデックスです。";
            }

            // ターンの進行
            String playResult = playTurn(playerIndex, cardIndex);

            // 役判定をここで行う
            checkRules(playerIndex);

            // ゲーム終了判定
            if (isGameFinished()) {
                return playResult + "\nゲーム終了！ 勝者: " + getWinnerInfo();
            }

            // 次のプレイヤーのターン通知
            return playResult + "\n次のターンはプレイヤー " + (currentPlayerIndex + 1) + " です。";
        } catch (NumberFormatException e) {
            return "エラー: 無効なカードインデックスです。";
        }
    }

    // ターンの進行処理
    private String playTurn(int playerIndex, int cardIndex) {
        Player player = game.getPlayers().get(playerIndex);
        Card playedCard = player.playCard(cardIndex);
        game.playTurn(player, playedCard);
        // ゲーム状態を送信
    MultiThreadServer.serverManager.broadcastGameState();
        nextTurn();
        return "プレイヤー " + (playerIndex + 1) + " がカードをプレイしました。\n" + getGameState();
    }

    // 現在のターンのプレイヤーを確認
    private boolean isCurrentPlayer(int playerIndex) {
        return playerIndex == currentPlayerIndex;
    }

    // 次のターンへ進む
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        // サーバーに現在の状態を送信
    MultiThreadServer.serverManager.broadcastGameState();
    }

    public String getPlayerGameState(int playerIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append("場のカード: ").append(game.getField().toString()).append("\n\n");

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);

            if (i == playerIndex) {
                sb.append("あなたの手札: ").append(player.getHand()).append("\n");
            } else {
                sb.append("プレイヤー ").append(i + 1).append(" の手札: ???\n");
            }

            sb.append("取り札: ").append(player.getCaptures()).append("\n\n");
        }
        return sb.toString();
    }
}
