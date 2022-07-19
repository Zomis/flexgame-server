package net.zomis.spring.games.impls.ur;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.impls.MyQLearning;

import java.util.Optional;
import java.util.stream.IntStream;

public abstract class RoyalAI implements RoyalGameOfUrAIs.AI {

    protected final MyQLearning.ActionPossible<RoyalGameOfUr> actionPossible = (environment, action) ->
            environment.canMove(environment.getCurrentPlayer(), action, environment.getRoll());

    private final JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
    private final ActionV2 rollAction = new ActionV2("roll", null);

    @Override
    public final Optional<ActionV2> control(RoyalGameOfUr game, PlayerInGame player) {
        if (game.getCurrentPlayer() != player.getIndex()) {
            return Optional.empty();
        }
        if (game.isRollTime()) {
            return Optional.of(rollAction);
        }
        if (!game.isMoveTime()) {
            return Optional.empty();
        }
        return Optional.of(new ActionV2("move", nodeFactory.numberNode(positionToMove(game))));
    }

    public abstract int positionToMove(RoyalGameOfUr game);

    final int[] getPossibleActions(RoyalGameOfUr game) {
        return IntStream.range(0, RoyalGameOfUr.EXIT).filter(i -> actionPossible.test(game, i)).toArray();
    }

}
