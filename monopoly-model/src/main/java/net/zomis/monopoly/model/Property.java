package net.zomis.monopoly.model;

public class Property {

    private String name;
    private int cost;
    private int houseCount;
    private Player owner;
    private int[] rents;

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getCost() {
        return cost;
    }

    public Player getOwner() {
        return owner;
    }

    public int getCurrentRent() {
        return rents[houseCount];
    }

    public int getHouseCount() {
        return houseCount;
    }
}
