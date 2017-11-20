package net.zomis.spring.games;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import net.zomis.aiscores.FieldScoreProducer;
import net.zomis.aiscores.FieldScores;
import net.zomis.fight.GameFight;
import net.zomis.fight.v2.IndexResults;
import net.zomis.fight.v2.StatsFight;
import net.zomis.fight.v2.StatsInterface;
import net.zomis.fight.v2.StatsPerform;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.spring.games.impls.QStoreMongo;
import net.zomis.spring.games.impls.qlearn.QStore;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrHelper;
import net.zomis.spring.games.impls.ur.RoyalRLOfUr;
import net.zomis.spring.games.ur.RoyalGameOfUrFightStats;
import org.apache.log4j.LogManager;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.*;

import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

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
    public void aiTest() {
        RoyalGameOfUr ur = setup(1, 2, new int[][]{{15, 15, 15, 15, 11, 3, 2}, {15, 9, 7, 4, 3, 0, 0}});
        URScorer ai = new URScorer("KnockoutFlower", RoyalGameOfUrAIs.scf().withScorer(knockout).withScorer(gotoFlower));
        FieldScores<RoyalGameOfUr, Integer> scores = ai.getProducer().analyzeAndScore(ur);
        System.out.println(scores.getScores());
        Optional<ActionV2> move = ai.control(ur, new PlayerInGame("", 1, "", null, ai));
        assertEquals(9, move.get().getActionData().asInt());

        ur = setup(1, 3, new int[][]{{15, 4, 0, 0, 0, 0, 0}, {10, 8, 6, 0, 0, 0, 0}});
        ai = new RoyalGameOfUrAIs.URScorer("KFE521S3", RoyalGameOfUrAIs.scf()
                .withScorer(knockout, 5)
                .withScorer(gotoFlower, 2)
                .withScorer(gotoSafety, 0.1)
                .withScorer(leaveSafety, -0.1)
                .withScorer(riskOfBeingTaken, -0.1)
                .withScorer(riskOfBeingTakenHere, 0.5)
                .withScorer(exit));

        Arrays.stream(RoyalGameOfUrAIs.class.getFields()).forEach(f -> {
            try {
                System.out.println(f.getName() + ": " + f.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        FieldScoreProducer<RoyalGameOfUr, Integer> producer = ai.getProducer();
        producer.setDetailed(true);
        scores = producer.analyzeAndScore(ur);
        scores.getScores().forEach((key, value) -> {
            System.out.println(key + "= " + value.getScore() + " : " + value.getScoreMap());
        });
        System.out.println(scores.getScores());
        move = ai.control(ur, new PlayerInGame("", 1, "", null, ai));
        assertEquals(6, move.get().getActionData().asInt());
    }

    public static void main(String[] args) {

        LogManager.getLogger("org.mongodb.driver.connection").setLevel(org.apache.log4j.Level.OFF);
        LogManager.getLogger("org.mongodb.driver.management").setLevel(org.apache.log4j.Level.OFF);
        LogManager.getLogger("org.mongodb.driver.cluster").setLevel(org.apache.log4j.Level.OFF);
        LogManager.getLogger("org.mongodb.driver.protocol.insert").setLevel(org.apache.log4j.Level.OFF);
        LogManager.getLogger("org.mongodb.driver.protocol.command").setLevel(org.apache.log4j.Level.OFF);
        LogManager.getLogger("org.mongodb.driver.protocol.query").setLevel(org.apache.log4j.Level.OFF);
        LogManager.getLogger("org.mongodb.driver.protocol.update").setLevel(org.apache.log4j.Level.OFF);


        RoyalRLOfUr royalAI = new RoyalRLOfUr();
        RoyalGameOfUrHelper helper = new RoyalGameOfUrHelper();
        RoyalGameOfUrAIs.AI[] players = new RoyalGameOfUrAIs.AI[] {
            new RoyalGameOfUrAIs.URScorer("Simple1", RoyalGameOfUrAIs.scf()
            ),
            royalAI
        };
        royalAI.setRecord(true);
        Scanner scanner = new Scanner(System.in);
        boolean playing = true;
        int skip = 0;
        while (playing) {
            RoyalGameOfUr game = new RoyalGameOfUr();
            royalAI.newGame();
            while (!game.isFinished()) {
                RoyalGameOfUrAIs.AI player = players[game.getCurrentPlayer()];
                PlayerInGame pig = new PlayerInGame("", game.getCurrentPlayer(), "", null, player);
                Optional<ActionV2> result = player.control(game, pig);
                if (!game.isRollTime()) {
                //    System.out.println(game);
                //    System.out.println("AI: " + royalAI.control(game, pig));
                //    scanner.nextLine();
                }
                result.ifPresent(act -> helper.performAction(game, game.getCurrentPlayer(), act.getName(), act.getActionData()));
            }
            System.out.println("Winner is " + game.getWinner());
            royalAI.setWin(game.getWinner() == 1);


            String str = "";
            if (skip <= 0) {
                System.out.println("What's next?");
                str = scanner.nextLine();
            } else {
                skip--;
            }
            if (str.equals("learn")) {
                royalAI.learn();
            }
            if (str.equals("exit")) {
                playing = false;
            }
            if (str.startsWith("play")) {
                skip = Integer.parseInt(str.substring(5));
            }
            if (str.equals("fight")) {
                royalAI.setRecord(false);
                new RoyalGameOfUrTest().fightWithAI(royalAI);
                royalAI.setRecord(true);
            }
        }
        scanner.close();
    }

    private static class RoyalQLearn implements AI {

        private final MyQLearning<RoyalGameOfUr, Long> learn;

        public RoyalQLearn(MyQLearning<RoyalGameOfUr, Long> learn) {
            this.learn = learn;
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

            int action = learn.pickAction(game);
            return Optional.of(new ActionV2("move", new JsonNodeFactory(false).numberNode(action)));
        }

    }


    @Test
    public void fight() {
        fightWithAI(new RoyalRLOfUr());
    }

    static Logger root = (Logger) LoggerFactory
            .getLogger(Logger.ROOT_LOGGER_NAME);

    static {
        root.setLevel(Level.INFO);
    }

    public void fightWithAI(RoyalRLOfUr royalAI) {

        MongoClient mongo = new MongoClient();
        MongoDatabase db = mongo.getDatabase("flexgame_server");

        MyQLearning.ActionPossible<RoyalGameOfUr> actionPossible = (environment, action) ->
            environment.canMove(environment.getCurrentPlayer(), action, environment.getRoll());
        QStore<Long> qStore = new QStoreMongo<>(db.getCollection("qlearn_ur")); // new QStoreMap<>();
        // System.out.println(qStore.getOrDefault(new RoyalGameOfUr().toLong(), 0));
        MyQLearning<RoyalGameOfUr, Long> learn = new MyQLearning<>(15, RoyalGameOfUr::toLong, actionPossible, (state, action) -> state << 4 + action,
                qStore);
        learn.setLearningRate(0.2);
        learn.setDiscountFactor(0.99);
        learn.setRandomMoveProbability(0.1);
        learn.setEnabled(false);
        RoyalQLearn royalQ = new RoyalQLearn(learn);

        RoyalGameOfUrAIs.AI[] ais = new RoyalGameOfUrAIs.AI[] {
                // TODO: Evaluate current position. Calculate likelihood of winning?
                // Use current positions and likelihood of punishing opponent
                // Chase AI that follows the opponent no matter the cost (such as opponent has one piece left)
/*                new RoyalGameOfUrAIs.URScorer("KFE521S3", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(exit)),*/
                new RoyalGameOfUrAIs.URScorer("KFE521T", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521T2", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(riskOfBeingTaken, -0.1)
                        .withScorer(exit)),
                new RoyalGameOfUrAIs.URScorer("KFE521S3C", RoyalGameOfUrAIs.scf()
                        .withScorer(knockout, 5)
                        .withScorer(gotoFlower, 2)
                        .withScorer(gotoSafety, 0.1)
                        .withScorer(leaveSafety, -0.1)
                        .withScorer(riskOfBeingTaken, -3)
                        .withScorer(exit)),
            //    royalQ,
        };
        RoyalGameOfUrHelper helper = new RoyalGameOfUrHelper();
        JsonNodeFactory factory = new JsonNodeFactory(false);
        MyQLearning.PerformAction<RoyalGameOfUr> performAction = (state, action) -> {
            int currentPlayer = state.getCurrentPlayer();
            helper.performAction(state, state.getCurrentPlayer(), "move", factory.numberNode(action));
            while (!state.isFinished() && state.isRollTime()) {
                state.roll();
            }
            int nextPlayer = state.getCurrentPlayer();

            double reward = state.isFinished() ? state.getWinner() == currentPlayer ? 10 : -10 : -0.01;
            double discountFactor = currentPlayer == nextPlayer ? learn.getDiscountFactor() : learn.getDiscountFactor() * -1;
            return new MyQLearning.Rewarded<>(state, reward).withDiscountFactor(discountFactor);
        };
        StatsPerform<List<RoyalGameOfUrAIs.AI>> strat = new StatsPerform<List<RoyalGameOfUrAIs.AI>>() {
            @Override
            public void perform(StatsInterface stats, List<RoyalGameOfUrAIs.AI> players, int number) {
                if (number % 20 == 0) {
                    System.out.println(players + " fighting round " + number);
                }
                RoyalGameOfUr ur = new RoyalGameOfUr();
                while (ur.isRollTime()) {
                    int cp = ur.getCurrentPlayer();
                    int roll = ur.roll();
                    stats.postTuple("roll", cp, roll);
                }
                while (!ur.isFinished()) {
                    if (!ur.isMoveTime()) {
                        throw new IllegalStateException("NOT MOVE TIME!");
                    }

                    RoyalGameOfUrAIs.AI player = players.get(ur.getCurrentPlayer());
                    Optional<ActionV2> result = player.control(ur, new PlayerInGame("", ur.getCurrentPlayer(), "", null, player));
                    // System.out.printf("Result for %d: %s. Values %s", ur.getCurrentPlayer(), result.orElse(null), Arrays.deepToString(ur.getPieces()));
                    result.ifPresent(act -> {
                        int actionValue = act.getActionData().asInt();
                        stats.postTuple("preMove", ur, actionValue);
                        MyQLearning.Rewarded<RoyalGameOfUr> reward = learn.step(ur, performAction, actionValue);
                        stats.post("q-reward", reward.getReward());
                    });
                }
                stats.post("gameOver", ur);
                stats.post("winner", ur.getWinner());
                stats.post("loser", 1 - ur.getWinner());
            }
        };

        long lastSize;
        do {
            lastSize = learn.getQTableSize();
            List<RoyalGameOfUrAIs.AI> aiList = Arrays.asList(ais);

            IndexResults results = StatsFight.fightEvently(aiList, 1000, strat, RoyalGameOfUrFightStats.urStats());

            // FightResults<RoyalGameOfUrAIs.AI> results = fight.fightEvenly(ais, 1000, strat);
            System.out.println(results.toMultiline());
            System.out.println("States: " + learn.getQTableSize());

            long newStates = learn.getQTableSize() - lastSize;
            System.out.println(newStates + " new states");
        } while (lastSize != learn.getQTableSize());
    }

}
