public class HanahudaGameLogicTest {
    public static void main(String[] args) {
        HanahudaGameLogicTest test = new HanahudaGameLogicTest();
        
        HanahudaGameLogic gameLogic = new HanahudaGameLogic();

        // ゲームのリセット
        gameLogic.resetGame();
        Game game = gameLogic.getGame();

        System.out.println("=== ゲーム初期化テスト開始 ===");

        // 山札のカード数を確認
        if (game.getDeck().getCards().size() == 24) {
            System.out.println("山札のカード数が正しい: 24枚");
        } else {
            System.out.println("エラー: 山札のカード数が異常: " + game.getDeck().getCards().size() + "枚");
        }

        // プレイヤー数を確認
        if (game.getPlayers().size() == 2) {
            System.out.println("プレイヤー数が正しい: 2人");
        } else {
            System.out.println("エラー: プレイヤー数が異常: " + game.getPlayers().size() + "人");
        }

        // 各プレイヤーの手札数を確認
        boolean handSizeCorrect = true;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);
            if (player.getHand().size() != 8) {
                System.out.println("エラー: プレイヤー " + (i + 1) + " の手札数が異常: " + player.getHand().size() + "枚");
                handSizeCorrect = false;
            }
        }
        if (handSizeCorrect) {
            System.out.println("全プレイヤーの手札数が正しい: 8枚");
        }

        // 場のカード数を確認
        if (game.getField().getCards().size() == 8) {
            System.out.println("場のカード数が正しい: 8枚");
        } else {
            System.out.println("エラー: 場のカード数が異常: " + game.getField().getCards().size() + "枚");
        }

        // 初期ターンのインデックスを確認
        if (game.getNowTurn()== 0) {
            System.out.println("初期ターンのインデックスが正しい: 0");
        } else {
            System.out.println("エラー: 初期ターンのインデックスが異常: " + game.getNowTurn());
        }

        System.out.println("=== ゲーム初期化テスト終了 ===");
        test.testPlayerAction();
        test.testRoleScoring();
        test.testGameEndCondition();
        test.testInvalidCommands();
    }
     // プレイヤーアクションのテスト
    public void testPlayerAction() {
        HanahudaGameLogic logic = new HanahudaGameLogic();
        logic.resetGame();

        System.out.println("=== プレイヤーアクションのテスト ===");
        String initialState = logic.getGameState();
        System.out.println("初期状態: \n" + initialState);

        String actionResult = logic.processPlayerAction(0, "PLAY_CARD:0"); // プレイヤー1が手札の最初のカードをプレイ
        System.out.println("アクション結果: \n" + actionResult);

        String newState = logic.getGameState();
        System.out.println("アクション後の状態: \n" + newState);
    }

    // 役の判定のテスト
    public void testRoleScoring() {
        HanahudaGameLogic logic = new HanahudaGameLogic();
        logic.resetGame();

        System.out.println("=== 役の判定のテスト ===");
        // 特定の取り札をセットして役判定
        Player player = logic.getGame().getPlayers().get(0);
        player.captureCard(new Card(1, Card.Month.JANUARY, Card.Point.HIKARI)); // 仮に役を持つカードを追加
        player.captureCard(new Card(2, Card.Month.JANUARY, Card.Point.HIKARI)); // 役を作るためのカード

        String scoringResult = RoleChecker.checkRules(player).toString(); // プレイヤー1の役を判定
        System.out.println("役判定結果: \n" + scoringResult);
    }

    // ゲーム終了条件のテスト
    public void testGameEndCondition() {
        HanahudaGameLogic logic = new HanahudaGameLogic();
        logic.resetGame();

        System.out.println("=== ゲーム終了条件のテスト ===");
        // 山札を空にする
        while (!logic.getGame().getDeck().getCards().isEmpty()) {
            logic.getGame().getDeck().draw();
        }

        boolean isFinished = logic.isGameFinished();
        System.out.println("ゲーム終了状態: " + (isFinished ? "終了" : "続行"));
    }

    // 異常系のテスト
    public void testInvalidCommands() {
        HanahudaGameLogic logic = new HanahudaGameLogic();
        logic.resetGame();

        System.out.println("=== 異常系のテスト ===");
        // 無効なコマンド
        String invalidCommandResult = logic.processPlayerAction(0, "INVALID_COMMAND");
        System.out.println("無効なコマンドの結果: \n" + invalidCommandResult);

        // 無効なカードインデックス
        String invalidCardIndexResult = logic.processPlayerAction(0, "PLAY_CARD:100");
        System.out.println("無効なカードインデックスの結果: \n" + invalidCardIndexResult);

        // 他プレイヤーがターン外でアクション
        String outOfTurnResult = logic.processPlayerAction(1, "PLAY_CARD:0");
        System.out.println("ターン外アクションの結果: \n" + outOfTurnResult);
    }
}
