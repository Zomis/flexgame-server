package net.zomis.spring.games;

import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;

public class RoyalGameOfUrMDP implements MDP<RoyalGameOfUr, Integer, DiscreteSpace> {

    private ArrayObservationSpace<RoyalGameOfUr> observationSpace = new ArrayObservationSpace<>(new int[] { 7 * 2 + 1 + 1 });
    private RoyalGameOfUr game;
    private DiscreteSpace actionSpace = new DiscreteSpace(7);
    private JSONObject emptyJson = new JSONObject("{}");

    @Override
    public ObservationSpace<RoyalGameOfUr> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public RoyalGameOfUr reset() {
        game = new RoyalGameOfUr();
        return game;
    }

    @Override
    public void close() {
        // Not needed
    }

    @Override
    public StepReply<RoyalGameOfUr> step(Integer action) {
        int me = game.getCurrentPlayer();
        while (game.isRollTime()) {
            game.roll();
        }
        int position = game.getPieces()[me][action];
        if (!game.canMove(me, position, game.getRoll())) {
            return new StepReply<>(game, -1000, false, emptyJson);
        }

        game.move(me, position, game.getRoll());
        double reward = me == game.getCurrentPlayer() ? 0.2 : -0.01;
        if (isDone()) {
            reward = game.getWinner() == me ? 10 : -10;
        }
        return new StepReply<>(game, reward, isDone(), emptyJson);
    }

    @Override
    public boolean isDone() {
        return game.isFinished();
    }

    @Override
    public RoyalGameOfUrMDP newInstance() {
        return new RoyalGameOfUrMDP();
    }

}
