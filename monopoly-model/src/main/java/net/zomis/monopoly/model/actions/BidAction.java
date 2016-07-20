package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.GameAction;
import net.zomis.monopoly.model.GameActionResult;
import net.zomis.monopoly.model.Player;

public class BidAction implements GameAction {

    public BidAction(int bid) {

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
