package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.RollDiceAction;

import java.util.Arrays;
import java.util.Optional;

public class Property {

    private final String name;
    private final int cost;
    private final int[] rents;
    private final LandAction action;
    private PropertyGroup group;
    private int houseCount;
    private Optional<Player> owner = Optional.empty();

    public Property(String name, int cost, int[] rents, LandAction action) {
        if (action == null) {
            throw new NullPointerException("Action is required for property: " + name);
        }
        this.name = name;
        this.cost = cost;
        this.rents = rents;
        this.action = action;
    }

    void setGroup(PropertyGroup group) {
        this.group = group;
    }

    public void setOwner(Player owner) {
        this.owner = Optional.of(owner);
    }

    public int getCost() {
        return cost;
    }

    public Optional<Player> getOwner() {
        return owner;
    }

    public int getCurrentRent() {
        return rents[houseCount];
    }

    public int getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    public PropertyGroup getGroup() {
        return group;
    }

    public boolean hasOwner() {
        return owner.isPresent();
    }

    public boolean isOwnedBy(Player player) {
        return owner.orElse(null) == player;
    }

    public void land(Player player, GameAction byAction) {
        this.action.land(player, this, byAction);
    }

    public int[] getRents() {
        return Arrays.copyOf(rents, rents.length);
    }

    public String getGroupName() {
        return group == null ? "" : group.getName();
    }

    public int getHouseCost() {
        return group == null ? 0 : group.getHouseCost();
    }

    public String getName() {
        return name;
    }
}
