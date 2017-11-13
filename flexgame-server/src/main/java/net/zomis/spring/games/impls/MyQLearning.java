package net.zomis.spring.games.impls;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MyQLearning<T, S> {

    private static final double DEFAULT_QVALUE = 0;
    private double discountFactor = 0.99;
    private double learningRate = 0.01;
    private boolean debug;
    private double randomMoveProbability = 0.0;
    private final Random random = new Random();

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public interface ActionPossible<T> {
        boolean test(T environment, int action);
    }

    public static class Rewarded<T> {
        private final T state;
        private final double reward;

        public Rewarded(T state, double reward) {
            this.state = state;
            this.reward = reward;
        }

        public double getReward() {
            return reward;
        }

        public T getState() {
            return state;
        }
    }

    public interface PerformAction<T> {
        Rewarded<T> apply(T environment, int action);
    }

    private final Map<S, Double> qTable = new HashMap<>();
    private final Function<T, S> stateFunction;
    private final BiFunction<S, Integer, S> stateActionFunction;
    private final ActionPossible<T> actionPossible;
    private final int maxActions;

    public MyQLearning(int maxActions,
               Function<T, S> stateFunction,
               ActionPossible<T> actionPossible,
               BiFunction<S, Integer, S> stateActionFunction) {
        this.maxActions = maxActions;
        this.stateFunction = stateFunction;
        this.actionPossible = actionPossible;
        this.stateActionFunction = stateActionFunction;
    }

    public Rewarded<T> step(T environment, PerformAction<T> performAction) {
        if (random.nextDouble() < randomMoveProbability) {
            return this.step(environment, performAction, pickRandomAction(environment));
        } else {
            return this.step(environment, performAction, pickBestAction(environment));
        }
    }

    private int pickRandomAction(T environment) {
        int count = (int) IntStream.range(0, maxActions).filter(i -> actionPossible.test(environment, i)).count();
        int actionIndex = random.nextInt(count);
        return IntStream.range(0, maxActions).filter(i -> actionPossible.test(environment, i))
            .limit(actionIndex + 1).reduce(0, (old, next) -> next);
    }

    public Rewarded<T> step(T environment, PerformAction<T> performAction, int action) {
        S state = stateFunction.apply(environment);
        S stateAction = stateActionFunction.apply(state, action);

        Rewarded<T> rewardedState = performAction.apply(environment, action);
        T nextState = rewardedState.getState();
        double rewardT = rewardedState.getReward();

        S nextStateStr = stateFunction.apply(nextState);
        double estimateOfOptimalFutureValue = IntStream.range(0, maxActions)
                .filter(i -> actionPossible.test(nextState, i))
                .mapToObj(i -> stateActionFunction.apply(nextStateStr, i))
                .mapToDouble(str -> qTable.getOrDefault(str, DEFAULT_QVALUE)).max()
                .orElse(0);

        double oldValue = qTable.getOrDefault(stateAction, DEFAULT_QVALUE);
        double learnedValue = rewardT + discountFactor * estimateOfOptimalFutureValue;
        double newValue = (1 - learningRate) * oldValue + learningRate * learnedValue;
        if (debug) {
            System.out.printf("Performed %d in state %s with reward %f. Old Value %f. Learned %f. New %f%n", action, state, rewardT,
                oldValue, learnedValue, newValue);
        }
        this.qTable.put(stateAction, newValue);
        return rewardedState;
    }

    private int pickBestAction(T environment) {
        S state = stateFunction.apply(environment);
        int bestAction = -1;
        double bestValue = -1000;
        for (int i = 0; i < maxActions; i++) {
            if (actionPossible.test(environment, i)) {
                S stateAction = stateActionFunction.apply(state, i);
                double value = DEFAULT_QVALUE;
                if (qTable.containsKey(stateAction)) {
                    value = qTable.get(stateAction);
                }

                if (bestAction == -1 || value > bestValue) {
                    bestAction = i;
                    bestValue = value;
                }
            }
        }
        if (bestAction == -1) {
            throw new IllegalStateException("No successful action in " + environment + ": " + state);
        }
        return bestAction;
    }

    public Map<S, Double> getQTable() {
        return new HashMap<>(qTable);
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getDiscountFactor() {
        return discountFactor;
    }

    public void setDiscountFactor(double discountFactor) {
        this.discountFactor = discountFactor;
    }

    public void setRandomMoveProbability(double randomMoveProbability) {
        this.randomMoveProbability = randomMoveProbability;
    }

    public double getRandomMoveProbability() {
        return randomMoveProbability;
    }

}
