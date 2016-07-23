package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BuyChoiceAction;
import net.zomis.monopoly.model.actions.EscapeChoiceAction;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

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
        game.getPlayer(1).gotoJail();
        assertEquals(game.getPlayer(0), game.getCurrentPlayer());
        TestUtils.perform(game, new RollDiceAction(5, 5));
        assertEquals(game.getPlayer(0).getPositionProperty(), game.getPlayer(1).getPositionProperty());
        assertFalse(game.getPlayer(0).isInJail());
        assertTrue(game.getPlayer(1).isInJail());
    }

    @Test
    public void payToLeaveJail() {
        prepareJail();
        Player player = game.getCurrentPlayer();
        TestUtils.perform(game, EscapeChoiceAction.pay(new RollDiceAction(2, 3)));
        assertFalse(player.isInJail());
        assertEquals(15, player.getPosition());
    }

    @Test
    public void escapeJailByThrowingDoubles() {
        prepareJail();
        Player player = game.getCurrentPlayer();
        TestUtils.perform(game, EscapeChoiceAction.roll(new RollDiceAction(2, 2)));
        assertFalse(player.isInJail());
    }

    private Player prepareJail() {
        game.getPlayer(1).gotoJail();
        game.nextPlayer();
        return game.getCurrentPlayer();
    }

    @Test
    public void cannotChooseEscapePlanIfNotInJail() {
        assertFalse(game.getCurrentPlayer().isInJail());
        assertFalse(game.getCurrentPlayer().isAllowed(EscapeChoiceAction.roll(new RollDiceAction(4, 2))));
    }

    @Test
    public void stuckInJailByNotThrowingDoubles() {
        Player player = prepareJail();
        TestUtils.perform(game, EscapeChoiceAction.roll(new RollDiceAction(4, 2)));
        assertTrue(player.isInJail());
        assertEquals(player, game.getCurrentPlayer());
    }

    @Test
    public void needToDecideOnEscapePlanBeforeThrowing() {
        Player player = prepareJail();
        assertFalse(player.isAllowed(RollDiceAction.roll(new Random())));
    }

}
