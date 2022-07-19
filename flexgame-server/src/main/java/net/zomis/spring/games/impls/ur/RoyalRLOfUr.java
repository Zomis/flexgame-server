package net.zomis.spring.games.impls.ur;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.aiscores.FieldScoreProducer;
import net.zomis.aiscores.extra.ParamAndField;
import net.zomis.aiscores.extra.ScoreUtils;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

public class RoyalRLOfUr implements RoyalGameOfUrAIs.AI {

    private static final Logger logger = LoggerFactory.getLogger(RoyalRLOfUr.class);
    private static final int INPUT_SIZE = 7 * 4 * 2 + 2;
    private static final int OUTPUT_LAYER = 7;
    private static final int[] HIDDEN = { INPUT_SIZE };
    /**
     * Neural Network with x inputs
     * x = 7 pieces * 4 bits (0-14) * 2 players + 2 (1-4, current roll)
     * x = 58
     * Pre: Sort both arrays.
     *
     * or
     * x = 14 tiles (0-14) * 2 bits (X,O,both,none) + 2 (1-4, current roll)
     *
     * Approach: Play a whole bunch of games
     * After a whole bunch of games, perform updates.
     * Check all games that went good and update network, and all games that went bad and update network
     *
     * Goal: Beat at least some of my basic scoring ais (such as Knockout-only)
     */
    private final JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
    private final MultiLayerNetwork network;
    private final Random random = new Random();
    private FieldScoreProducer<RoyalGameOfUr, Integer> producer =
        new FieldScoreProducer<>(RoyalGameOfUrAIs.scf().build(), RoyalGameOfUrAIs.scoreStrategy);

    private List<AIGameData> gameData = new ArrayList<>();
    private boolean record;

    public void learn() {
        for (AIGameData gd : gameData) {
            System.out.println("Learn from " + gd.win);
            for (MoveInfo move : gd.moves) {
                INDArray input = move.input;
                double[] score = toArray(move.output);
                double win = gd.win ? 1 : 0;
                score[move.chosenAction] = win;
                INDArray labels = Nd4j.create(score);
                // How to affect only one output node and not the other ones? I don't want to label all outputs!
                // try using actual output and change chosenAction to either 1 or 0
                DataSet ds = new DataSet(input, labels);
                network.fit(ds);
            }
        }
        gameData.clear();
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    private static class AIGameData {
        List<MoveInfo> moves = new ArrayList<>();
        Boolean win;
    }

    private static class MoveInfo {
        INDArray input;
        INDArray output;
        int chosenAction;
    }

    public void newGame() {
        gameData.add(new AIGameData());
    }

    public void setWin(boolean win) {
        AIGameData currentData = gameData.get(gameData.size() - 1);
        currentData.win = win;
    }

    public RoyalRLOfUr() {
        final MultiLayerConfiguration conf = getDeepDenseLayerNetworkConfiguration();
        network = new MultiLayerNetwork(conf);
        network.init();
        network.setListeners(new ScoreIterationListener(1));
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

        double[] arr = arrayFromGame(game);
        Arrays.stream(arr).filter(d -> d != 0 && d != 1).findAny().ifPresent(d -> {
            throw new IllegalStateException("Invalid " + d);
        });
        INDArray input = Nd4j.create(arr);
        List<INDArray> forward = network.feedForward(input);
        INDArray output = forward.get(forward.size() - 1);
        output = ignoreIllegalActions(game, output);
        int action = weightedRandom(output);
//        System.out.println(forward);
  //      System.out.println("AI chose " + action);
        if (record) {
            AIGameData currentData = this.gameData.get(gameData.size() - 1);
            MoveInfo moveInfo = new MoveInfo();
            moveInfo.input = input;
            moveInfo.output = output;
            moveInfo.chosenAction = action;
            currentData.moves.add(moveInfo);
        }

        ParamAndField<RoyalGameOfUr, Integer> best = ScoreUtils.pickBest(
            producer, game, random);
        if (best == null) {
            return Optional.empty();
        }
        int positionOfBest = game.getPieces()[player.getIndex()][best.getField()];
        return Optional.of(new ActionV2("move", nodeFactory.numberNode(positionOfBest)));
    }

    private static INDArray ignoreIllegalActions(RoyalGameOfUr game, INDArray output) {
        int length = output.size(1);
        double[] d = toArray(output);
        for (int i = 0; i < length; i++) {
            int position = game.getPieces()[game.getCurrentPlayer()][i];
            boolean canMove = !game.canMove(game.getCurrentPlayer(), position, game.getRoll());
            if (!canMove) {
                d[i] = 0;
            }
        }
        return Nd4j.create(d);
    }

    private static double[] toArray(INDArray output) {
        return IntStream.range(0, output.size(1)).mapToDouble(output::getDouble).toArray();
    }

    private int weightedRandom(INDArray output) {
        double sum = 0;
        double arrSum = IntStream.range(0, output.size(1)).mapToDouble(output::getDouble).sum();
        double rand = random.nextDouble() * arrSum;
        //System.out.println("output " + output + " random " + rand);
        for (int i = 0; i < output.size(1); i++) {
            sum += output.getDouble(i);
            if (sum >= rand) {
                return i;
            }
        }
        throw new IllegalStateException("random " + rand + " and array " + output);
    }

    private double[] arrayFromGame(RoyalGameOfUr game) {
        double[] result = new double[INPUT_SIZE];
        int roll = game.isRollTime() ? 0 : game.getRoll() - 1;
        result[0] = roll >> 1 & 1;
        result[1] = roll & 1;
        int[][] pieces = game.getPieces();
        for (int playerIndex = 0; playerIndex < pieces.length; playerIndex++) {
            int[] playerPieces = Arrays.copyOf(pieces[playerIndex], pieces[playerIndex].length);
            int r = game.getCurrentPlayer() == 0 ? 2 : 2 + playerPieces.length * 4;
            Arrays.sort(playerPieces);
            for (int i = 0; i < pieces[playerIndex].length; i++) {
                int pos = playerPieces[i];
                result[r] = pos >> 3 & 1;
                result[r+1] = pos >> 2 & 1;
                result[r+2] = pos >> 1 & 1;
                result[r+3] = pos & 1;
                r += 4;
            }
        }
        return result;
    }

    private static MultiLayerConfiguration getDeepDenseLayerNetworkConfiguration() {
        // org.deeplearning4j.rl4j.network.dqn.DQN

        return new NeuralNetConfiguration.Builder()
            .seed(42)
            .iterations(1)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .learningRate(0.01)
            .weightInit(WeightInit.XAVIER)
            .updater(new Sgd())
            .list(
                new DenseLayer.Builder().nIn(INPUT_SIZE).nOut(HIDDEN[0]).build(),
                new DenseLayer.Builder().nIn(HIDDEN[0]).nOut(OUTPUT_LAYER).build(),
                new OutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.SOFTMAX)
                    .nIn(OUTPUT_LAYER).nOut(OUTPUT_LAYER).build()
            ).pretrain(false).backprop(true).build();
    }
}
