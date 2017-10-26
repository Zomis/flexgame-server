import net.zomis.spring.games.impls.TTTGame;
import net.zomis.spring.games.impls.TTPlayer;
import net.zomis.spring.games.generic.Action;

class XYMove {
    int x;
    int y;
}
game("ttt", TTTGame.class) {
    setup(null) {
        return new TTTGame();
    }
    players 2
    playerType TTPlayer.class
    config null

    actions {
        action('move', XYMove.class) {Action action ->
            action.game.move(action.game.getPlayerByIndex(action.playerIndex), action.actionData.x, action.actionData.y)
        }
    }

    gameInfo {TTTGame game ->
        return {
            turn game.getTurn()
            board game.getBoard()
        }
    }

}
