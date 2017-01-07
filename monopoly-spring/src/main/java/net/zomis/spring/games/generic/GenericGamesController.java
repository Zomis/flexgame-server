package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.zomis.spring.games.messages.*;
import net.zomis.spring.games.messages.JoinGameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("/games")
public class GenericGamesController implements InitializingBean {

    @Autowired
    private GroovyGames games;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final Logger logger = LoggerFactory.getLogger(GenericGamesController.class);

    public GroovyGames getGames() {
        return games;
    }

    private GameRestDelegate delegate(String gameType) {
        GameRestDelegate delegate = games.getGame(gameType);
        if (delegate == null) {
            // Return bad request?
            throw new IllegalArgumentException("No such game type: " + gameType +
                ", available game types are " + games.getGames().keySet());
        }
        return delegate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        games.initialize(new InputStreamReader(resourceLoader.getResource("classpath:monopoly.groovy").getInputStream()));
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
        return games.getGames().keySet();
    }

    @RequestMapping(value = "/{gameType}", method = RequestMethod.GET)
    public GameList listGames(@PathVariable("gameType") String gameType) {
        return delegate(gameType).listGames();
    }

    @RequestMapping(value = "/{gameType}", method = RequestMethod.POST)
    public ResponseEntity<CreateGameResponse> startNewGame(@PathVariable("gameType") String gameType, @RequestBody CreateGameRequest request) {
        return delegate(gameType).startNewGame(request);
    }

    @RequestMapping(value = "/{gameType}/{game}/join")
    public ResponseEntity<JoinGameResponse> joinGame(@PathVariable("gameType") String gameType, @PathVariable("game") String gameID, @RequestBody JoinGameRequest request) {
        return delegate(gameType).joinGame(gameID, request);
    }

    @RequestMapping(value = "/{gameType}/{game}/details", method = RequestMethod.GET)
    public ResponseEntity<Object> details(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).getDetailedInfo(game);
    }

    @RequestMapping(value = "/{gameType}/{game}/start", method = RequestMethod.POST)
    public ResponseEntity<StartGameResponse> start(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).start(game);
    }

    @RequestMapping(value = "/{gameType}/{game}", method = RequestMethod.GET)
    public ResponseEntity<GameInfo> summary(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).summary(game);
    }

    @RequestMapping(value = "/{gameType}/{game}/actions/{type}", method = RequestMethod.POST)
    public ResponseEntity<GameMoveResult> action(@PathVariable("gameType") String gameType, @PathVariable("game") String game,
             @PathVariable("type") String type,
             @RequestParam("token") String authToken, @RequestBody JsonNode action) {
        ResponseEntity<GameMoveResult> result = delegate(gameType).action(game, authToken, type, action);
        logger.info("Action result: " + result);
        return result;
    }

}
