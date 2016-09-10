package net.zomis.spring.games.monopoly;

import net.zomis.monopoly.model.GameSetup;
import net.zomis.monopoly.model.actions.RollDiceAction;
import net.zomis.spring.games.messages.*;
import net.zomis.spring.games.monopoly.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oldgames")
public class GamesController {

//    private static final Logger logger = LoggerFactory.getLogger(GamesController.class);
//
//    private final Map<UUID, ServerGame> games = new ConcurrentHashMap<>();
//
//    private Optional<ServerGame> getGame(UUID uuid) {
//        if (games.containsKey(uuid)) {
//            return Optional.of(games.get(uuid));
//        }
//        return Optional.empty();
//    }
//
//    @RequestMapping(method = RequestMethod.GET)
//    public GameList listGames() {
//        return new GameList(games.values().stream().map(ServerGame::getGameInfo).collect(Collectors.toList()));
//    }
//
//    @RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<JoinGameResponse> startNewGame(@RequestBody StartGameRequest request) {
//        logger.info("Received start game request: " + request);
//        if (!request.isSpeedDie()) {
//            return ResponseEntity.status(418).body(null);
//        }
//        GameSetup setup = new GameSetup().withSpeedDie(request.isSpeedDie());
//        ServerGame game = new ServerGame(setup.create());
//        games.put(game.getUUID(), game);
//        return game.addPlayer(request.getPlayerName(), request.getPiece());
//    }
//
//    @RequestMapping(value = "/{uuid}/join", method = RequestMethod.POST)
//    public ResponseEntity<JoinGameResponse> joinGame(@PathParam("uuid") String uuid, @RequestBody JoinGameRequest request) {
//        logger.info("Received join game request: " + request);
//        Optional<ServerGame> game = getGame(UUID.fromString(uuid));
//        if (!game.isPresent()) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        return game.get().addPlayer(request.getPlayerName(), request.getPiece());
//    }
//
//    @RequestMapping(value = "/{uuid}/summary", method = RequestMethod.GET)
//    public ResponseEntity<GameInfo> summary(@PathParam("uuid") String uuid, @RequestBody JoinGameRequest request) {
//        logger.info("Received summary request: " + request);
//        Optional<ServerGame> game = getGame(UUID.fromString(uuid));
//        if (!game.isPresent()) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        return ResponseEntity.ok(game.get().getGameInfo());
//    }
//
//    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
//    public ResponseEntity<GameDetails> getDetailedInfo(@PathParam("uuid") String uuid) {
//        logger.info("Received details request: " + uuid);
//        Optional<ServerGame> game = getGame(UUID.fromString(uuid));
//        if (!game.isPresent()) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        return ResponseEntity.ok(game.get().getGameDetails());
//    }
//
//    @RequestMapping(value = "/{gameUUID}/player/{playerUUID}/roll", method = RequestMethod.GET)
//    public ResponseEntity<GameMoveResult> roll(@PathParam("gameUUID") String gameUUID,
//            @PathParam("playerUUID") String playerUUID) {
//        logger.info("Received roll request: " + playerUUID + " in game " + gameUUID);
//        Optional<ServerGame> game = getGame(UUID.fromString(gameUUID));
//        if (!game.isPresent()) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        Optional<ServerGamePlayer> player = game.get().authorize(playerUUID);
//        if (!player.isPresent()) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        return player.get().makeMove(RollDiceAction.roll(game.get().getRandom()));
//    }
//
//    /*
//    *
//    * more tests
//    *
//    * implement buying properties, wait with auctions
//    * implement building houses
//    * implement paying rent
//    *
//    * rewrite to ECS ?
//    **/

}