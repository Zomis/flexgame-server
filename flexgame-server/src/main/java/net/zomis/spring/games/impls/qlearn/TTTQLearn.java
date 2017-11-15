package net.zomis.spring.games.impls.qlearn;

import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;

import java.util.function.Function;

public class TTTQLearn {

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

    public static boolean isDraw(TTController tt) {
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

}
