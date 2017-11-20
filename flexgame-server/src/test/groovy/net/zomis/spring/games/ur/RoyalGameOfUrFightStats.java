package net.zomis.spring.games.ur;

import net.zomis.fight.ext.*;
import net.zomis.fight.v2.IndexResults;
import net.zomis.fight.v2.StatsExtract;
import net.zomis.fight.v2.StatsFight;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RoyalGameOfUrFightStats {

    // Game, Player index, result
    BiFunction<RoyalGameOfUr, Integer, List<Integer>> tilesMostOccupied; // Tag "postMove"
    BiFunction<RoyalGameOfUr, Integer, Integer> piecesAhead;
    BiFunction<RoyalGameOfUr, Integer, Integer> piecesBehind; // one int for each piece, for each postMove, for each game


    // Average QLearn move score
    ToIntBiFunction<RoyalGameOfUr, Integer> averageMoveScore; // tag "move", tuple
    BiPredicate<RoyalGameOfUr, Integer> unableToMove; // tag "move", tuple

    ToIntBiFunction<RoyalGameOfUr, Integer> helpfulThrows; // tag "roll", tuple with player. Count possible knockouts, flowers, go safes
    ToIntBiFunction<RoyalGameOfUr, Integer> possibleMoves; // tag "preMove", tuple with player.
    ToIntBiFunction<RoyalGameOfUr, Integer> probabilityReducedForTaking; // listen for tag "move" analyze before and after
    ToIntBiFunction<RoyalGameOfUr, Integer> piecesAtHome; // number of pieces on the first four tiles
    // listen for multiple tags, gather info from one tag and save data on another


    // Use case 1: Find number of Knockouts grouped by winner and opponent.
    // e.g. Winner KFE521T -> Opponent Idiot ->  2000 knockouts

    // Use case 2: Find number of AIs grouped by number of knockouts
    // e.g. knockout 5 -> KFE521T
    // knockout 0 -> Idiot


    public static StatsExtract<List<RoyalGameOfUrAIs.AI>> urStats() {
        // Old fight system was basically: index - player1, index - player2, Win/Lose/Draw
        Collector<Integer, ?, IntSummaryStatistics> ints = Collectors.summarizingInt(i -> i);
        Collector<IntSummaryStatistics, ?, IntSummaryStatistics> sumPerGame = Collectors.summarizingInt(is -> (int)is.getSum());
        StatsExtract<List<RoyalGameOfUrAIs.AI>> se = StatsExtract.create();
        se
            .indexes("player1", "player2")
//            .indexes("winner", "loser")
            .data("gameOver", RoyalGameOfUr.class, (stats, ur, obj) -> {
                int winner = ur.getWinner();
                List<RoyalGameOfUrAIs.AI> ais = stats.getCurrent();
//                stats.<Integer>index("winner", savedKey -> ais.get(savedKey == winner ? winner : 1 - winner));
//                stats.<Integer>index("loser",  savedKey -> ais.get(savedKey != winner ? winner : 1 - winner));
                stats.<Integer>index("player1", ai -> ais.get(ai));
                stats.<Integer>index("player2", ai -> ais.get(1 - ai));
                stats.save("winResult", winner, WinResult.WIN);
                stats.save("winResult", 1 - winner, WinResult.LOSS);
                int winnerSum = Arrays.stream(ur.getPieces()[winner]).sum();
                int loserSum = Arrays.stream(ur.getPieces()[1 - winner]).sum();
                int diff = winnerSum - loserSum;
                stats.save("winDiff", winner, diff);
                stats.save("loseDiff", 1 - winner, diff);
            })
            .value("winResult", WinResult.class, FightCollectors.stats())
            .value("winDiff", Integer.class, ints)
            .value("loseDiff", Integer.class, ints)
            .valueAndThen("move", Integer.class, ints, sumPerGame)
            .valueAndThen("knockouts", Integer.class, ints, sumPerGame)
            .valueAndThen("knockouted", Integer.class, ints, sumPerGame)
            .valueAndThen("moveToFlower", Integer.class, ints, sumPerGame)
            .valueAndThen("moveFromFlower", Integer.class, ints, sumPerGame)
            .value("piecesInGame", Integer.class, ints)
            .dataTuple("preMove", RoyalGameOfUr.class, Integer.class,
                (stats, ur, action) -> {
                    int op = 1 - ur.getCurrentPlayer();
                    boolean isKnockout = ur.canKnockout(action + ur.getRoll()) && ur.playerOccupies(op, action + ur.getRoll());
                    int knockoutValue = isKnockout ? 1 : 0;
                    stats.save("knockouts", ur.getCurrentPlayer(), knockoutValue);
                    stats.save("knockouted", 1 - ur.getCurrentPlayer(), knockoutValue);

                    boolean isFlower = ur.isFlower(action + ur.getRoll());
                    stats.save("move", ur.getCurrentPlayer(), 1);
                    stats.save("piecesInGame", ur.getCurrentPlayer(), (int) Arrays.stream(ur.getPieces()[ur.getCurrentPlayer()]).filter(i -> i > 0 && i < RoyalGameOfUr.EXIT).count());
                    stats.save("moveToFlower", ur.getCurrentPlayer(), isFlower ? 1 : 0);
                    stats.save("moveFromFlower", ur.getCurrentPlayer(), ur.isFlower(action) ? 1 : 0);
                });
        return se;
    }

    /*
    * INDEX BY WINNER AI
    *
    * - Number of moves until a piece is out
    *
    * x Tiles most occupied
    * # Number of moves stayed on flower
    * - Pieces out in game
    * - Pieces/Steps behind opponent
    * - Pieces/Steps ahead
    * - Knockouts
    * - Flowers
    * - Unable to move count
    * - Total moves in game
    *
    * - Helpful throws: Knockout, Flower, Go safe
    * - Number of possible moves on turn
    * - Probability reduced for taking (e.g. from 3 steps to 4 steps, or a 2+3 --> roll 2 --> 4+3
    * - Number of pieces at home (first 4) for each player
    *
    * */
    // Can a database such as MongoDB, but preferably in-memory, be used to store all this data and provide dynamic indexing support?

    public static void main(String[] args) {
        // MFE - find the average number of 100% mines in 100 games of A vs. B (Make analysis before/after each click and check how many new appeared)
        // Solution: Call `stats.get("obj")` and `stats.put("obj")` to store data between postTuple calls? (Or simply use a postXXX for this data)
        // CWars2 - count cards used in fights, see if there is a pattern between cards used and match result
        // Solution: Group by player (winner/loser) and use data for number of times all cards was used?
        // Group by number of times a card was used and show statistics for winner/loser


        StatsExtract<Integer> se = StatsExtract.create();
        se
            .indexes("index")
            .value("value", Integer.class, Collectors.summarizingInt(i -> i))
            // .index("number", (i, key) -> i / 10 + (Integer) key)
            .dataTuple("number", Integer.class, void.class, (stats, i, v) -> {
                stats.<Integer>index("index", savedKey -> i / 10 + savedKey);
                stats.save("value", i / 100, i);
            });

        Stream<Integer> stream = IntStream.rangeClosed(0, 100).boxed();
        StatsFight.performAll(stream, (stats, data, number) -> stats.post("number", data), se);
        IndexResults seResults = se.getResults();
        System.out.println(seResults.toMultiline());

        GameFightNew<Object, Object> gf = new GameFightNew<>();
        stream = IntStream.rangeClosed(0, 100).boxed();
        FightIndexer<Integer> fint = new FightIndexer<>();
        fint.addIndex("Start ", i -> i / 10);
        fint.addData("Sum", Collectors.summarizingInt(i -> i));
        FightRes<Integer> results = gf.processStream("Test", stream, fint);
        System.out.println(results.toString(0));

        // Stream<Fight<AI, RoyalGameOfUr>> fights = gf.createEvenFightStream(ais, 100, params -> new RoyalGameOfUr());
        new RoyalGameOfUrFightStats();
    }


}
