package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.BuildAction;
import org.junit.Before;
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
        assertFalse(game.getCurrentPlayer().isAllowed(new BuildAction(2, game.getProperty(3))));
    }

    @Test
    public void visitHotel() {
        throw new UnsupportedOperationException();
    }

}
