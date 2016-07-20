package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.GameAction;
import net.zomis.monopoly.model.GameActionResult;
import net.zomis.monopoly.model.Player;

public class BuyChoiceAction {

    public static final GameAction BUY = new GameAction() {
        @Override
        public boolean isAllowed(Player player) {
            return false;
        }

        @Override
        public GameActionResult perform(Player player) {
            return null;
        }
    };
    public static final GameAction NOT_BUY = new GameAction() {
        @Override
        public boolean isAllowed(Player player) {
            return false;
        }

        @Override
        public GameActionResult perform(Player player) {
            return null;
        }
    };

}
