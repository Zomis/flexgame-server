package net.zomis.spring.games;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.aiscores.*;
import net.zomis.aiscores.extra.ParamAndField;
import net.zomis.aiscores.extra.ScoreUtils;
import net.zomis.aiscores.scorers.ScoreInterface;
import net.zomis.aiscores.scorers.SimpleScorer;
import net.zomis.fight.FightInterface;
import net.zomis.fight.FightResults;
import net.zomis.fight.GameFight;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.generic.v2.PlayerController;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrHelper;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.exit;
import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.gotoFlower;
import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.knockout;
import static org.junit.Assert.assertFalse;

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
                new RoyalGameOfUrAIs.URScorer("Idiot", RoyalGameOfUrAIs.scf()),
                new RoyalGameOfUrAIs.URScorer("Knockout", RoyalGameOfUrAIs.scf().withScorer(knockout)),
                new RoyalGameOfUrAIs.URScorer("KnockoutFlower", RoyalGameOfUrAIs.scf().withScorer(knockout).withScorer(gotoFlower)),
                new RoyalGameOfUrAIs.URScorer("KnockoutFlowerExit", RoyalGameOfUrAIs.scf().withScorer(knockout).withScorer(gotoFlower).withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("FlowerExit", RoyalGameOfUrAIs.scf().withScorer(gotoFlower).withScorer(exit)),
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
        FightResults<RoyalGameOfUrAIs.AI> results = fight.fightEvenly(ais, 50, strat);
        System.out.println(results.toStringMultiLine());
    }

}
