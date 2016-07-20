package net.zomis.monopoly.model;

public class Player {

    private Inventory<Property> inventory;
    private Piece piece;
    private Tile tile;
    private int money;
    private final String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public GameActionResult perform(GameAction action) {
        return action.perform(this);
    }

    public boolean isAllowed(GameAction action) {
        return action.isAllowed(this);
    }

}
