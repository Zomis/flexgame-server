package net.zomis.monopoly.spring;

import net.zomis.monopoly.model.Player;
import net.zomis.monopoly.model.Property;

import java.util.Arrays;

public class GameProperty {

    private final String name;
    private final int houseCount;
    private final int cost;
    private final int owner;
    private final int[] rents;
    private final String propertyGroup;
    private final int buildCost;

    public GameProperty(Property property) {
        this.name = property.getName();
        this.houseCount = property.getHouseCount();
        this.cost = property.getCost();
        this.rents = property.getRents();
        this.propertyGroup = property.getGroupName();
        this.buildCost = property.getHouseCost();
        this.owner = property.getOwner().map(Player::getIndex).orElse(-1);
    }

    public int getBuildCost() {
        return buildCost;
    }

    public int getCost() {
        return cost;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public int getOwner() {
        return owner;
    }

    public int[] getRents() {
        return Arrays.copyOf(rents, rents.length);
    }

    public String getPropertyGroup() {
        return propertyGroup;
    }

}
