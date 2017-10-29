package net.zomis.spring.games;

import net.zomis.aiscores.FieldScoreProducer;
import net.zomis.aiscores.FieldScores;
import net.zomis.fight.FightInterface;
import net.zomis.fight.FightResults;
import net.zomis.fight.GameFight;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrHelper;
import org.junit.Test;

import java.util.*;

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
    public void fight() {
        GameFight<RoyalGameOfUrAIs.AI> fight = new GameFight<>("UR");
        RoyalGameOfUrAIs.AI[] ais = new RoyalGameOfUrAIs.AI[] {
                new RoyalGameOfUrAIs.URScorer("KnockoutFlowerOld", RoyalGameOfUrAIs.scf().withScorer(knockoutBuggy).withScorer(gotoFlower)),
                new RoyalGameOfUrAIs.URScorer("KFE521 Safety", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521S2", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521S3", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KnockoutFlowerExit521", RoyalGameOfUrAIs.scf().withScorer(knockout, 5).withScorer(gotoFlower, 2).withScorer(exit)),
        };
        RoyalGameOfUrHelper helper = new RoyalGameOfUrHelper();
        FightInterface<RoyalGameOfUrAIs.AI> strat = new FightInterface<RoyalGameOfUrAIs.AI>() {
            @Override
            public RoyalGameOfUrAIs.AI determineWinner(RoyalGameOfUrAIs.AI[] players, int fightNumber) {
                RoyalGameOfUr ur = new RoyalGameOfUr();
                while (!ur.isFinished()) {
                    RoyalGameOfUrAIs.AI player = players[ur.getCurrentPlayer()];
                    Optional<ActionV2> result = player.control(ur, new PlayerInGame("", ur.getCurrentPlayer(), "", null, player));
                    // System.out.printf("Result for %d: %s. Values %s", ur.getCurrentPlayer(), result.orElse(null), Arrays.deepToString(ur.getPieces()));
                    result.ifPresent(act -> helper.performAction(ur, ur.getCurrentPlayer(), act.getName(), act.getActionData()));
                }

                return players[ur.getWinner()];
            }
        };
        FightResults<RoyalGameOfUrAIs.AI> results = fight.fightEvenly(ais, 1000, strat);
        System.out.println(results.toStringMultiLine());
    }

}
