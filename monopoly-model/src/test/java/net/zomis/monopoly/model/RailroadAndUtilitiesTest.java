package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BuyChoiceAction;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
        assertEquals(game.getPlayer(0), game.getProperty(5).getOwner().get());
    }

    @Test
    public void payRentRailroad1() {
        int money = game.getCurrentPlayer().getMoney();
        game.getProperty(5).setOwner(game.getPlayers().get(1));
        TestUtils.perform(game, new RollDiceAction(3, 2));
        assertEquals(money - 50, game.getPlayer(0).getMoney());
    }

    @Test
    public void payRentRailroad2() {
        int money = game.getCurrentPlayer().getMoney();
        game.getProperty(5).setOwner(game.getPlayers().get(1));
        game.getProperty(15).setOwner(game.getPlayers().get(1));
        TestUtils.perform(game, new RollDiceAction(3, 2));
        assertEquals(money - 100, game.getPlayer(0).getMoney());
    }

    @Test
    public void payRentRailroad3() {
        int money = game.getCurrentPlayer().getMoney();
        game.getProperty(5).setOwner(game.getPlayers().get(1));
        game.getProperty(15).setOwner(game.getPlayers().get(1));
        game.getProperty(25).setOwner(game.getPlayers().get(1));
        TestUtils.perform(game, new RollDiceAction(3, 2));
        assertEquals(money - 150, game.getPlayer(0).getMoney());
    }

    @Test
    public void payRentRailroad4() {
        int money = game.getCurrentPlayer().getMoney();
        game.getProperty(5).setOwner(game.getPlayers().get(1));
        game.getProperty(15).setOwner(game.getPlayers().get(1));
        game.getProperty(25).setOwner(game.getPlayers().get(1));
        game.getProperty(35).setOwner(game.getPlayers().get(1));
        TestUtils.perform(game, new RollDiceAction(3, 2));
        assertEquals(money - 200, game.getPlayer(0).getMoney());
    }

    @Test
    public void buyUtilities() {
        TestUtils.perform(game, new RollDiceAction(6, 6));
        TestUtils.perform(game, BuyChoiceAction.BUY);
        assertEquals(game.getPlayer(0), game.getProperty(12).getOwner().get());

        game.getPlayer(0).setPosition(25);
        TestUtils.perform(game, new RollDiceAction(2, 1));
        TestUtils.perform(game, BuyChoiceAction.BUY);
        assertEquals(game.getPlayer(0), game.getProperty(28).getOwner().get());
    }

    @Test
    public void payRentUtilities1() {
        Player player = game.getCurrentPlayer();
        int money = game.getCurrentPlayer().getMoney();
        Player opponent = game.getPlayer(1);
        game.getProperty(12).setOwner(opponent);

        TestUtils.perform(game, new RollDiceAction(6, 6));
        assertEquals(money - 4 * 12, player.getMoney());
    }

    @Test
    public void payRentUtilities2() {
        Player player = game.getCurrentPlayer();
        int money = game.getCurrentPlayer().getMoney();
        Player opponent = game.getPlayer(1);
        game.getProperty(12).setOwner(opponent);
        game.getProperty(28).setOwner(opponent);

        TestUtils.perform(game, new RollDiceAction(6, 6));
        assertEquals(money - 10 * 12, player.getMoney());
    }

}
