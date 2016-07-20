package net.zomis.monopoly.model;

import java.util.ArrayList;
import java.util.List;

public class GameSetup {

    private boolean speedDie;
    private final List<Player> players = new ArrayList<>();

    public GameSetup addPlayer(String name) {
        this.players.add(new Player(name));
        return this;
    }

    public Game create() {
        return new Game(this, players);
    }

    public GameSetup withSpeedDie(boolean speedDie) {
        this.speedDie = speedDie;
        return this;
    }

    public boolean isSpeedDie() {
        return speedDie;
    }

}
