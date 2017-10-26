package net.zomis.spring.games.generic;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.zomis.spring.games.messages.GameMoveResult;

import java.util.Map;
import java.util.function.Function;

@Deprecated
public class GroovyGameHelper implements GameHelper<Object, Object> {

    public Function<Object, Object> constructor;
    public Function<Object, Object> details;
    public Map<String, GroovyGames.GroovyAction> actions;


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
    public GameMoveResult performAction(PlayerInGame playerInGame, String actionType, TreeNode data) {
        GroovyGames.GroovyAction action = actions.get(actionType);
        Object actionData = new ObjectMapper().convertValue(data, action.parameter);
        action.perform.call(new Action(null, playerInGame, actionData));
        return new GameMoveResult("ok");
    }

    @Override
    public Object gameDetails(Object game) {
        return details.apply(game);
    }

}
