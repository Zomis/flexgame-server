package net.zomis.spring.games.impls.qlearn;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.generic.v2.PlayerController;
import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;

import java.util.Optional;
import java.util.function.Function;

public class TTTQLearn implements PlayerController<TTController> {

    public static final MyQLearning.PerformAction<TTController> performAction = (tt, action) -> {
        int x = action % 3;
        int y = action / 3;
        TTPlayer player = tt.getCurrentPlayer();
        tt.play(tt.getGame().getSub(x, y));
        double reward = 0;

        if (tt.isGameOver() && tt.getWonBy().isExactlyOnePlayer()) {
            reward = tt.getWonBy().is(player) ? 1 : -1;
        } else if (isDraw(tt)) {
            reward = 0;
        } else {
            reward = -0.01;
        }
        return new MyQLearning.Rewarded<>(tt, reward);
    };
    public static final MyQLearning.ActionPossible<TTController> actionPossible = (tt, action) -> {
        int x = action % 3;
        int y = action / 3;
        if (tt.getGame().getSub(x, y) == null) {
            return false;
        }
        return tt.isAllowedPlay(tt.getGame().getSub(x, y));
    };

    public static MyQLearning<TTController, String> newLearner(QStore<String> qStore) {
        int sizeX = 3;
        int sizeY = 3;

        Function<TTController, String> stateToString = g -> {
            StringBuilder str = new StringBuilder();
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    TTBase sub = g.getGame().getSub(x, y);
                    str.append(sub.getWonBy().isExactlyOnePlayer() ? sub.getWonBy().name() : "_");
                }
                str.append('-');
            }
            return str.toString();
        };
        MyQLearning<TTController, String> learn = new MyQLearning<>(9, stateToString, actionPossible,
            (state, action) -> state + action, qStore);
        // learn.setLearningRate(-0.01); // This leads to bad player moves. Like XOX-OXO-_X_ instead of XOX-OXO-X__
        learn.setDiscountFactor(-0.9);
        learn.setLearningRate(1.0);
        learn.setRandomMoveProbability(1.0);
        return learn;
    }

    private static boolean isDraw(TTController tt) {
        for (int yy = 0; yy < tt.getGame().getSizeY(); yy++) {
            for (int xx = 0; xx < tt.getGame().getSizeX(); xx++) {
                TTBase sub = tt.getGame().getSub(xx, yy);
                if (!sub.isWon()) {
                    return false;
                }
            }
        }
        return true;
    }

    public TTTQLearn(MyQLearning<TTController, String> learner) {
        this.learner = learner;
    }

    private final MyQLearning<TTController, String> learner;
    private final JsonNodeFactory nodeFactory = new JsonNodeFactory(false);

    @Override
    public Optional<ActionV2> control(TTController game, PlayerInGame player) {
        if (game.getCurrentPlayer() == TTPlayer.X ^ player.getIndex() == 0) {
            return Optional.empty();
        }
        if (game.isGameOver() || isDraw(game)) {
            return Optional.empty();
        }
        int action = learner.pickAction(game);
        int x = action % 3;
        int y = action / 3;
        ObjectNode obj = nodeFactory.objectNode();
        obj.set("x", nodeFactory.numberNode(x));
        obj.set("y", nodeFactory.numberNode(y));
        return Optional.of(new ActionV2("move", obj));
    }

    public void perform(TTController game, ActionV2 action) {
        int x = action.getActionData().get("x").asInt();
        int y = action.getActionData().get("y").asInt();
        learner.step(game, performAction, y * 3 + x);
    }
}
