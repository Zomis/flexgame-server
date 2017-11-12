package net.zomis.spring.games.impls;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MyQLearning<T> {

    private static final double DEFAULT_QVALUE = 0;
    private final double discountFactor = 0.99;
    private final double learningRate = 0.01;

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

    private final Map<String, Double> qTable = new HashMap<>();
    private final Function<T, String> stateToString;
    private final PerformAction<T> performAction;
    private final ActionPossible<T> actionPossible;
    private final int maxActions;

    public MyQLearning(int maxActions,
               Function<T, String> stateToString,
               ActionPossible<T> actionPossible,
               PerformAction<T> performAction) {
        this.maxActions = maxActions;
        this.stateToString = stateToString;
        this.actionPossible = actionPossible;
        this.performAction = performAction;
    }

    public Rewarded<T> step(T environment) {
        int action = pickBestAction(environment);

        String state = stateToString.apply(environment);
        String stateAction = state + action;

        Rewarded<T> rewardedState = performAction.apply(environment, action);
        T nextState = rewardedState.getState();
        double rewardT = rewardedState.getReward();

        String nextStateStr = stateToString.apply(nextState);
        double estimateOfOptimalFutureValue = IntStream.range(0, maxActions)
                .filter(i -> actionPossible.test(nextState, i))
                .mapToObj(i -> nextStateStr + i)
                .mapToDouble(str -> qTable.getOrDefault(str, DEFAULT_QVALUE)).max()
                .orElse(0);

        double oldValue = qTable.getOrDefault(stateAction, DEFAULT_QVALUE);
        double learnedValue = rewardT + discountFactor * estimateOfOptimalFutureValue;
        double newValue = (1 - learningRate) * oldValue + learningRate * learnedValue;
/*        System.out.printf("Performed %d in state %s with reward %f. Old Value %f. Learned %f. New %f%n", action, state, rewardT,
                oldValue, learnedValue, newValue);*/
        this.qTable.put(stateAction, newValue);
        return rewardedState;
    }

    private int pickBestAction(T environment) {
        String state = stateToString.apply(environment);
        int bestAction = -1;
        double bestValue = -1000;
        for (int i = 0; i < maxActions; i++) {
            if (actionPossible.test(environment, i)) {
                String stateAction = state + i;
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

    public Map<String, Double> getQTable() {
        return new HashMap<>(qTable);
    }

}
