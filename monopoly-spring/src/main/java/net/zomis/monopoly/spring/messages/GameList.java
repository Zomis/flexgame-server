package net.zomis.monopoly.spring.messages;

import java.util.List;

public class GameList {

    private final List<GameInfo> games;

    public GameList(List<GameInfo> games) {
        this.games = games;
    }

    public List<GameInfo> getGames() {
        return games;
    }

}
