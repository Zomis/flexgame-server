package net.zomis.spring.games.messages;

import net.zomis.spring.games.messages.GameInfo;

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
