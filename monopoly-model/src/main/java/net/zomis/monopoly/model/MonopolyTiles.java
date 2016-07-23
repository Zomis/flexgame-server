package net.zomis.monopoly.model;

import net.zomis.monopoly.model.actions.RollDiceAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonopolyTiles {

    private static final LandAction LAND_ON_PROPERTY_ACTION = null;
    private static final LandAction DO_NOTHING = (player, tile, byAction) -> {};
    private static final int[] UTILITY_RENT_MULTIPLICATOR = { 0, 4, 10 };

    private static final Property FREE_PARKING = new Property("Free Parking", 0, null, DO_NOTHING);

    // Unimplemented
    private static final Property GO_TO_JAIL = unbuyableProperty("Go to Jail", DO_NOTHING);
    private static final Property LUXURY_TAX = unbuyableProperty("Luxury Tax", DO_NOTHING);
    private static final Property GO = unbuyableProperty("Go", DO_NOTHING);
    private static final Property COMMUNITY_CHEST = unbuyableProperty("Community Chest", DO_NOTHING);
    private static final Property INCOME_TAX = unbuyableProperty("Income Tax", DO_NOTHING);
    private static final Property CHANCE = unbuyableProperty("Chance", DO_NOTHING);
    private static final Property JAIL = unbuyableProperty("Jail", DO_NOTHING);

    private static Property unbuyableProperty(String name, LandAction action) {
        return new Property(name, 0, null, action);
    }

    public static List<PropertyGroup> defaultPropertyGroups() {
        List<PropertyGroup> result = new ArrayList<>();

        result.add(group("Purple", 50,
                property("Mediterranean Avenue").cost(60).rents(2, 10, 30, 90, 160, 250),
                property("Baltic Avenue").cost(60).rents(4, 20, 60, 180, 320, 450)));

        result.add(group("Light Blue", 50,
                property("Oriental Avenue").cost(100).rents(6, 30, 90, 270, 400, 550),
                property("Vermont Avenue").cost(100).rents(6, 30, 90, 270, 400, 550),
                property("Connecticut Avenue").cost(120).rents(8, 40, 100, 300, 450, 600)));

        result.add(group("Pink", 100,
                property("St. Charles Place").cost(140).rents(10, 50, 150, 450, 625, 750),
                property("States Avenue").cost(140).rents(10, 50, 150, 450, 625, 750),
                property("Virginia Avenue").cost(160).rents(12, 60, 180, 500, 700, 900)));

        result.add(group("Orange", 100,
                property("St. James Place").cost(180).rents(14, 70, 200, 550, 750, 950),
                property("Tennessee Avenue").cost(180).rents(14, 70, 200, 550, 750, 950),
                property("New York Avenue").cost(200).rents(16, 80, 220, 600, 800, 1000)));

        result.add(group("Red", 150,
                property("Kentucky Avenue").cost(220).rents(18, 90, 250, 700, 875, 1050),
                property("Indiana Avenue").cost(220).rents(18, 90, 250, 700, 875, 1050),
                property("Illinois Avenue").cost(240).rents(20, 100, 300, 750, 925, 1100)));

        result.add(group("Yellow", 150,
                property("Atlantic Avenue").cost(260).rents(22, 110, 330, 800, 975, 1150),
                property("Ventnor Avenue").cost(260).rents(22, 110, 330, 800, 975, 1150),
                property("Marvin Gardens").cost(280).rents(24, 120, 360, 850, 1025, 1200)));

        result.add(group("Green", 200,
                property("Pacific Avenue").cost(300).rents(26, 130, 390, 900, 1100, 1275),
                property("North Carolina Avenue").cost(300).rents(26, 130, 390, 900, 1100, 1275),
                property("Pennsylvania Avenue").cost(320).rents(28, 150, 450, 1000, 1200, 1400)));

        result.add(group("Dark Blue", 200,
                property("Park Place").cost(350).rents(35, 175, 500, 1100, 1300, 1500),
                property("Boardwalk").cost(400).rents(50, 200, 600, 1400, 1700, 2000)));

        return result;
    }

    public static List<Property> createDefault() {
        Stream<PropertyGroup> allGroups = defaultPropertyGroups().stream();
        Iterator<Property> groups = allGroups.flatMap(pg -> pg.getProperties().stream()).collect(Collectors.toList()).iterator();

        PropertyGroup railroadGroup = new PropertyGroup("Railroads", 0);
        PropertyGroup utilityGroup = new PropertyGroup("Utilities", 0);

        List<Property> result = Arrays.asList(
                GO, take(groups), COMMUNITY_CHEST, take(groups), INCOME_TAX,
                railroad(railroadGroup, "Reading Railroad"), take(groups), CHANCE, take(groups), take(groups),
                JAIL, take(groups), util(utilityGroup, "Electric Company"), take(groups), take(groups),
                railroad(railroadGroup, "Pennsylvania Railroad"), take(groups), COMMUNITY_CHEST, take(groups), take(groups),
                FREE_PARKING, take(groups), CHANCE, take(groups), take(groups),
                railroad(railroadGroup, "B. & O. Railroad"), take(groups), take(groups), util(utilityGroup, "Water Works"), take(groups),
                GO_TO_JAIL, take(groups), take(groups), COMMUNITY_CHEST, take(groups),
                railroad(railroadGroup, "Short Line Railroad"), CHANCE, take(groups), LUXURY_TAX, take(groups));
        return result;
    }

    private static Property railroad(PropertyGroup group, String name) {
        Property property = new Property(name, 200, null, MonopolyTiles::railroadAction);
        group.addProperty(property);
        return property;
    }

    public static void railroadAction(Player player, Property tile, GameAction byAction) {
        if (tile.hasOwner()) {
            Player opponent = tile.getOwner().get();
            Stream<Property> opponentOwnedProperties = tile.getGroup().getOwnedBy(opponent);
            long rent = opponentOwnedProperties.count() * 50;
            player.pay(opponent, rent);
        } else {
            buyOrNotBuy(player, tile, byAction);
        }
    }

    private static void buyOrNotBuy(Player player, Property tile, GameAction byAction) {

    }

    private static Property util(PropertyGroup utilityGroup, String name) {
        Property property = new Property(name, 200, null, MonopolyTiles::utilityAction);
        utilityGroup.addProperty(property);
        return property;
    }

    private static void utilityAction(Player player, Property property, GameAction action) {
        if (property.hasOwner()) {
            Player opponent = property.getOwner().get();
            Stream<Property> opponentOwnedProperties = property.getGroup().getOwnedBy(opponent);
            if (!(action instanceof RollDiceAction)) {
                throw new IllegalStateException("Landed on utility " + property + "by unknown action: " + action);
            }
            RollDiceAction diceRoll = (RollDiceAction) action;

            long ownedCount = opponentOwnedProperties.count();

            long rent = UTILITY_RENT_MULTIPLICATOR[(int) ownedCount] * diceRoll.getTotal();
            player.pay(opponent, rent);
        } else {
            buyOrNotBuy(player, property, action);
        }
    }

    private static Property take(Iterator<Property> iterable) {
        return iterable.next();
    }

    private static PropertyBuilder property(String name) {
        return new PropertyBuilder(name);
    }

    private static PropertyGroup group(String name, int houseCost, PropertyBuilder... properties) {
        List<Property> resultProperties = Arrays.stream(properties)
            .map(pb -> new Property(pb.name, pb.cost, pb.rents, LAND_ON_PROPERTY_ACTION))
            .collect(Collectors.toList());
        PropertyGroup group = new PropertyGroup(name, houseCost);
        resultProperties.forEach(group::addProperty);
        return group;
    }

    private static class PropertyBuilder {

        private final String name;
        private int cost;
        private int[] rents;

        public PropertyBuilder(String name) {
            this.name = name;
        }

        public PropertyBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        public PropertyBuilder rents(int... rents) {
            this.rents = Arrays.copyOf(rents, rents.length);
            return this;
        }

    }

}
