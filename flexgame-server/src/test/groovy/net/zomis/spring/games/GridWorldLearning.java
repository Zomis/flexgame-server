package net.zomis.spring.games;

import net.zomis.spring.games.impls.GridWorld;
import net.zomis.spring.games.impls.MyQLearning;
import org.junit.Test;

import java.util.Scanner;
import java.util.function.Function;

public class GridWorldLearning {

    public static void main(String[] args) {
        new GridWorldLearning().gridWorld();
    }

    @Test
    public void gridWorld() {
        Function<GridWorld, String> stateToString = g -> String.valueOf(g.getPosX()) + g.getPosY();
        MyQLearning.ActionPossible<GridWorld> actionPossible = GridWorld::canMove;
        MyQLearning.PerformAction<GridWorld> performAction = GridWorld::performMove;
        MyQLearning<GridWorld, String> learn = new MyQLearning<>(4, stateToString, actionPossible, (state, action) -> state + action);

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 1000; i++) {
            System.out.println("START NEW " + i);
            int stepCount = 0;
            GridWorld game = new GridWorld();
            while (!game.isFinished()) {
       //         game.print();
                MyQLearning.Rewarded<GridWorld> step = learn.step(game, performAction);
                game = step.getState();
                stepCount++;
           //     System.out.printf("Step %d. Performed action with reward %f%n", stepCount, step.getReward());
//                learn.getQTable().forEach((key, value) -> System.out.println(key + " = " + value));
          //      scanner.nextLine();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            System.out.println("TERMINAL STATE REACHED AFTER " + stepCount);
         //   game.print();
    //        learn.getQTable().forEach((key, value) -> System.out.println(key + " = " + value));
            //System.out.println();
            //scanner.nextLine();
        }
        scanner.close();

    }

}
