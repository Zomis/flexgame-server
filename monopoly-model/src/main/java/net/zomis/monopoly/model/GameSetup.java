package net.zomis.monopoly.model;

import java.util.ArrayList;
import java.util.List;

public class GameSetup {

    private boolean speedDie;
    private final List<String> players = new ArrayList<>();

    public GameSetup addPlayer(String name) {
        this.players.add(name);
        return this;
    }

    public Game create() {
        return Game.createGame(this, players);
    }

    public GameSetup withSpeedDie(boolean speedDie) {
        this.speedDie = speedDie;
        return this;
    }

    public boolean isSpeedDie() {
        return speedDie;
    }

}
