package net.zomis.monopoly.model;

public class Player {

    private final Game game;
    private final String name;
    private final int index;
    private Inventory<Property> inventory;
    private Piece piece;
    private int position;
    private int money;

    public Player(Game game, int index, String name) {
        this.game = game;
        this.index = index;
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

    public void pay(Player opponent, long rent) {
        throw new UnsupportedOperationException();
    }

    public Game getGame() {
        return game;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void collect(int money) {
        this.money += money;
    }

    public Property getPositionProperty() {
        return this.game.getProperty(position);
    }

    public int getIndex() {
        return index;
    }

    public Piece getPiece() {
        return piece;
    }
}
