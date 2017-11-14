package net.zomis.spring.games.impls.ur;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.aiscores.FieldScoreProducer;
import net.zomis.aiscores.ScoreConfigFactory;
import net.zomis.aiscores.ScoreParameters;
import net.zomis.aiscores.ScoreStrategy;
import net.zomis.aiscores.extra.ParamAndField;
import net.zomis.aiscores.extra.ScoreUtils;
import net.zomis.aiscores.scorers.SimpleScorer;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.generic.v2.PlayerController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoyalGameOfUrAIs {

    public interface AI extends PlayerController<RoyalGameOfUr> {}

    private static final Collection<Integer> fields = IntStream.range(0, 7).mapToObj(i -> i).collect(Collectors.toList());
    public static final ScoreStrategy<RoyalGameOfUr, Integer> scoreStrategy = new ScoreStrategy<RoyalGameOfUr, Integer>() {

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
    public static ScoreConfigFactory<RoyalGameOfUr, Integer> scf() {
        return new ScoreConfigFactory<>();
    }

    public static class URScorer implements AI {

        private final String name;
        public final FieldScoreProducer<RoyalGameOfUr, Integer> producer;

        public URScorer(String name, ScoreConfigFactory<RoyalGameOfUr, Integer> scoreConfig) {
            producer = new FieldScoreProducer<>(scoreConfig.build(), scoreStrategy);
            this.name = name;
        }

        public FieldScoreProducer<RoyalGameOfUr, Integer> getProducer() {
            return producer;
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

    public static final SimpleScorer<RoyalGameOfUr, Integer> knockout = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int opponent = (cp + 1) % 2;
        int next = ur.getPieces()[cp][i] + ur.getRoll();
        return ur.canKnockout(next) && ur.playerOccupies(opponent, next) ? 1 : 0;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> position = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        return (double) ur.getPieces()[cp][i] / RoyalGameOfUr.EXIT;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> leaveFlower = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int position = ur.getPieces()[cp][i];
        return ur.isFlower(position) ? 1 : 0;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> gotoSafety = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int position = ur.getPieces()[cp][i];
        int next = position + ur.getRoll();
        return next > 12 ? 1 : 0;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> leaveSafety = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int position = ur.getPieces()[cp][i];
        int next = position + ur.getRoll();
        return position <= 4 && next > 4 ? 1 : 0;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> whichPiece = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        return 1.0 * (i + 1) / ur.getPieces()[cp].length;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> riskOfBeingTakenHere = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int position = ur.getPieces()[cp][i];
        int nextPlayer = (ur.getCurrentPlayer() + 1) % ur.getPieces().length;
        IntUnaryOperator positionToTakePossible = roll -> ur.canKnockout(position) && ur.playerOccupies(nextPlayer, position - roll) ? 1 : 0;
        double take1 = positionToTakePossible.applyAsInt(1) * 4.0 / 16.0;
        double take2 = positionToTakePossible.applyAsInt(2) * 6.0 / 16.0;
        double take3 = positionToTakePossible.applyAsInt(3) * 4.0 / 16.0;
        double take4 = positionToTakePossible.applyAsInt(4) * 1.0 / 16.0;
        return take1 + take2 + take3 + take4;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> riskOfBeingTaken = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int position = ur.getPieces()[cp][i];
        int next = position + ur.getRoll();
        int[][] pieces = ur.getPiecesCopy();
        pieces[cp][i] = next;

        int nextPlayer = (ur.getCurrentPlayer() + 1) % pieces.length;
        RoyalGameOfUr copy = new RoyalGameOfUr(nextPlayer, -1, pieces);

        /*
        * Create a copy of board and analyze risk of being taken.
        * Remember that 1/16, 4/16, 6/16, 4/16, 1/16
        */
        double take1 = canTakeWithRoll(copy, 1) * 4.0 / 16.0;
        double take2 = canTakeWithRoll(copy, 2) * 6.0 / 16.0;
        double take3 = canTakeWithRoll(copy, 3) * 4.0 / 16.0;
        double take4 = canTakeWithRoll(copy, 4) * 1.0 / 16.0;
        return take1 + take2 + take3 + take4;
    });

    private static double canTakeWithRoll(RoyalGameOfUr ur, int roll) {
        int[][] pieces = ur.getPieces();
        int cp = ur.getCurrentPlayer();
        int opponent = (cp + 1) % pieces.length;
        for (int i = 0; i < pieces[cp].length; i++) {
            int value = pieces[cp][i];
            int next = value + roll;
            if (ur.canKnockout(next) && ur.playerOccupies(opponent, next)) {
                return 1;
            }
        }
        return 0;
    }

    public static final SimpleScorer<RoyalGameOfUr, Integer> exit = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int next = ur.getPieces()[cp][i] + ur.getRoll();
        return next == RoyalGameOfUr.EXIT ? 1 : 0;
    });
    public static final SimpleScorer<RoyalGameOfUr, Integer> gotoFlower = new SimpleScorer<>((i, params) -> {
        RoyalGameOfUr ur = params.getParameters();
        int cp = ur.getCurrentPlayer();
        int next = ur.getPieces()[cp][i] + ur.getRoll();
        return ur.isFlower(next) ? 1 : 0;
    });


}
