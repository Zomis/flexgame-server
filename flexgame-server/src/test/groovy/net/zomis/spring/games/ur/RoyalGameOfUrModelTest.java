package net.zomis.spring.games.ur;

import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RoyalGameOfUrModelTest {

    @Test
    public void testCannotMove() {
        RoyalGameOfUr game = new RoyalGameOfUr();
        assertFalse(game.canMove(0, 4, 3));
    }

    @Test
    public void testIllegalMoveDoesNothing() {
        RoyalGameOfUr game = new RoyalGameOfUr();
        boolean allowed = game.move(0, 4, 3);
        assertTrue(Arrays.stream(game.getPieces()[0]).allMatch(i -> i == 0));
        assertFalse(allowed);
    }

}
