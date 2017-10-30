package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import net.zomis.spring.games.generic.v2.ActionResult;
import net.zomis.spring.games.generic.v2.DatabaseInterface;
import net.zomis.spring.games.generic.v2.GameHelper2;
import net.zomis.spring.games.generic.v2.GameRestDelegate2;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrHelper;
import net.zomis.spring.games.impls.TTTGameH;
import net.zomis.spring.games.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/games2")
public class GenericGamesController2 implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GenericGamesController2.class);

    private Map<String, GameRestDelegate2<?>> games = new ConcurrentHashMap<>();
    private final TokenGenerator generator = new TokenGenerator();

    @Autowired
    private MongoClient client;

    public <G> void addGame(String name, DatabaseInterface<G> db, GameHelper2<G> helper) {
        this.games.put(name, new GameRestDelegate2<>(db, helper, generator));
    }

    private GameRestDelegate2<?> delegate(String gameType) {
        GameRestDelegate2<?> delegate = games.get(gameType);
        if (delegate == null) {
            // Return bad request?
            throw new IllegalArgumentException("No such game type: " + gameType +
                ", available game types are " + games.keySet());
        }
        return delegate;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(@RequestBody JsonNode data) {
        return data.toString();
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public JsonNode test(@RequestParam("name") String name) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("name", name);
        return new ObjectMapper().valueToTree(map);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Set<String> gameTypes() {
        return games.keySet();
    }

    @RequestMapping(value = "/{gameType}", method = RequestMethod.GET)
    public GameList listGames(@PathVariable("gameType") String gameType) {
        return delegate(gameType).listGames();
    }

    @RequestMapping(value = "/{gameType}", method = RequestMethod.POST)
    public ResponseEntity<CreateGameResponse> startNewGame(@PathVariable("gameType") String gameType, @RequestBody CreateGameRequest request) {
        return delegate(gameType).startNewGame(request);
    }

    @RequestMapping(value = "/{gameType}/{game}/join", method = RequestMethod.POST)
    public ResponseEntity<JoinGameResponse> joinGame(@PathVariable("gameType") String gameType, @PathVariable("game") String gameID, @RequestBody JoinGameRequest request) {
        return delegate(gameType).joinGame(gameID, request);
    }

    @RequestMapping(value = "/{gameType}/{game}/details", method = RequestMethod.GET)
    public ResponseEntity<Object> details(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).getDetailedInfo(game);
    }

    @RequestMapping(value = "/{gameType}/{game}/start", method = RequestMethod.POST)
    public ResponseEntity<StartGameResponse> start(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        // TODO: Also needs privateKey for player who created game
        return delegate(gameType).start(game);
    }

    @RequestMapping(value = "/{gameType}/{game}", method = RequestMethod.GET)
    public ResponseEntity<GameInfo> summary(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).summary(game);
    }

    @RequestMapping(value = "/{gameType}/{game}/ai", method = RequestMethod.POST)
    public ResponseEntity<ActionResult> addAI(@PathVariable("gameType") String gameType, @PathVariable("game") String game,
             @RequestBody AIInvite aiInvite) {
        return delegate(gameType).addAI(game, aiInvite);
    }

    @RequestMapping(value = "/{gameType}/{game}/aiMove", method = RequestMethod.GET)
    public ResponseEntity<ActionResult> aiMove(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).aiMove(game);
    }

    @RequestMapping(value = "/{gameType}/{game}/actions/{type}", method = RequestMethod.POST)
    public ResponseEntity<ActionResult> action(@PathVariable("gameType") String gameType, @PathVariable("game") String game,
             @PathVariable("type") String type,
             @RequestParam("token") String authToken, @RequestBody JsonNode action) {
        return delegate(gameType).action(game, authToken, type, action);
    }

    @Override
    public void afterPropertiesSet() {
        addGame("ttt", new DBMongo<>(client), new TTTGameH());
        addGame("ur", new DBMongo<>(client), new RoyalGameOfUrHelper());
    }
}
