package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.RollDiceAction;
import net.zomis.monopoly.model.actions.SpeedRollAction;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static net.zomis.monopoly.model.TestUtils.perform;
import static org.junit.Assert.assertFalse;

public class SpeedDiePlayTest {

    private Game game;

    @Before
    public void setup() {
        game = new GameSetup()
            .addPlayer("One")
            .addPlayer("Two")
            .withSpeedDie(true)
            .create();
    }

    private void activateSpeedDie() {

    }

    @Test
    public void speedDieTriples() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void speedDieBus() {
        activateSpeedDie();
        perform(game, new SpeedRollAction(SpeedRollAction.SpeedRollType.BUS, new RollDiceAction(1, 5)));
        game.getCurrentPlayer().perform(new RollDiceAction(6));
        throw new UnsupportedOperationException();
    }

    @Test
    public void speedDieMrMonopoly() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void speedDieNotOnFirstRound() {
        assertFalse(game.getCurrentPlayer().isAllowed(SpeedRollAction.roll(new Random())));
    }

}
