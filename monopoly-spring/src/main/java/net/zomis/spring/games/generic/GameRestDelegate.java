package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.JsonNode;
import net.zomis.spring.games.messages.CreateGameResponse;
import net.zomis.spring.games.messages.GameList;
import net.zomis.spring.games.messages.JoinGameResponse;
import net.zomis.spring.games.messages.GameMoveResult;
import net.zomis.spring.games.messages.GameInfo;
import net.zomis.spring.games.messages.JoinGameRequest;
import net.zomis.spring.games.messages.StartGameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameRestDelegate {

    private static final Logger logger = LoggerFactory.getLogger(GameRestDelegate.class);

    private final GameHelper helper;
    private final TokenGenerator tokenGenerator;
    private final Map<UUID, GenericGame> games = new ConcurrentHashMap<>();

    public GameRestDelegate(GameHelper helper, TokenGenerator tokenGenerator) {
        this.helper = helper;
        this.tokenGenerator = tokenGenerator;
    }

    private Optional<GenericGame> getGame(UUID uuid) {
        if (games.containsKey(uuid)) {
            return Optional.of(games.get(uuid));
        }
        return Optional.empty();
    }

    public GameList listGames() {
        return new GameList(games.values().stream().map(GenericGame::getGameInfo).collect(Collectors.toList()));
    }

    public ResponseEntity<CreateGameResponse> startNewGame(StartGameRequest request) {
        logger.info("Received start game request: " + request);
        GenericGame game = new GenericGame(helper, helper.constructGame(request.getGameConfig()), tokenGenerator);
        games.put(game.getUUID(), game);

        ResponseEntity<JoinGameResponse> joinResponse = game.addPlayer(request.getPlayerName(), request.getPlayerConfig());
        CreateGameResponse response = new CreateGameResponse(game.getUUID(), UUID.fromString(joinResponse.getBody().getUuid()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<JoinGameResponse> joinGame(String gameID, JoinGameRequest request) {
        logger.info("Received join game request: " + request);
        Optional<GenericGame> game = getGame(UUID.fromString(gameID));
        if (!game.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        return game.get().addPlayer(request.getPlayerName(), request.getPlayerConfig());
    }

    public ResponseEntity<GameInfo> summary(String uuid) {
        Optional<GenericGame> game = getGame(UUID.fromString(uuid));
        if (!game.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(game.get().getGameInfo());
    }

    public ResponseEntity<Object> getDetailedInfo(String uuid) {
        logger.info("Received details request: " + uuid);
        Optional<GenericGame> game = getGame(UUID.fromString(uuid));
        if (!game.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(game.get().getGameDetails());
    }

    public ResponseEntity<GameMoveResult> action(String gameUUID, String authToken,
            String actionType, JsonNode jsonNode) {
        logger.info("Received action request of type '" + actionType + "' in game " + gameUUID);
        Optional<GenericGame> game = getGame(UUID.fromString(gameUUID));
        if (!game.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        Optional<PlayerInGame> player = game.get().authorize(authToken);
        if (!player.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        GameMoveResult result = helper.performAction(player.get().getIndex(), jsonNode);
        return ResponseEntity.ok(result);
    }

}
