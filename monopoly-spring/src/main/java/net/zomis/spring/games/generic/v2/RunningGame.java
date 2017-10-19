package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.GenericGame;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.messages.GameInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RunningGame<G> {

    private static final Logger logger = LoggerFactory.getLogger(GenericGame.class);
    private final GameHelper2<G> gameHelper;
    private final String id;
    private final G game;
    private final List<PlayerInGame> playerKeys;
    private long lastActivity;

    public RunningGame(GameHelper2<G> gameHelper, String id, List<PlayerInGame> players, G game) {
        this.gameHelper = gameHelper;
        this.id = id;
        this.playerKeys = Collections.synchronizedList(new ArrayList<>(players));
        this.game = game;
    }

    public GameInfo getGameInfo() {
        return new GameInfo(id, playerKeys.stream().map(PlayerInGame::getName).collect(Collectors.toList()),
            lastActivity, playerKeys.size(), true);
    }

    public Object getGameDetails() {
        // TODO: Add support for passing from who's perspective (Needed for games with private information)
        return gameHelper.gameDetails(this, null);
    }

    public G getGame() {
        return game;
    }

    public Optional<PlayerInGame> authorize(String authToken) {
        return playerKeys.stream()
                .filter(e -> e.hasToken(authToken))
                .findFirst();
    }

}
