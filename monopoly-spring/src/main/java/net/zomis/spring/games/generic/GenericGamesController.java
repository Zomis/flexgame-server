package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.zomis.spring.games.messages.*;
import net.zomis.spring.games.monopoly.messages.JoinGameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/games")
public class GenericGamesController implements InitializingBean {

    @Autowired
    private GroovyGames games;

    @Autowired
    private ResourceLoader resourceLoader;

    private final Map<String, GameRestDelegate> gameTypes = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(GenericGamesController.class);

    private GameRestDelegate delegate(String gameType) {
        GameRestDelegate delegate = gameTypes.get(gameType);
        if (delegate == null) {
            // Return bad request?
            throw new IllegalArgumentException("No such game type: " + gameType + ", available game types are " + gameTypes.keySet());
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

    @RequestMapping(value = "/{gameType}/", method = RequestMethod.GET)
    public GameList listGames(@PathVariable("gameType") String gameType) {
        return delegate(gameType).listGames();
    }

    @RequestMapping(value = "/{gameType}/", method = RequestMethod.POST)
    public ResponseEntity<CreateGameResponse> startNewGame(@PathVariable("gameType") String gameType, StartGameRequest request) {
        return delegate(gameType).startNewGame(request);
    }

    @RequestMapping(value = "/{gameType}/{game}/join")
    public ResponseEntity<JoinGameResponse> joinGame(@PathVariable("gameType") String gameType, @PathVariable("game") String gameID, @RequestBody JoinGameRequest request) {
        return delegate(gameType).joinGame(gameID, request);
    }

    @RequestMapping(value = "/{gameType}/{game}/summary", method = RequestMethod.GET)
    public ResponseEntity<GameInfo> summary(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).summary(game);
    }

    @RequestMapping(value = "/{gameType}/{game}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDetailedInfo(@PathVariable("gameType") String gameType, @PathVariable("game") String game) {
        return delegate(gameType).getDetailedInfo(game);
    }

    @RequestMapping(value = "/{gameType}/{game}/actions/{type}", method = RequestMethod.POST)
    public ResponseEntity<GameMoveResult> action(@PathVariable("gameType") String gameType, @PathVariable("game") String game,
             @PathVariable("type") String type,
             @RequestParam("token") String authToken, @RequestBody JsonNode action) {
        return delegate(gameType).action(game, authToken, type, action);
    }

}
