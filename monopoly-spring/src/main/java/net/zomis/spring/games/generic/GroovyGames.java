package net.zomis.spring.games.generic;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroovyGames {

    private static final Logger logger = LoggerFactory.getLogger(GroovyGames.class);

    private final Map<String, GameRestDelegate> games = new HashMap<>();

    public Map<String, GameRestDelegate> getGames() {
        return new HashMap<>(games);
    }

    public void initialize(Reader resource) throws URISyntaxException, IOException {
        logger.info("Initializing from reader: " + resource);
        if (resource == null) {
            throw new NullPointerException("resource not found");
        }
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);
        DelegatingScript script = (DelegatingScript) shell.parse(resource);
        script.setDelegate(this);
        script.run();
        logger.info("Games available after initialization: " + games.keySet());
    }

    public void game(String name, Class<?> gameClass, Closure<?> gameClosure) {
        logger.info("game: " + name + " with game class " + gameClass);
        GroovyGameDelegate delegate = new GroovyGameDelegate();
        gameClosure.setDelegate(delegate);
        gameClosure.call();
        GameHelper helper = delegate.helper;
        games.put(name, new GameRestDelegate(helper, new TokenGenerator()));
        logger.info("game '" + name + "' resulted in " + delegate);
    }

    public GameRestDelegate getGame(String name) {
        return this.games.get(name);
    }

    private static class GroovyGameDelegate {
        private int minPlayers;
        private int maxPlayers;
        private Class<?> gameConfigClass;
        private Class<?> playerConfigClass;
        private Closure<?> gameInfo;
        private GroovyGameHelper helper = new GroovyGameHelper();

        GroovyGameDelegate players(int count) {
            this.minPlayers = count;
            this.maxPlayers = count;
            return this;
        }

        public void setup(Class<?> gameConfigClass, Closure<?> closure) {
            this.gameConfigClass = gameConfigClass;
            helper.constructor = closure::call;
        }

        public void playerType(Class<?> playerConfigClass) {
            this.playerConfigClass = playerConfigClass;
        }

        public void to(int max) {
            this.maxPlayers = max;
        }

        public void config(Class<?> gameConfigClass) {
            this.gameConfigClass = gameConfigClass;
        }

        public void playerConfig(Class<?> playerConfigClass) {
            this.playerConfigClass = playerConfigClass;
        }

        public void actions(Closure<?> closure) {
            ActionsDelegate delegate = new ActionsDelegate();
            closure.setDelegate(delegate);
            closure.call();
            helper.actions = delegate.actions;
        }

        public void gameInfo(Closure<?> closure) {
            this.gameInfo = closure;
            helper.details = game -> {
                System.out.println("game is " + game);
                Closure<?> buildClosure = (Closure<?>) closure.call(game);

                JsonBuilder builder = new JsonBuilder();
                builder.call(buildClosure);

                String result = builder.toPrettyString();
                System.out.println(result);
                JsonSlurper slurper = new JsonSlurper();
                return slurper.parseText(result);
            };
        }

    }

    private static class ActionsDelegate {

        private final Map<String, GroovyAction> actions = new HashMap<>();

        public void action(String name, Class<?> parameter, Closure<?> perform) {
            this.actions.put(name, new GroovyAction(name, parameter, perform));
        }

        public void action(String name, Closure<?> perform) {
            this.action(name, void.class, perform);
        }

    }

    public static class GroovyAction {
        public final String name;
        public final Class<?> parameter;
        public final Closure<?> perform;

        public GroovyAction(String name, Class<?> parameter, Closure<?> perform) {
            this.name = name;
            this.parameter = parameter;
            this.perform = perform;
        }

    }

    @Override
    public String toString() {
        return games.toString();
    }
}
