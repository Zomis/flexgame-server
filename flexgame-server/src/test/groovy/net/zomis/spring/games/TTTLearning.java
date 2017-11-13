package net.zomis.spring.games;

import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTFactories;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTClassicController;
import net.zomis.tttultimate.games.TTController;
import net.zomis.tttultimate.players.TTAI;
import net.zomis.tttultimate.players.TTAIFactory;
import org.junit.Test;

import java.util.Scanner;
import java.util.function.Function;

public class TTTLearning {

    public static void main(String[] args) {
        new TTTLearning().playTTT();
    }

    @Test
    public void playTTT() {
        int sizeX = 3;
        int sizeY = 3;
        Function<TTController, String> stateToString = g -> {
            StringBuilder str = new StringBuilder();
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    TTBase sub = g.getGame().getSub(x, y);
                    str.append(sub.getWonBy().isExactlyOnePlayer() ? sub.getWonBy().name() : "_");
                }
                str.append('-');
            }
            return str.toString();
        };
        MyQLearning.ActionPossible<TTController> actionPossible = (tt, action) -> {
            int x = action % 3;
            int y = action / 3;
            if (tt.getGame().getSub(x, y) == null) {
                return false;
            }
            return tt.isAllowedPlay(tt.getGame().getSub(x, y));
        };
        MyQLearning.PerformAction<TTController> performAction = (tt, action) -> {
            int x = action % 3;
            int y = action / 3;
            TTPlayer player = tt.getCurrentPlayer();
            tt.play(tt.getGame().getSub(x, y));
            double reward = 0;

            if (tt.isGameOver() && tt.getWonBy().isExactlyOnePlayer()) {
                reward = tt.getWonBy().is(player) ? 1 : -1;
            } else if (isDraw(tt)) {
                reward = 0;
            } else {
                reward = -0.01;
            }
            return new MyQLearning.Rewarded<>(tt, reward);
        };
        MyQLearning<TTController> learn = new MyQLearning<>(9, stateToString, actionPossible);
        // learn.setLearningRate(-0.01); // This leads to bad player moves. Like XOX-OXO-_X_ instead of XOX-OXO-X__
        learn.setDiscountFactor(-0.9);
        learn.setLearningRate(1.0);
        learn.setRandomMoveProbability(1.0);

        Scanner scanner = new Scanner(System.in);
        int i = 0;
        int BREAK = 100_000;
        TTPlayer human;
        while (true) {
            i++;
            if (i % Math.ceil(BREAK / 20d) == 0) {
                System.out.println(i);
            }
            boolean debug = i % BREAK == BREAK - 1;
            human = debug ? Math.random() < 0.5 ? TTPlayer.X : TTPlayer.O : TTPlayer.NONE;
            learn.setDebug(debug);
            if (debug) {
                System.out.println("START NEW " + i);
            }
            if (i % 100 == 0 && learn.getRandomMoveProbability() >= 0.05) {
                learn.setRandomMoveProbability(learn.getRandomMoveProbability() - 0.01);
            }
            int stepCount = 0;
            TTBase board = new TTFactories().classicMNK(3);
            TTController game = new TTClassicController(board);
            // ai = new TTTAI();
            while (!game.isGameOver() && stepCount < 9) {
                if (debug) {
                    this.print(game);
                }
                if (human.is(game.getCurrentPlayer())) {
                    int pos = humanMove(game, scanner);
                    //int pos = aiMove(game);
                    if (!actionPossible.test(game, pos)) {
                        System.out.println("Illegal move.");
                        continue;
                    }
                    MyQLearning.Rewarded<TTController> step = learn.step(game, performAction, pos);
                    System.out.println("Player performed " + pos + " with reward " + step.getReward());
                    game = step.getState();
                } else {
                    MyQLearning.Rewarded<TTController> step = learn.step(game, performAction);
                    game = step.getState();
                    if (debug) {
                        System.out.printf("Step %d. Performed action with reward %f%n", stepCount, step.getReward());
                        // learn.getQTable().forEach((key, value) -> System.out.println(key + " = " + value));
                    }
                }
                // scanner.nextLine();
                stepCount++;
            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//            }
            if (debug) {
                System.out.println("TERMINAL STATE REACHED AFTER " + stepCount);
                this.print(game);
                // learn.getQTable().forEach((key, value) -> System.out.println(key + " = " + value));
                System.out.println();
            }
            // scanner.nextLine();
            if (i % BREAK == BREAK - 1) {
                scanner.nextLine();
            }
        }
        // scanner.close();
    }

    private final TTAI myAI = TTAIFactory.random().build();
    private int aiMove(TTController game) {
        TTBase sub = myAI.play(game);
        return sub.getY() * 3 + sub.getX();
    }

    private int humanMove(TTController game, Scanner scanner) {
        String move = scanner.nextLine();
        while (true) {
            try {
                return Integer.parseInt(move);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid move: " + move);
                return -1;
            }
        }
    }

    private boolean isDraw(TTController tt) {
        for (int yy = 0; yy < tt.getGame().getSizeY(); yy++) {
            for (int xx = 0; xx < tt.getGame().getSizeX(); xx++) {
                TTBase sub = tt.getGame().getSub(xx, yy);
                if (!sub.isWon()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void print(TTController game) {
        TTBase board = game.getGame();
        int i = 0;
        for (int y = 0; y < board.getSizeY(); y++) {
            for (int x = 0; x < board.getSizeX(); x++) {
                TTBase sub = board.getSub(x, y);
                System.out.print(sub.getWonBy().isExactlyOnePlayer() ? sub.getWonBy().name() : String.valueOf(i));
                i++;
            }
            System.out.println();
        }
    }

}
