package net.zomis.monopoly.model;

import java.util.*;
import java.util.stream.IntStream;

public class Game {

    private final List<Player> players = new ArrayList<>();
    private final List<Property> properties = MonopolyTiles.createDefault();
    // GameTasks: Pay money, Auction, Bus choice (Speed Die), Buy or not buy, Move destination choice (Speed Die),
    // Throw to get out of Jail or pay or use card, Decide starting plaayer?
    // It doesn't have to be the current player who has to do something, for example "collect $50 from all players"
    private final Deque<GameTask> stack = new LinkedList<>();

    private GameSetup setup;
    private int currentPlayer;

    public Game(GameSetup gameSetup) {
        this.setup = gameSetup;
    }

    public static Game createGame(GameSetup setup, List<String> playerNames) {
        Game game = new Game(setup);
        IntStream.range(0, playerNames.size())
            .mapToObj(i -> new Player(game, i, playerNames.get(i)))
            .forEach(game.players::add);
        return game;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return this.players.get(currentPlayer);
    }

    public Property getProperty(int i) {
        return properties.get(i);
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    public int getTileCount() {
        return properties.size();
    }

    public boolean isEmptyStack() {
        return stack.isEmpty();
    }

    public GameSetup getSetup() {
        return setup;
    }

    public void addState(GameTask gameTask) {
        this.stack.addLast(gameTask);
    }

    public GameTask getState() {
        return this.stack.getFirst();
    }

    public boolean isTaskType(GameTask.GameTaskType type) {
        return getState().getType() == type;
    }

}
