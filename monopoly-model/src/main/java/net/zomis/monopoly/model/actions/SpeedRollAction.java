package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.GameAction;
import net.zomis.monopoly.model.GameActionResult;
import net.zomis.monopoly.model.Player;

import java.util.Random;

public class SpeedRollAction implements GameAction {

    public static enum SpeedRollType {
        ONE, TWO, THREE, MR_MONOPOLY, BUS;

        private static final SpeedRollType[] values = { ONE, TWO, THREE, MR_MONOPOLY, MR_MONOPOLY, BUS };

        public static SpeedRollType roll(Random random) {
            return values[random.nextInt(values.length)];
        }

    }

    public static GameAction roll(Random random) {
        return new SpeedRollAction(SpeedRollType.roll(random), RollDiceAction.roll(random));
    }

    public SpeedRollAction(SpeedRollType speedRollDieValue, RollDiceAction rollDiceAction) {

    }

    @Override
    public boolean isAllowed(Player player) {
        return false;
    }

    @Override
    public GameActionResult perform(Player player) {
        return null;
    }

}
