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
import net.zomis.spring.games.impls.RoyalGameOfUr;
import net.zomis.spring.games.impls.RoyalGameOfUrHelper;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    interface AI extends PlayerController<RoyalGameOfUr> {}

    private static final Collection<Integer> fields = IntStream.range(0, 7).mapToObj(i -> i).collect(Collectors.toList());
    private static final ScoreStrategy<RoyalGameOfUr, Integer> scoreStrategy = new ScoreStrategy<RoyalGameOfUr, Integer>() {

        @Override
        public Collection<Integer> getFieldsToScore(RoyalGameOfUr ai) {
            return fields;
        }

        @Override
        public boolean canScoreField(ScoreParameters<RoyalGameOfUr> scoreParameters, Integer pieceIndex) {
            RoyalGameOfUr ur = scoreParameters.getParameters();
            int currentPlayer = scoreParameters.getParameters().getCurrentPlayer();
            int position = ur.getPieces()[currentPlayer][pieceIndex];
            return scoreParameters.getParameters().canMove(currentPlayer, position, ur.getRoll());
        }
    };
    private class URScorer implements AI {

        private final String name;
        FieldScoreProducer<RoyalGameOfUr, Integer> producer;
        public URScorer(String name, List<ScoreInterface<RoyalGameOfUr, Integer>> scorers) {
            List<SimpleScorer<RoyalGameOfUr, Integer>> fscorers = scorers.stream().map(SimpleScorer::new).collect(Collectors.toList());
            ScoreConfigFactory<RoyalGameOfUr, Integer> scf = new ScoreConfigFactory<>();
            fscorers.forEach(scf::withScorer);
            producer = new FieldScoreProducer<>(scf.build(), scoreStrategy);
            this.name = name;
        }

        @Override
        public Optional<ActionV2> control(RoyalGameOfUr game, PlayerInGame player) {
            if (game.isRollTime()) {
                return Optional.of(new ActionV2("roll", null));
            }
            if (!game.isMoveTime()) {
                return Optional.empty();
            }
            ParamAndField<RoyalGameOfUr, Integer> best = ScoreUtils.pickBest(producer, game, new Random());
            if (best == null) {
                return Optional.empty();
            }
            int positionOfBest = game.getPieces()[player.getIndex()][best.getField()];
            return Optional.of(new ActionV2("move", new JsonNodeFactory(false).numberNode(positionOfBest)));
        }

        @Override
        public String toString() {
            return "URScorer{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Test
    public void fight() {
        GameFight<AI> fight = new GameFight<>("UR");
        FScorer<RoyalGameOfUr, Integer> fscore = new SimpleScorer<>((ai, i) -> 1);
        ScoreInterface<RoyalGameOfUr, Integer> knockout = (i, params) -> {
            RoyalGameOfUr ur = params.getParameters();
            int cp = ur.getCurrentPlayer();
            int opponent = (cp + 1) % 2;
            int next = ur.getPieces()[cp][i] + ur.getRoll();
            return ur.playerOccupies(opponent, next) ? 1 : 0;
        };
        ScoreInterface<RoyalGameOfUr, Integer> exit = (i, params) -> {
            RoyalGameOfUr ur = params.getParameters();
            int cp = ur.getCurrentPlayer();
            int next = ur.getPieces()[cp][i] + ur.getRoll();
            return next == RoyalGameOfUr.EXIT ? 1 : 0;
        };
        ScoreInterface<RoyalGameOfUr, Integer> gotoFlower = (i, params) -> {
            RoyalGameOfUr ur = params.getParameters();
            int cp = ur.getCurrentPlayer();
            int next = ur.getPieces()[cp][i] + ur.getRoll();
            return ur.isFlower(next) ? 1 : 0;
        };
        AI[] ais = new AI[] {
                new URScorer("Idiot", Collections.emptyList()),
                new URScorer("Knockout", Arrays.asList(knockout)),
                new URScorer("KnockoutFlower", Arrays.asList(knockout, gotoFlower)),
                new URScorer("KnockoutFlowerExit", Arrays.asList(knockout, gotoFlower, exit)),
                new URScorer("FlowerExit", Arrays.asList(gotoFlower, exit)),
        };
        RoyalGameOfUrHelper helper = new RoyalGameOfUrHelper();
        FightInterface<AI> strat = new FightInterface<AI>() {
            @Override
            public AI determineWinner(AI[] players, int fightNumber) {
                RoyalGameOfUr ur = new RoyalGameOfUr();
                while (!ur.isFinished()) {
                    AI player = players[ur.getCurrentPlayer()];
                    Optional<ActionV2> result = player.control(ur, new PlayerInGame("", ur.getCurrentPlayer(), "", null, player));
                    // System.out.printf("Result for %d: %s. Values %s", ur.getCurrentPlayer(), result.orElse(null), Arrays.deepToString(ur.getPieces()));
                    result.ifPresent(act -> helper.performAction(ur, ur.getCurrentPlayer(), act.getName(), act.getActionData()));
                }

                return players[ur.getWinner()];
            }
        };
        FightResults<AI> results = fight.fightEvenly(ais, 50, strat);
        System.out.println(results.toStringMultiLine());
    }

}
