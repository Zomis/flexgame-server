package net.zomis.spring.games.generic;

import net.zomis.spring.games.generic.v2.PlayerController;

public class PlayerInGame {

    private final String name;
    private final int index;
    private final String authToken;
    private final Object playerConfig;
    private final PlayerController controller;

    public PlayerInGame(String name, int index, String authToken, Object playerConfiguration, PlayerController controller) {
        this.name = name;
        this.index = index;
        this.authToken = authToken;
        this.playerConfig = playerConfiguration;
        this.controller = controller;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean hasToken(String authToken) {
        return this.authToken.equals(authToken);
    }

    @Override
    public String toString() {
        return "PlayerInGame{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }

    public PlayerController getController() {
        return controller;
    }
}
