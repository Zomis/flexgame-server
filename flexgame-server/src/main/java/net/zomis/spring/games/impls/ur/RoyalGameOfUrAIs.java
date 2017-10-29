package net.zomis.spring.games.impls.ur;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.aiscores.FieldScoreProducer;
import net.zomis.aiscores.ScoreConfigFactory;
import net.zomis.aiscores.ScoreParameters;
import net.zomis.aiscores.ScoreStrategy;
import net.zomis.aiscores.extra.ParamAndField;
import net.zomis.aiscores.extra.ScoreUtils;
import net.zomis.aiscores.scorers.ScoreInterface;
import net.zomis.aiscores.scorers.SimpleScorer;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.generic.v2.PlayerController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoyalGameOfUrAIs {

    public interface AI extends PlayerController<RoyalGameOfUr> {}

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
    public static class URScorer implements AI {

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

    public static final ScoreInterface<RoyalGameOfUr, Integer> knockout = (i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int opponent = (cp + 1) % 2;
        int next = ur.getPieces()[cp][i] + ur.getRoll();
        return ur.playerOccupies(opponent, next) ? 1 : 0;
    };
    public static final ScoreInterface<RoyalGameOfUr, Integer> exit = (i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int next = ur.getPieces()[cp][i] + ur.getRoll();
        return next == RoyalGameOfUr.EXIT ? 1 : 0;
    };
    public static final ScoreInterface<RoyalGameOfUr, Integer> gotoFlower = (i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int next = ur.getPieces()[cp][i] + ur.getRoll();
        return ur.isFlower(next) ? 1 : 0;
    };


}
