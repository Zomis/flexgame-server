package net.zomis.monopoly.model;

public interface GameAction {

    boolean isAllowed(Player player);
    GameActionResult perform(Player player);

}
