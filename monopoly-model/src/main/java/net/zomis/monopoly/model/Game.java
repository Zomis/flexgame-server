package net.zomis.monopoly.model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private List<Player> players;
    private List<Property> properties;

    private GameSetup setup;
    private int currentPlayer;

    public Game(GameSetup gameSetup, List<Player> players) {
        this.setup = gameSetup;
        this.players = new ArrayList<>(players);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return this.players.get(currentPlayer);
    }

    public Property getProperty(int i) {
        return null;
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }


}
