package net.zomis.spring.games.impls.ur;

import net.zomis.fight.ext.FightCollectors;
import net.zomis.fight.ext.WinResult;
import net.zomis.fight.ext.WinStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collector;
import java.util.stream.IntStream;

public class MonteCarloAI extends RoyalAI {
// MonteCarlo that plays with MonteCarlo that plays with KFE521T ?
    private static final Logger logger = LoggerFactory.getLogger(MonteCarloAI.class);
/*
* Best move questions
* [10, 14, 4, 0, 1, 0, 0], [1, 8, 2, 0, 0, 0, 0]], currentPlayer=1, roll=2
*
*
* */
    private final int fights;
    private final RoyalAI ai;

    public MonteCarloAI(int fights, RoyalAI ai) {
        this.fights = fights;
        this.ai = ai;
    }

    @Override
    public int positionToMove(RoyalGameOfUr game) {
        int[] possibleActions = getPossibleActions(game);
        if (possibleActions.length == 1) {
            return possibleActions[0];
        }

        double best = 0;
        int bestAction = -1;
        int me = game.getCurrentPlayer();
        for (int action : possibleActions) {
            RoyalGameOfUr copy = game.copy();
            copy.move(game.getCurrentPlayer(), action, game.getRoll());
            double expectedWin = fight(copy, me);
            logger.info("Action {} in state {} has {}", action, game, expectedWin);
            if (expectedWin > best) {
                bestAction = action;
                best = expectedWin;
            }
        }
        int aiResult = ai.positionToMove(game);
        if (aiResult != bestAction) {
            logger.warn("Monte Carlo returned different result than its simulation AI in state {}." +
                " AI {} - Monte Carlo {}", game, aiResult, bestAction);
        }
        return bestAction;
    }

    private double fight(RoyalGameOfUr game, int me) {
        Collector<WinResult, ?, WinStats> collector = FightCollectors.stats();
        return IntStream.range(0, this.fights)
            .parallel()
            .mapToObj(i -> singleFight(game.copy(), me))
            .collect(collector)
            .getPercentage();
    }

    private WinResult singleFight(RoyalGameOfUr game, int me) {
        while (!game.isFinished()) {
            while (game.isRollTime()) {
                game.roll();
            }

            int movePosition = ai.positionToMove(game);
            boolean allowed = game.move(game.getCurrentPlayer(), movePosition, game.getRoll());
            if (!allowed) {
                throw new IllegalStateException("Unexpected move: " + game.toCompactString() + ": " + movePosition);
            }
        }
        return WinResult.resultFor(game.getWinner(), me, -1);
    }

}
