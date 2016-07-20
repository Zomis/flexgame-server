package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.GameAction;
import net.zomis.monopoly.model.GameActionResult;
import net.zomis.monopoly.model.Player;

import java.util.Arrays;
import java.util.Random;

public class RollDiceAction implements GameAction {

    private final int[] values;

    public RollDiceAction(int... values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    public int[] getValues() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public boolean isAllowed(Player player) {
        return false;
    }

    @Override
    public GameActionResult perform(Player player) {
        return null;
    }

    public static RollDiceAction roll(Random random) {
        return null;
    }

}
