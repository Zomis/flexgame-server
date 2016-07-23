package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.*;

public class BuyChoiceAction {

    public static final GameAction BUY = new GameAction() {
        @Override
        public boolean isAllowed(Player player) {
            if (!isBuyChoice(player)) {
                return false;
            }
            return player.getMoney() >= player.getGame().getState().getProperty().getCost();
        }

        @Override
        public GameActionResult perform(Player player) {
            GameTask state = player.getGame().popState();
            Property property = state.getProperty();
            player.pay(null, property.getCost());
            property.setOwner(player);
            return new GameActionResult(true, player.getName() + " bought " + property);
        }
    };

    private static boolean isBuyChoice(Player player) {
        GameTask state = player.getGame().getState();
        return state != null && state.getType() == GameTask.GameTaskType.BUY_OR_NOT && state.getActor() == player;
    }

    public static final GameAction NOT_BUY = new GameAction() {
        @Override
        public boolean isAllowed(Player player) {
            return isBuyChoice(player);
        }

        @Override
        public GameActionResult perform(Player player) {
            throw new UnsupportedOperationException("Auctions not implemented yet");
        }
    };

}
