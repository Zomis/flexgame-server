package net.zomis.monopoly.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PropertyGroup {

    private final String name;
    private final int houseCost;
    private final List<Property> properties = new ArrayList<>();

    public PropertyGroup(String name, int houseCost) {
        this.name = name;
        this.houseCost = houseCost;
    }

    public String getName() {
        return name;
    }

    public int getHouseCost() {
        return houseCost;
    }

    public List<Property> getProperties() {
        return new ArrayList<>(properties);
    }

    public void addProperty(Property property) {
        this.properties.add(property);
        property.setGroup(this);
    }

    public Stream<Property> getOwnedBy(Player player) {
        return properties.stream().filter(p -> p.isOwnedBy(player));
    }
}
