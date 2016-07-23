package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BuildAction;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static net.zomis.monopoly.model.TestUtils.perform;

public class HouseHotelTest {

    private Game game;

    @Before
    public void setup() {
        game = new GameSetup()
            .addPlayer("One")
            .addPlayer("Two")
            .withSpeedDie(true)
            .create();
    }

    @Test
    public void buyHotel() {
        game.getProperty(1).setOwner(game.getCurrentPlayer());
        game.getProperty(3).setOwner(game.getCurrentPlayer());
        perform(game, new BuildAction(10, game.getProperty(1), game.getProperty(3)));
        assertEquals(5, game.getProperty(1).getHouseCount());
        assertEquals(5, game.getProperty(3).getHouseCount());
    }

    @Test
    public void buySomeHouses() {
        game.getProperty(1).setOwner(game.getCurrentPlayer());
        game.getProperty(3).setOwner(game.getCurrentPlayer());
        perform(game, new BuildAction(7, game.getProperty(1), game.getProperty(3)));
        assertEquals(4, game.getProperty(1).getHouseCount());
        assertEquals(3, game.getProperty(3).getHouseCount());
    }

    @Test
    public void buySomeHousesOtherPriority() {
        game.getProperty(1).setOwner(game.getCurrentPlayer());
        game.getProperty(3).setOwner(game.getCurrentPlayer());
        // Property 3 first, then property 1
        perform(game, new BuildAction(7, game.getProperty(3), game.getProperty(1)));
        assertEquals(4, game.getProperty(3).getHouseCount());
        assertEquals(3, game.getProperty(1).getHouseCount());
    }

    @Test
    public void preventBuyingUnevenHouses() {
        game.getProperty(1).setOwner(game.getCurrentPlayer());
        game.getProperty(3).setOwner(game.getCurrentPlayer());
        assertFalse(game.getCurrentPlayer().isAllowed(new BuildAction(2, game.getProperty(3))));
    }

    @Test
    public void preventBuyingHousesWhenYouDoNotOwnGroup() {
        game.getProperty(1).setOwner(game.getCurrentPlayer());
        assertFalse(game.getCurrentPlayer().isAllowed(new BuildAction(1, game.getProperty(1))));
    }

    @Test
    public void visitHotel() {
        Player opponent = game.getPlayer(1);
        int opponentMoney = opponent.getMoney();
        game.getProperty(1).setOwner(opponent);
        game.getProperty(3).setOwner(opponent);
        game.getProperty(1).setHouseCount(5);
        game.getProperty(3).setHouseCount(5);
        assertEquals(450, game.getProperty(3).getCurrentRent());

        Player player = game.getCurrentPlayer();
        int money = player.getMoney();
        TestUtils.perform(game, new RollDiceAction(1, 3));
        assertEquals(opponent, game.getCurrentPlayer());
        assertEquals(money - 450, player.getMoney());
        assertEquals(opponentMoney + 450, opponent.getMoney());
    }

    @Test
    @Ignore
    public void auctionHousesWhenFewExist() {
        throw new UnsupportedOperationException("Buildings are currently infinite");
    }

}
