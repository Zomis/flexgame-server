package net.zomis.spring.games.generic;

import net.zomis.spring.games.messages.GameInfo;
import net.zomis.spring.games.messages.JoinGameResponse;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class GenericGame {

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

}
