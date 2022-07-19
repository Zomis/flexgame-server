package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.generic.v2.PlayerController;
import net.zomis.spring.games.impls.qlearn.TTTQLearn;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;

import java.util.*;
import java.util.function.Supplier;

import static net.zomis.spring.games.impls.qlearn.TTTQLearn.isDraw;
import static net.zomis.spring.games.impls.qlearn.TTTQLearn.performAction;

public class ScoredTTT {

    private int[] score;
    private TTController game;
    private final Supplier<TTController> supplier;

    public ScoredTTT(Supplier<TTController> supplier) {
        this.supplier = supplier;
        this.game = supplier.get();
        this.score = new int[2];
    }

    public static ScoredTTT of(Supplier<TTController> supplier) {
        return new ScoredTTT(supplier);
    }

    public TTController getCurrent() {
        return game;
    }

    public void postAction() {
        if (game.isGameOver() && game.getWonBy().isExactlyOnePlayer()) {
            int index = game.getWonBy().is(TTPlayer.X) ? 0 : 1;
            score[index]++;
            game = supplier.get();
        }
        if (isDraw(game)) {
            game = supplier.get();
        }
    }

    public int[] getScore() {
        return Arrays.copyOf(score, score.length);
    }

    public static class ScoredTTTQLearn implements PlayerController<ScoredTTT>, Queryable<ScoredTTT> {

        public ScoredTTTQLearn(MyQLearning<TTController, String> learner) {
            this.learner = learner;
        }

        private final MyQLearning<TTController, String> learner;
        private final JsonNodeFactory nodeFactory = new JsonNodeFactory(false);

        @Override
        public Optional<ActionV2> control(ScoredTTT scored, PlayerInGame player) {
            TTController game = scored.getCurrent();
            if (game.getCurrentPlayer() == TTPlayer.X ^ player.getIndex() == 0) {
                return Optional.empty();
            }
            if (game.isGameOver() || TTTQLearn.isDraw(game)) {
                return Optional.empty();
            }
            int action = learner.pickWeightedBestAction(game);
            ActionV2 actionV2 = actionToActionV2(action);
            return Optional.of(actionV2);
        }

        private ActionV2 actionToActionV2(int action) {
            int x = action % 3;
            int y = action / 3;
            ObjectNode obj = nodeFactory.objectNode();
            obj.set("x", nodeFactory.numberNode(x));
            obj.set("y", nodeFactory.numberNode(y));
            return new ActionV2("move", obj);
        }

        public void perform(TTController game, ActionV2 action) {
            int x = action.getActionData().get("x").asInt();
            int y = action.getActionData().get("y").asInt();
            learner.step(game, performAction, y * 3 + x);
        }

        @Override
        public Collection<ActionScore> query(ScoredTTT scored) {
            TTController current = scored.getCurrent();
            double[] value = learner.getActionScores(current);
            Collection<ActionScore> result = new ArrayList<>();
            for (int i = 0; i < learner.getMaxActions(); i++) {
                if (learner.isActionPossible(current, i)) {
                    result.add(new ActionScore(actionToActionV2(i), value[i]));
                }
            }
            return result;
        }
    }

}
