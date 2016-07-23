package net.zomis.monopoly.model;

public class Player {

    private final Game game;
    private final String name;
    private final int index;
    private Inventory<Property> inventory;
    private Piece piece;
    private int position;
    private int money = 2000;
    int doublesRolled;
    public int escapeAttempts;
    private boolean inJail;

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
        GameActionResult result = action.perform(this);
        if (result.isOk()) {
            // if player has rolled doubles, then take turn again
            if (doublesRolled == 0 && game.isEmptyStack()) {
                game.nextPlayer();
            }
        }
        return result;
    }

    public boolean isAllowed(GameAction action) {
        return action.isAllowed(this);
    }

    public void pay(Player opponent, long amount) {
        this.money -= amount;
        if (opponent != null) {
            opponent.money += amount;
        } else {
            // check if free parking should collect the money
        }
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

    public int getDoublesRolled() {
        return doublesRolled;
    }

    public void addDoublesRolled() {
        doublesRolled++;
    }

    public void gotoJail() {
        this.doublesRolled = 0;
        this.inJail = true;
        this.position = game.getPropertyIndex(MonopolyTiles.JAIL);
    }

    public boolean isInJail() {
        return inJail;
    }

    public void escapeJail() {
        this.inJail = false;
    }

}
