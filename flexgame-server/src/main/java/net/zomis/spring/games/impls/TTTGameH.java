package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.zomis.spring.games.Hashids;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.generic.v2.*;
import net.zomis.spring.games.impls.qlearn.QStoreMap;
import net.zomis.spring.games.impls.qlearn.TTTQLearn;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;
import net.zomis.tttultimate.games.TTControllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TTTGameH implements GameHelper2<ScoredTTT> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Hashids idGenerator = new Hashids(getClass().getSimpleName());
    private final AtomicInteger gameId = new AtomicInteger();
    private final JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
    private Map<String, ScoredTTT.ScoredTTTQLearn> qaiMap = new HashMap<>();

    @Override
    public LobbyGame<ScoredTTT> createGame(Object gameConfiguration) {
        String id = idGenerator.encrypt(gameId.incrementAndGet());
        return new LobbyGame<>(id, new TokenGenerator(), this, gameConfiguration);
    }
    // TODO: Keep score, first player to X (20 or so?) or best of 30 games? Make it a parameter?

    @Override
    public ActionResult playerJoin(LobbyGame<ScoredTTT> game, Object playerConfiguration) {
        if (game.getPlayers().size() < 2) {
            return new ActionResult(true, "OK");
        }
        return new ActionResult(false, "Game is full");
    }

    @Override
    public Optional<PlayerController<ScoredTTT>> inviteAI(LobbyGame<ScoredTTT> game, String aiName, Object aiConfig, Object playerConfiguration) {
        if (game.getPlayers().size() >= 2) {
            return Optional.empty();
        }
        if (aiName.startsWith("Q")) {
            MyQLearning<TTController, String> learn = TTTQLearn.newLearner(new QStoreMap<>());
            qaiMap.putIfAbsent(aiName, new ScoredTTT.ScoredTTTQLearn(learn));
            return Optional.of(qaiMap.get(aiName));
        }

        if (aiName.equals("FixedMove")) {
            return Optional.of(new FixedMoveTTTAI(aiConfig));
        }
        return Optional.empty();
    }

    @Override
    public ScoredTTT startGame(LobbyGame<ScoredTTT> lobbyGame) {
        if (lobbyGame.getPlayers().size() != 2) {
            return null;
        }
        // TODO: ? lobbyGame.shufflePlayerOrder();
        return ScoredTTT.of(TTControllers::classicTTT);
    }

    @Override
    public InternalActionResult performAction(RunningGame<ScoredTTT> running, PlayerInGame player, String actionType, Object actionData) {
        switch (actionType) {
            case "move":
                TTController game = running.getGame().getCurrent();
                Point point = mapper.convertValue(actionData, Point.class);
                if (game.getCurrentPlayer() == TTPlayer.X ^ player.getIndex() == 0) {
                    return new InternalActionResult(false, "Not your turn");
                }
                TTBase piece = game.getGame().getSub(point.x, point.y);
                if (piece.getWonBy().isExactlyOnePlayer()) {
                    return new InternalActionResult(false, "Position already taken");
                }
                // TODO: Use events like `beforeAction` and `afterAction`?
                Optional<ScoredTTT.ScoredTTTQLearn> qlearn = getQLearn(running);
                if (qlearn.isPresent()) {
                    qlearn.get().perform(game, new ActionV2(actionType, (JsonNode) actionData));
                } else {
                    game.play(game.getGame().getSub(point.x, point.y));
                }
                running.getGame().postAction();
                return new InternalActionResult(true, "");
            default:
                return new InternalActionResult(false, "Unknown action");
        }
    }

    private Optional<ScoredTTT.ScoredTTTQLearn> getQLearn(RunningGame<ScoredTTT> running) {
        return running.players().filter(p -> p.getController() instanceof ScoredTTT.ScoredTTTQLearn)
            .map(p -> (ScoredTTT.ScoredTTTQLearn) p.getController())
            .findAny();
    }

    @Override
    public Object gameSummary(RunningGame<ScoredTTT> game) {
        return new Object();
    }

    @Override
    public Object gameDetails(RunningGame<ScoredTTT> game, PlayerInGame fromWhosPerspective) {
        ObjectNode obj = nodeFactory.objectNode();
        ArrayNode node = nodeFactory.arrayNode(3);
        ScoredTTT board = game.getGame();
        for (int y = 0; y < 3; y++) {
            ArrayNode row = nodeFactory.arrayNode(3);
            for (int x = 0; x < 3; x++) {
                TTPlayer value = board.getCurrent().getGame().getSub(x, y).getWonBy();
                if (value.isExactlyOnePlayer()) {
                    row.add(value.name());
                } else {
                    row.addNull();
                }
            }
            node.add(row);
        }
        obj.set("board", node);
        int[] score = game.getGame().getScore();
        obj.set("scores", nodeFactory.arrayNode().add(score[0]).add(score[1]));
        return obj;
    }

}
