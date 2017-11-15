package net.zomis.spring.games;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.spring.games.impls.QStoreMongo;
import net.zomis.spring.games.impls.qlearn.QStore;
import net.zomis.spring.games.impls.qlearn.QStoreMap;
import net.zomis.spring.games.impls.qlearn.TTTQLearn;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTFactories;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTClassicController;
import net.zomis.tttultimate.games.TTController;
import net.zomis.tttultimate.players.TTAI;
import net.zomis.tttultimate.players.TTAIFactory;
import org.bson.Document;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

import static net.zomis.spring.games.impls.qlearn.TTTQLearn.actionPossible;
import static net.zomis.spring.games.impls.qlearn.TTTQLearn.performAction;

public class TTTLearning {

    static Logger root = (Logger) LoggerFactory
            .getLogger(Logger.ROOT_LOGGER_NAME);

    static {
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) throws InterruptedException {
        new TTTLearning().playTTT();
    }

    @Test
    public void playTTT() throws InterruptedException {
        MongoClient client = new MongoClient();
        MongoDatabase mongoDB = client.getDatabase("flexgame_server");
        MongoCollection<Document> mongoCollection = mongoDB.getCollection("qlearn_ttt");
        QStore<String> qStore = new QStoreMongo<>(mongoCollection);

        MyQLearning<TTController, String> learn = TTTQLearn.newLearner(new QStoreMap<>());
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        int BREAK = 100_000;
        TTPlayer human;
        while (true) {
            i++;
            System.out.println("Waiting...");
            scanner.nextLine();
            System.out.println("Game " + i + " size " + qStore.size());
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
