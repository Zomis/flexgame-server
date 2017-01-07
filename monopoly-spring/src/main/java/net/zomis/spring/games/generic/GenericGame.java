package net.zomis.spring.games.generic;

import net.zomis.spring.games.messages.GameInfo;
import net.zomis.spring.games.messages.JoinGameResponse;
import net.zomis.spring.games.messages.StartGameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class GenericGame {

    private static final Logger logger = LoggerFactory.getLogger(GenericGame.class);
    private final GameHelper gameHelper;
    private final UUID uuid;
    private final Object object;
    private final List<PlayerInGame> playerKeys = Collections.synchronizedList(new ArrayList<>());
    private final Random random = new Random(42);
    private boolean started;
    private final TokenGenerator tokenGenerator;

    public GenericGame(GameHelper<?, ?> gameHelper, Object game, TokenGenerator tokenGenerator) {
        this.gameHelper = gameHelper;
        this.tokenGenerator = tokenGenerator;
        this.uuid = UUID.randomUUID();
        this.object = game;
    }

    public GameInfo getGameInfo() {
        return new GameInfo(uuid, playerKeys.stream().map(PlayerInGame::getName).collect(Collectors.toList()),
            Instant.now().toEpochMilli(),
            playerKeys.size(), started);
    }

    public UUID getUUID() {
        return uuid;
    }

    public ResponseEntity<JoinGameResponse> addPlayer(String playerName, Object playerConfig) {
//        if (playerKeys.containsKey(playerName)) {
//            return ResponseEntity.badRequest().body(null);
//        }
        UUID playerKey = UUID.randomUUID();
        synchronized (playerKeys) {
            String token = tokenGenerator.generateToken();
            playerKeys.add(new PlayerInGame(playerName, playerKeys.size() - 1,
                token, object));
        }
        logger.info("Added player to game: " + this + " keys are now: " + playerKeys);

        return ResponseEntity.ok(new JoinGameResponse(playerKey));
    }

    public Object getGameDetails() {
        return gameHelper.gameDetails(object);
    }

    public Random getRandom() {
        return random;
    }

    public Optional<PlayerInGame> authorize(String authToken) {
        return playerKeys.stream()
            .filter(e -> e.hasToken(authToken))
            .findFirst();
    }

    public StartGameResponse start() {
        return new StartGameResponse(true);
    }

}
