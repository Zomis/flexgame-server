package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.JsonNode;
import net.zomis.spring.games.generic.v2.ActionResult;
import net.zomis.spring.games.generic.v2.GameHelper2;
import net.zomis.spring.games.generic.v2.LobbyGame;
import net.zomis.spring.games.generic.v2.RunningGame;
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
import java.util.stream.Stream;

public class GameRestDelegate2<G> {

    private static final Logger logger = LoggerFactory.getLogger(GameRestDelegate2.class);

    private final GameHelper2<G> helper;
    private final TokenGenerator tokenGenerator;
    private final Map<String, LobbyGame<G>> lobbyGames = new ConcurrentHashMap<>();
    private final Map<String, RunningGame<G>> runningGames = new ConcurrentHashMap<>();

    public GameRestDelegate2(GameHelper2<G> helper, TokenGenerator tokenGenerator) {
        this.helper = helper;
        this.tokenGenerator = tokenGenerator;
    }

    private Optional<LobbyGame<G>> getLobbyGame(String id) {
        if (lobbyGames.containsKey(id)) {
            return Optional.of(lobbyGames.get(id));
        }
        return Optional.empty();
    }

    public GameList listGames() {
        Stream<GameInfo> infos = Stream.concat(lobbyGames.values().stream().map(LobbyGame::getGameInfo),
                runningGames.values().stream().map(RunningGame::getGameInfo));
        return new GameList(infos.collect(Collectors.toList()));
    }

    public ResponseEntity<CreateGameResponse> startNewGame(CreateGameRequest request) {
        logger.info("Received start game request: " + request);
        LobbyGame<G> game = helper.createGame(request.getGameConfig());
        lobbyGames.put(game.getId(), game);

        JoinGameResponse joinResponse = game.joinRequest(request.getPlayerName(), request.getPlayerConfig());
        CreateGameResponse response = new CreateGameResponse(game.getId(), joinResponse.getPrivateKey());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<JoinGameResponse> joinGame(String gameID, JoinGameRequest request) {
        logger.info("Received join game request: " + request);
        Optional<LobbyGame<G>> game = getLobbyGame(gameID);
        if (!game.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        JoinGameResponse joinResponse = game.get().joinRequest(request.getPlayerName(), request.getPlayerConfig());
        if (joinResponse == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(joinResponse);
    }

    public ResponseEntity<GameInfo> summary(String uuid) {
        Optional<GameInfo> lobby = Optional.ofNullable(lobbyGames.get(uuid)).map(LobbyGame::getGameInfo);
        Optional<GameInfo> running = Optional.ofNullable(runningGames.get(uuid)).map(RunningGame::getGameInfo);
        Optional<GameInfo> info = Stream.of(lobby, running).filter(Optional::isPresent).findAny().map(Optional::get);
        return info.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    public ResponseEntity<Object> getDetailedInfo(String uuid) {
        logger.info("Received details request: " + uuid);
        RunningGame<G> game = runningGames.get(uuid);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(game.getGameDetails());
    }

    public ResponseEntity<ActionResult> action(String gameUUID, String authToken,
            String actionType, JsonNode jsonNode) {
        logger.info("Received action request of type '" + actionType + "' in game " + gameUUID + ": " + jsonNode);
        RunningGame<G> game = runningGames.get(gameUUID);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Optional<PlayerInGame> player = game.authorize(authToken);
        if (!player.isPresent()) {
            logger.warn("No player found with " + authToken + " in game " + game);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ActionResult(false, "Player in game not found"));
        }
        ActionResult result = helper.performAction(game, player.get(), actionType, jsonNode);
        if (result == null) {
            throw new NullPointerException("Perform action must return a response");
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<StartGameResponse> start(String game) {
        Optional<LobbyGame<G>> theGame = getLobbyGame(game);
        if (!theGame.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StartGameResponse(false));
        }
        synchronized (this) {
            lobbyGames.remove(game);
            RunningGame<G> running = theGame.get().startGame();
            runningGames.put(game, running);
        }
        return ResponseEntity.ok(new StartGameResponse(true));
    }

}
