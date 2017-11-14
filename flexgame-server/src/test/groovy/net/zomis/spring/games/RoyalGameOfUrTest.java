package net.zomis.spring.games;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.aiscores.FieldScoreProducer;
import net.zomis.aiscores.FieldScores;
import net.zomis.fight.FightInterface;
import net.zomis.fight.FightResults;
import net.zomis.fight.GameFight;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrHelper;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class RoyalGameOfUrTest {

    @Test
    public void cannotMoveSkipTurn() {
        RoyalGameOfUr ur = setup(0, 2, new int[][]{{6, 15, 15, 2, 4, 0, 0}, {15, 4, 8, 13, 1, 0, 0}});
        assertFalse(ur.canMove(2));
    }

    private RoyalGameOfUr setup(int currentPlayer, int currentRoll, int[][] positions) {
        return new RoyalGameOfUr(currentPlayer, currentRoll, positions);
    }

    @Test
    public void aiTest() {
        RoyalGameOfUr ur = setup(1, 2, new int[][]{{15, 15, 15, 15, 11, 3, 2}, {15, 9, 7, 4, 3, 0, 0}});
        URScorer ai = new URScorer("KnockoutFlower", RoyalGameOfUrAIs.scf().withScorer(knockout).withScorer(gotoFlower));
        FieldScores<RoyalGameOfUr, Integer> scores = ai.getProducer().analyzeAndScore(ur);
        System.out.println(scores.getScores());
        Optional<ActionV2> move = ai.control(ur, new PlayerInGame("", 1, "", null, ai));
        assertEquals(9, move.get().getActionData().asInt());

        ur = setup(1, 3, new int[][]{{15, 4, 0, 0, 0, 0, 0}, {10, 8, 6, 0, 0, 0, 0}});
        ai = new RoyalGameOfUrAIs.URScorer("KFE521S3", RoyalGameOfUrAIs.scf()
                .withScorer(knockout, 5)
                .withScorer(gotoFlower, 2)
                .withScorer(gotoSafety, 0.1)
                .withScorer(leaveSafety, -0.1)
                .withScorer(riskOfBeingTaken, -0.1)
                .withScorer(riskOfBeingTakenHere, 0.5)
                .withScorer(exit));

        Arrays.stream(RoyalGameOfUrAIs.class.getFields()).forEach(f -> {
            try {
                System.out.println(f.getName() + ": " + f.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        FieldScoreProducer<RoyalGameOfUr, Integer> producer = ai.getProducer();
        producer.setDetailed(true);
        scores = producer.analyzeAndScore(ur);
        scores.getScores().forEach((key, value) -> {
            System.out.println(key + "= " + value.getScore() + " : " + value.getScoreMap());
        });
        System.out.println(scores.getScores());
        move = ai.control(ur, new PlayerInGame("", 1, "", null, ai));
        assertEquals(6, move.get().getActionData().asInt());
    }

    public static void main(String[] args) {
        RoyalGameOfUrHelper helper = new RoyalGameOfUrHelper();
    }

    private static class RoyalQLearn implements AI {

        private final MyQLearning<RoyalGameOfUr, Long> learn;

        public RoyalQLearn(MyQLearning<RoyalGameOfUr, Long> learn) {
            this.learn = learn;
        }

        @Override
        public Optional<ActionV2> control(RoyalGameOfUr game, PlayerInGame player) {
            if (game.getCurrentPlayer() != player.getIndex()) {
                return Optional.empty();
            }
            if (game.isRollTime()) {
                return Optional.of(new ActionV2("roll", null));
            }
            if (!game.isMoveTime()) {
                return Optional.empty();
            }

            int action = learn.pickAction(game);
            return Optional.of(new ActionV2("move", new JsonNodeFactory(false).numberNode(action)));
        }

    }


    @Test
    public void fight() {
        MyQLearning.ActionPossible<RoyalGameOfUr> actionPossible = (environment, action) ->
            environment.canMove(environment.getCurrentPlayer(), action, environment.getRoll());
        MyQLearning<RoyalGameOfUr, Long> learn = new MyQLearning<>(15, RoyalGameOfUr::toLong, actionPossible, (state, action) -> state << 4 + action);
        learn.setLearningRate(0.2);
        learn.setDiscountFactor(0.99);
        learn.setRandomMoveProbability(0.1);
        RoyalQLearn royalQ = new RoyalQLearn(learn);

        GameFight<RoyalGameOfUrAIs.AI> fight = new GameFight<>("UR");
        RoyalGameOfUrAIs.AI[] ais = new RoyalGameOfUrAIs.AI[] {
                new RoyalGameOfUrAIs.URScorer("KFE521S3", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521T", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521S3C2", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(riskOfBeingTakenHere, 0.5)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521S3C", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(riskOfBeingTaken, -3)
                        .withScorer(exit)),
                royalQ,
        };
        RoyalGameOfUrHelper helper = new RoyalGameOfUrHelper();
        JsonNodeFactory factory = new JsonNodeFactory(false);
        MyQLearning.PerformAction<RoyalGameOfUr> performAction = (state, action) -> {
            int currentPlayer = state.getCurrentPlayer();
            helper.performAction(state, state.getCurrentPlayer(), "move", factory.numberNode(action));
            while (!state.isFinished() && state.isRollTime()) {
                state.roll();
            }
            int nextPlayer = state.getCurrentPlayer();

            double reward = state.isFinished() ? state.getWinner() == currentPlayer ? 10 : -10 : -0.01;
            double discountFactor = currentPlayer == nextPlayer ? learn.getDiscountFactor() : learn.getDiscountFactor() * -1;
            return new MyQLearning.Rewarded<>(state, reward).withDiscountFactor(discountFactor);
        };
        FightInterface<RoyalGameOfUrAIs.AI> strat = new FightInterface<RoyalGameOfUrAIs.AI>() {
            @Override
            public RoyalGameOfUrAIs.AI determineWinner(RoyalGameOfUrAIs.AI[] players, int fightNumber) {
                RoyalGameOfUr ur = new RoyalGameOfUr();
                while (ur.isRollTime()) {
                    ur.roll();
                }
                while (!ur.isFinished()) {
                    if (!ur.isMoveTime()) {
                        throw new IllegalStateException("NOT MOVE TIME!");
                    }

                    RoyalGameOfUrAIs.AI player = players[ur.getCurrentPlayer()];
                    Optional<ActionV2> result = player.control(ur, new PlayerInGame("", ur.getCurrentPlayer(), "", null, player));
                    // System.out.printf("Result for %d: %s. Values %s", ur.getCurrentPlayer(), result.orElse(null), Arrays.deepToString(ur.getPieces()));
                    result.ifPresent(act -> learn.step(ur, performAction, act.getActionData().asInt()));
                }
                return players[ur.getWinner()];
            }
        };

        int lastSize;
        do {
            lastSize = learn.getQTableSize();
            FightResults<RoyalGameOfUrAIs.AI> results = fight.fightEvenly(ais, 1000, strat);
            System.out.println(results.toStringMultiLine());
            System.out.println("States: " + learn.getQTableSize());

            int newStates = learn.getQTableSize() - lastSize;
            System.out.println(newStates + " new states");
        } while (lastSize != learn.getQTableSize());
    }

}
