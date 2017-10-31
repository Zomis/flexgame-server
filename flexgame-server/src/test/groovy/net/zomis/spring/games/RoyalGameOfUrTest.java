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

    @Test
    public void fight() {
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
