package net.zomis.monopoly.model;

import org.junit.Before;
import org.junit.Test;

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
        throw new UnsupportedOperationException();
    }

    @Test
    public void gotoJailThrowingDoublesThreeTimes() {
        throw new UnsupportedOperationException();
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
