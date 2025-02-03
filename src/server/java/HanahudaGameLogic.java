import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;
public class HanahudaGameLogic {
    // **スレッドプールを作成**
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Game game;
    private int playCount = 0; // 現在のターンでのプレイ回数
    private Random random = new Random(); // **ここで1つだけ作成**
    private boolean botMode;
    
    public Game getGame(){
        return game;
    }

    public void resetGame() {
        Deck deck = new Deck();
        this.game = new Game(deck);
        game.initializeGame(this);
        game.setCurrentPlayerIndex(1);
        System.out.println("ゲーム開始！ 配札完了！");
    }
    public void setBotMode(boolean botMode){
        this.botMode = botMode;
    }
    public boolean getBouMode(){
        return botMode;
    }

    //PLAY_CARDで呼ばれる関数。
    public String processPlayerAction(int playerIndex, int handCardId, int fieldCardId) {

        if (playerIndex != game.getCurrentPlayerIndex()) {
            return "ERROR: 現在のプレイヤーではありません。";
        }
        String playResult = handleCardPlay(playerIndex, handCardId, fieldCardId);//内部処理は委託する。
        game.incrementPlayCount();

        if (game.getPlayCount() >= 2) {
            return checkAndHandleKoiKoi(playerIndex);
        }
        return "CONTINUE_TURN: プレイヤー " + playerIndex + " のターンが継続中です。\n" + playResult;
    }

    // こいこい判定用のメソッド
    private String checkAndHandleKoiKoi(int playerIndex) {
        Player currentPlayer = game.getPlayers().get(playerIndex == 2 ? 1 : 0);
        RoleResult previousRoleResult = currentPlayer.getRoleResult();
        RoleResult newRoleResult = RoleChecker.checkRules(currentPlayer);
        currentPlayer.setRoleResult(newRoleResult);

        if (!newRoleResult.getAchievedRoles().isEmpty() &&
            !previousRoleResult.getAchievedRoles().containsAll(newRoleResult.getAchievedRoles())) {
            
            currentPlayer.setKoiKoi(true);
            return "KOIKOI_WAITING: 役が成立しました。「こいこい」を選択してください。\n" +
                "役: " + newRoleResult.getAchievedRoles() + "\n" +
                "得点: " + newRoleResult.getTotalScore();
        }

        currentPlayer.setKoiKoi(false);
        return game.getIsEnd() ? "GAME_END: 相手の勝利。" : "NEXT_TURN: ターン " + game.getCurrentTurn();
    }

    //processPlayerActionの補助関数
    public String handleCardPlay(int playerIndex, int cardId,int fieldcard) {//プレイヤーの指定したカードを処理し、ゲームの進行を行う。
        Player player = game.getPlayers().get(playerIndex-1);//ここでー１をして0と1に直す
        int handIndex = findCardIndexInHand(player, cardId);
        Card playedCard = player.playCard(handIndex);
        game.getField().playCardOnField(player, playedCard, fieldcard);
        if (game.getPlayCount() == 0) {
            Card drawnCard = game.getDeck().draw();
            if (drawnCard != null) {
                player.addCardToHand(drawnCard);
                return "山札からカードを引きました: " + drawnCard.toString();
            }
            return "山札が空です。";//起こらない
        }
    
        return game.getPlayCount() == 1 ? "相手のターンへ" : "エラー: 3回目のプレイは無効";
    }

    private int findCardIndexInHand(Player player, int cardId) {
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getId() == cardId) {
                return i;
            }
        }
        throw new IllegalArgumentException("指定されたカードIDが手札にありません: " + cardId);
    }

    public String determineWinner() {
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
    
        // **Player クラスに保存された最新の RoleResult を使う**
        RoleResult result1 = player1.getRoleResult();
        RoleResult result2 = player2.getRoleResult();
    
        System.out.println(result1);
        System.out.println(result2);
    
        int score1 = result1.getTotalScore();
        int score2 = result2.getTotalScore();
        String result = "ゲーム終了！\n";
        
        if(!player1.isKoiKoi()){
            result += "プレイヤー 1 の得点: " + score1 + result1+"\n";
        }

        if(!plater2.isKoiKoi()){
            result += "プレイヤー 2 の得点: " + score2 + result2+"\n";
        }

        if (score1 > score2) {
            result += "プレイヤー 1 の勝利！";
        } else if (score2 > score1) {
            result += "プレイヤー 2 の勝利！";
        } else {
            result += "引き分け！";
        }
    
        return result;
    }

    //これらはパブリックで管理される。
    public void nextTurn() {
        game.nextTurn();
        System.out.println("game.getIsEnd()=="+game.getIsEnd());
        if (game.getIsEnd()) {
            return;
        }
        
        int currentPlayerId = game.getCurrentPlayerIndex();
        if (botMode && isBotTurn(currentPlayerId)) {
            playBotTurn(currentPlayerId);
        }
        
    }
    // **BOTのターンかどうかを判定**
    private boolean isBotTurn(int playerId) {
        return playerId == 2;//2はコンピュータのターン
    }
    private void playBotTurn(int botPlayerId) {
        System.out.println("BOTのターンです。");
        Player player = game.getPlayers().get(botPlayerId - 1);
    
        // 1回目のプレイ
        scheduler.schedule(() -> botPlayCard(player), 500, TimeUnit.MILLISECONDS);
        
        // 2回目のプレイ（場の更新を待つ）
        scheduler.schedule(() -> {
            int handCardId = player.getHand().get(player.getHand().size()-1).getId();
            int fieldCardId = findMatchingCardInField(game.getField(), handCardId);
            System.out.println(handleCardPlay(2, handCardId, fieldCardId));
            scheduler.schedule(() -> nextTurn(), 500, TimeUnit.MILLISECONDS);
        }, 5000, TimeUnit.MILLISECONDS);
    }
    
    // BOTが1枚プレイする共通処理
    private void botPlayCard(Player player) {
        System.out.println("DEBUG: ぼっとがプレイ中");
        if (!player.getHand().isEmpty()) {
            int handCardId = player.getHand().get(0).getId();
            System.out.println("DEBUG: ぼっとがプレイ中handCardId"+handCardId);
            int fieldCardId = findMatchingCardInField(game.getField(), handCardId);
            System.out.println("DEBUG: ぼっとがプレイ中fieldCardId"+fieldCardId);
            System.out.println("DEBUG: ぼっとがプレイ中player.getPlayerID()"+player.getPlayerID());
            System.out.println(handleCardPlay(2, handCardId, fieldCardId));
        } else {
            System.out.println("BOTの手札が空でプレイをスキップします。");
        }
    }

    public int chooseBestCardIndex(List<Card> playerHand, List<Card> playerTaken, List<Card> tableCards) {
        return playerHand.get(0).getId();
    }
    
    // **場にあるカードと一致するカードを探す**
    private int findMatchingCardInField(Field field, int cardId) {
        List<Card> fieldCards = field.getCards();
        for (Card card : fieldCards) {
            if (card.getMonth().ordinal()  == cardId / 4 ) {
                System.out.println("card.getMonth().ordinal()"+card.getMonth().ordinal() +"cardId/4"+ (cardId / 4) );
                return card.getId();
            }
        }
        return -1; // 場に一致するカードがない場合は -1（そのまま場に出す）
    }

    public void endGame(){
        //ゲーム終了処理
        game.setIsEnd(true);
        System.out.println("GAME_END: endGameだけど");
        //リザルトを返してゲームリセット
    }
    
    public boolean isGameFinished() {
        //System.out.println("game.isBothHandEmpty()="+game.isBothHandEmpty()+" game.getIsEnd()="+game.getIsEnd());
        return game.getIsEnd();
    }
    
    
}
