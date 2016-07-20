package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.GameAction;
import net.zomis.monopoly.model.GameActionResult;
import net.zomis.monopoly.model.Player;
import net.zomis.monopoly.model.Property;

public class BuildAction implements GameAction {

    public BuildAction(int houseCount, Property... properties) {

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
