package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.GenericGame;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.GameHelper2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RunningGame<G> {

    private static final Logger logger = LoggerFactory.getLogger(GenericGame.class);
    private final GameHelper2<G> gameHelper;
    private final String id;
    private final G game;
    private final List<PlayerInGame> playerKeys = Collections.synchronizedList(new ArrayList<>());

    public RunningGame(GameHelper2<G> gameHelper, String id, G game) {
        this.gameHelper = gameHelper;
        this.id = id;
        this.game = game;
    }

    public G getGame() {
        return game;
    }
}
