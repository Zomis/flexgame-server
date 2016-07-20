package net.zomis.monopoly.model;

import java.util.List;

public class GameSetup {

    boolean speedDie;
    List<Player> players;

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

}
