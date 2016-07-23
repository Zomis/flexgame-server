package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BidAction;
import net.zomis.monopoly.model.actions.BuyChoiceAction;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;
import static net.zomis.monopoly.model.TestUtils.perform;

public class PlayTest {

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
    public void buyProperty() {
        int money = game.getCurrentPlayer().getMoney();
        perform(game, new RollDiceAction(2, 1));
        assertEquals(game.getPlayer(0), game.getCurrentPlayer());
        TestUtils.expectedTaskType(game, GameTask.GameTaskType.BUY_OR_NOT);
        perform(game, BuyChoiceAction.BUY);
        int cost = game.getProperty(3).getCost();
        assertEquals(TestUtils.getPreviousPlayer(game), game.getProperty(3).getOwner().get());
        assertEquals(money - cost, TestUtils.getPreviousPlayer(game).getMoney());
    }

    @Test
    public void cannotRollWhenNeedToMakeDecision() {
        perform(game, new RollDiceAction(2, 1));
        assertEquals(GameTask.GameTaskType.BUY_OR_NOT, game.getState().getType());
        assertFalse(game.getCurrentPlayer().isAllowed(RollDiceAction.roll(new Random())));
    }

    @Test
    public void auctionProperty() {
        int money = game.getCurrentPlayer().getMoney();
        perform(game, new RollDiceAction(2, 1));
        perform(game, BuyChoiceAction.NOT_BUY);
        game.getPlayer(0).perform(new BidAction(10));
        game.getPlayer(1).perform(new BidAction(20));
        game.getPlayer(0).perform(new BidAction(30));
        assertFalse(game.getPlayer(0).isAllowed(new BidAction(40))); // Not allowed to bid over yourself
        assertFalse(game.getPlayer(1).isAllowed(new BidAction(29))); // Not allowed to bid less than current bid
    }

    @Test
    public void payRent() {
        perform(game, new RollDiceAction(2, 1));
        perform(game, BuyChoiceAction.BUY);

        int money = game.getCurrentPlayer().getMoney();
        int rent = game.getProperty(3).getCurrentRent();
        perform(game, new RollDiceAction(2, 1));
        assertEquals(money - rent, TestUtils.getPreviousPlayer(game).getMoney());
    }

    @Test
    public void throwAgain() {
        Player player = game.getCurrentPlayer();
        perform(game, new RollDiceAction(3, 3));
        perform(game, BuyChoiceAction.BUY);
        assertEquals(player, game.getCurrentPlayer());
        assertTrue(player.isAllowed(RollDiceAction.roll(new Random())));
    }

    @Test
    public void visitOwnProperty() {
        Player owner = game.getCurrentPlayer();
        int money = owner.getMoney();
        game.getProperty(3).setOwner(owner);
        perform(game, new RollDiceAction(2, 1));
        assertEquals(owner, game.getProperty(3).getOwner().get());
        assertEquals(game.getPlayer(1), game.getCurrentPlayer());
        assertEquals(money, owner.getMoney());
    }

}
