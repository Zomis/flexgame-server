package net.zomis.monopoly.model;

import static org.junit.Assert.assertTrue;

public class TestUtils {

    public static GameActionResult perform(Game game, GameAction action) {
        Player player = game.getCurrentPlayer();
        assertTrue(player.isAllowed(action));
        return player.perform(action);
        // game.pollStack();
    }

    public static Player getPreviousPlayer(Game game) {
        return null;
    }

}
