package net.zomis.monopoly.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestUtils {

    public static GameActionResult perform(Game game, GameAction action) {
        Player player = game.getCurrentPlayer();
        assertTrue("Expected " + action + " to be allowed by " + player + " but it wasn't", player.isAllowed(action));
        return player.perform(action);
        // game.pollStack();
    }

    public static Player getPreviousPlayer(Game game) {
        int index = game.getCurrentPlayer().getIndex();
        index--;
        if (index < 0) {
            index += game.getPlayers().size();
        }

        return game.getPlayer(index);
    }

    public static void expectedTaskType(Game game, GameTask.GameTaskType type) {
        assertNotNull(game.getState());
        assertEquals(type, game.getState().getType());
    }

}
