package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BuyChoiceAction;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class JailTest {

    private Game game;

    @Before
    public void setup() {
        game = new GameSetup()
            .addPlayer("One")
            .addPlayer("Two")
            .withSpeedDie(false)
            .create();
    }

    @Test
    public void gotoJailSquare() {
        Player player = game.getCurrentPlayer();
        player.setPosition(26);
        assertEquals(MonopolyTiles.GO_TO_JAIL, game.getProperty(30));
        TestUtils.perform(game, new RollDiceAction(3, 1));
        assertTrue(player.isInJail());
        assertEquals(MonopolyTiles.JAIL, player.getPositionProperty());
        assertEquals(game.getPlayer(1), game.getCurrentPlayer());
    }

    @Test
    public void gotoJailSquareDoubles() {
        Player player = game.getCurrentPlayer();
        player.setPosition(26);
        assertEquals(MonopolyTiles.GO_TO_JAIL, game.getProperty(30));
        TestUtils.perform(game, new RollDiceAction(2, 2));
        assertTrue(player.isInJail());
        assertEquals(MonopolyTiles.JAIL, player.getPositionProperty());
        assertEquals(game.getPlayer(1), game.getCurrentPlayer());
    }

    @Test
    public void gotoJailThrowingDoublesThreeTimes() {
        Player player = game.getCurrentPlayer();
        TestUtils.perform(game, new RollDiceAction(3, 3));
        TestUtils.perform(game, BuyChoiceAction.BUY);
        assertEquals(player, game.getCurrentPlayer());

        TestUtils.perform(game, new RollDiceAction(4, 4));
        TestUtils.perform(game, BuyChoiceAction.BUY);
        assertEquals(player, game.getCurrentPlayer());

        TestUtils.perform(game, new RollDiceAction(1, 1));
        assertTrue(player.isInJail());
        assertEquals(MonopolyTiles.JAIL, player.getPositionProperty());
        assertEquals(game.getPlayer(1), game.getCurrentPlayer());
        assertFalse(game.getCurrentPlayer().isAllowed(BuyChoiceAction.BUY));
    }

    @Test
    public void visitJail() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void payToLeaveJail() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void escapeJailByThrowingDoubles() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void stuckInJailByNotThrowingDoubles() {
        throw new UnsupportedOperationException();
    }

}
