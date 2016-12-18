package net.zomis.spring.games.generic;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroovyGames {

    private final Map<String, GameRestDelegate> games = new HashMap<>();

    public Map<String, GameRestDelegate> getGames() {
        return new HashMap<>(games);
    }

    public void initialize(Reader resource) throws URISyntaxException, IOException {
        if (resource == null) {
            throw new NullPointerException("resource not found");
        }
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell shell = new GroovyShell(cc);
        DelegatingScript script = (DelegatingScript) shell.parse(resource);
        script.setDelegate(this);
        script.run();
    }

    public void game(String name, Class<?> gameClass, Closure<?> gameClosure) {
        GroovyGameDelegate delegate = new GroovyGameDelegate();
        gameClosure.setDelegate(delegate);
        gameClosure.call();
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

        GroovyGameDelegate players(int min) {
            this.minPlayers = min;
            return this;
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
        }

        public void gameInfo(Closure<?> closure) {
            this.gameInfo = closure;
        }

    }

    private static class ActionsDelegate {

        private final List<GroovyAction> actions = new ArrayList<>();

        public void action(String name, Class<?> parameter, Closure<?> perform) {
            this.actions.add(new GroovyAction(name, parameter, perform));
        }

        public void action(String name, Closure<?> perform) {
            this.action(name, void.class, perform);
        }

    }

    private static class GroovyAction {
        private final String name;
        private final Class<?> parameter;
        private final Closure<?> perform;

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
