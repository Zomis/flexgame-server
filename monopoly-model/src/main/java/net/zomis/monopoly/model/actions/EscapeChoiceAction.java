package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.GameAction;
import net.zomis.monopoly.model.GameActionResult;
import net.zomis.monopoly.model.GameTask;
import net.zomis.monopoly.model.Player;

import java.util.Arrays;
import java.util.Random;

public class EscapeChoiceAction {

    public static final int PAY_COST = 50;

    private static boolean needToChoose(Player player) {
        return player.getGame().isTaskType(GameTask.GameTaskType.GET_OUT_OF_JAIL_CHOICE);
    }

    public static GameAction roll(RollDiceAction dice) {
        return new GameAction() {
            @Override
            public boolean isAllowed(Player player) {
                return needToChoose(player);
            }

            @Override
            public GameActionResult perform(Player player) {
                if (dice.isDoubles()) {
                    player.escapeJail();
                    player.perform(dice);
                    return new GameActionResult(true, "Player rolled " + Arrays.toString(dice.getValues()) +
                        " and escaped jail");
                } else {
                    player.escapeAttempts++;
                    if (player.escapeAttempts == 3) {
                        pay(dice).perform(player);
                        return new GameActionResult(true, "Player rolled " + Arrays.toString(dice.getValues()) +
                                " and failed to escape jail three times in a row so player has to pay.");
                    }
                    return new GameActionResult(true, "Player rolled " + Arrays.toString(dice.getValues()) +
                            " and failed to escape jail");
                }
            }
        };
    }

    public static GameAction pay(RollDiceAction dice) {
        return new GameAction() {
            @Override
            public boolean isAllowed(Player player) {
                return needToChoose(player) && player.getMoney() >= PAY_COST;
            }

            @Override
            public GameActionResult perform(Player player) {
                player.pay(null, PAY_COST);
                player.escapeJail();
                player.perform(dice);
                return new GameActionResult(true, "Player payed to get out of jail");
            }
        };
    }

    public static final GameAction USE_CARD = new GameAction() {
        @Override
        public boolean isAllowed(Player player) {
            throw new UnsupportedOperationException("Get out of jail card is not implemented");
        }

        @Override
        public GameActionResult perform(Player player) {
            throw new UnsupportedOperationException("Get out of jail card is not implemented");
        }
    };

}
