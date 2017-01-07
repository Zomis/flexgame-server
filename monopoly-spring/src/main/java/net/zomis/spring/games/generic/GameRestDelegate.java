package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.JsonNode;
import net.zomis.spring.games.messages.*;
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

    public ResponseEntity<CreateGameResponse> startNewGame(CreateGameRequest request) {
        logger.info("Received start game request: " + request);
        GenericGame game = new GenericGame(helper, helper.constructGame(request.getGameConfig()), tokenGenerator);
        games.put(game.getUUID(), game);

        ResponseEntity<JoinGameResponse> joinResponse = game.addPlayer(request.getPlayerName(), request.getPlayerConfig());
        CreateGameResponse response = new CreateGameResponse(game.getUUID(),
            UUID.fromString(joinResponse.getBody().getPrivateKey()));
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
        logger.info("Received action request of type '" + actionType + "' in game " + gameUUID + ": " + jsonNode);
        Optional<GenericGame> game = getGame(UUID.fromString(gameUUID));
        if (!game.isPresent()) {
            return ResponseEntity.badRequest().body(new GameMoveResult("Game not found"));
        }
        Optional<PlayerInGame> player = game.get().authorize(authToken);
        if (!player.isPresent()) {
            logger.warn("No player found with " + authToken + " in game " + game);
            return ResponseEntity.badRequest().body(new GameMoveResult("Player in game not found"));
        }
        GameMoveResult result = helper.performAction(player.get().getIndex(), jsonNode);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<StartGameResponse> start(String game) {
        Optional<GenericGame> theGame = getGame(UUID.fromString(game));
        theGame.get().start();
        return ResponseEntity.ok(new StartGameResponse(true));
    }

}
