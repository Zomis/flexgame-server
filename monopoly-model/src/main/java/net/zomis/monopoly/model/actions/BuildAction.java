package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.*;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

public class BuildAction implements GameAction {

    private final int amount;
    private final Property[] properties;

    public BuildAction(int houseCount, Property... properties) {
        this.amount = houseCount;
        this.properties = Arrays.copyOf(properties, properties.length);
    }

    @Override
    public boolean isAllowed(Player player) {
        if (Arrays.stream(properties).map(Property::getGroup).distinct().count() != 1) {
            // Can only buy on one group at a time
            return false;
        }
        if (properties[0].getGroup().getHouseCost() <= 0) {
            // Not possible to buy on all groups
            return false;
        }
        if (!properties[0].getGroup().getProperties().stream().allMatch(p -> p.isOwnedBy(player))) {
            // Can only buy if you own the whole group
            return false;
        }
        PropertyGroup group = properties[0].getGroup();
        int[] resultingHouseCount = group.getProperties().stream().mapToInt(Property::getHouseCount).toArray();

        for (int i = 0; i < amount; i++) {
            int index = i % properties.length;
            Property property = properties[index];
            int groupIndex = group.getProperties().indexOf(property);
            resultingHouseCount[groupIndex]++;
        }

        IntSummaryStatistics stats = Arrays.stream(resultingHouseCount).summaryStatistics();
        boolean balanced = stats.getMax() - stats.getMin() <= 1;
        return balanced && player.getGame().isEmptyStack() && player.getGame().getCurrentPlayer() == player && player.getMoney() >= getCost();
    }

    private int getCost() {
        return amount * properties[0].getGroup().getHouseCost();
    }

    @Override
    public GameActionResult perform(Player player) {
        player.pay(null, getCost());
        for (int i = 0; i < amount; i++) {
            int propertyIndex = i % properties.length;
            Property property = properties[propertyIndex];
            property.setHouseCount(property.getHouseCount() + 1);
        }
        return new GameActionResult(true, player + " built " + amount + " buildings on " + properties[0].getGroup().getName());
    }

}
