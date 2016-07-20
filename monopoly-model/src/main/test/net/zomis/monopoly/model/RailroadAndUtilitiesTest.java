package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BuyChoiceAction;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.junit.Before;
import org.junit.Test;

import static net.zomis.monopoly.model.TestUtils.perform;

public class RailroadAndUtilitiesTest {

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
    public void buyRailroad() {
        perform(game, new RollDiceAction(3, 2));
        perform(game, BuyChoiceAction.BUY);
        throw new UnsupportedOperationException("Need to assert something here");
    }

    @Test
    public void payRentRailroad1() {
        game.getProperty(5).setOwner(game.getPlayers().get(1));
        int money = game.getCurrentPlayer().getMoney();
        throw new UnsupportedOperationException();
    }

    @Test
    public void payRentRailroad2() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void payRentRailroad3() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void payRentRailroad4() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void buyUtilities() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void payRentUtilities1() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void payRentUtilities2() {
        throw new UnsupportedOperationException();
    }

}
