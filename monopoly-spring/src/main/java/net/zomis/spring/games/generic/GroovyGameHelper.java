package net.zomis.spring.games.generic;

import net.zomis.spring.games.messages.GameMoveResult;

import java.util.function.Function;

public class GroovyGameHelper implements GameHelper<Object, Object> {

    public Function<Object, Object> constructor;

    @Override
    public Object constructGame(Object configuration) {
        return constructor.apply(configuration);
    }

    @Override
    public void addPlayer(Object playerConfig) {

    }

    @Override
    public Object start() {
        return null;
    }

    @Override
    public GameMoveResult performAction(int playerIndex, Object action) {
        return null;
    }

    @Override
    public Object gameDetails(Object game) {
        return null;
    }

}
