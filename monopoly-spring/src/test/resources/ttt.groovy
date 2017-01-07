import net.zomis.spring.games.ttt.TTTGame;
import net.zomis.spring.games.ttt.TTPlayer;

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
        action('move', XYMove.class) { TTTGame game, TTPlayer who ->
            game.move(who, action.x, action.y)
        }
    }

}
