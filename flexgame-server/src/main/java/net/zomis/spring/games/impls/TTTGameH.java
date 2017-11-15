package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.spring.games.Hashids;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.generic.v2.*;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;
import net.zomis.tttultimate.games.TTControllers;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TTTGameH implements GameHelper2<TTController> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Hashids idGenerator = new Hashids(getClass().getSimpleName());
    private final AtomicInteger gameId = new AtomicInteger();

    @Override
    public LobbyGame<TTController> createGame(Object gameConfiguration) {
        String id = idGenerator.encrypt(gameId.incrementAndGet());
        return new LobbyGame<>(id, new TokenGenerator(), this, gameConfiguration);
    }

    @Override
    public ActionResult playerJoin(LobbyGame<TTController> game, Object playerConfiguration) {
        if (game.getPlayers().size() < 2) {
            return new ActionResult(true, "OK");
        }
        return new ActionResult(false, "Game is full");
    }

    @Override
    public Optional<PlayerController<TTController>> inviteAI(LobbyGame<TTController> game, String aiName, Object aiConfig, Object playerConfiguration) {
        if (game.getPlayers().size() >= 2) {
            return Optional.empty();
        }

        if (aiName.equals("FixedMove")) {
            return Optional.of(new FixedMoveTTTAI(aiConfig));
        }
        return Optional.empty();
    }

    @Override
    public TTController startGame(LobbyGame<TTController> lobbyGame) {
        if (lobbyGame.getPlayers().size() != 2) {
            return null;
        }
        // TODO: ? lobbyGame.shufflePlayerOrder();
        TTController gameObj = TTControllers.classicTTT();
        return gameObj;
    }

    @Override
    public InternalActionResult performAction(RunningGame<TTController> running, PlayerInGame player, String actionType, Object actionData) {
        switch (actionType) {
            case "move":
                TTController game = running.getGame();
                Point point = mapper.convertValue(actionData, Point.class);
                if (game.getCurrentPlayer() == TTPlayer.X ^ player.getIndex() == 0) {
                    return new InternalActionResult(false, "Not your turn");
                }
                TTBase piece = game.getGame().getSub(point.x, point.y);
                if (piece.getWonBy().isExactlyOnePlayer()) {
                    return new InternalActionResult(false, "Position already taken");
                }
                    game.play(game.getGame().getSub(point.x, point.y));
                return new InternalActionResult(true, "");
            default:
                return new InternalActionResult(false, "Unknown action");
        }
    }

    @Override
    public Object gameSummary(RunningGame<TTController> game) {
        return new Object();
    }

    @Override
    public Object gameDetails(RunningGame<TTController> game, PlayerInGame fromWhosPerspective) {
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ArrayNode node = nodeFactory.arrayNode(3);
        TTController board = game.getGame();
        for (int y = 0; y < 3; y++) {
            ArrayNode row = nodeFactory.arrayNode(3);
            for (int x = 0; x < 3; x++) {
                TTPlayer value = board.getGame().getSub(x, y).getWonBy();
                if (value.isExactlyOnePlayer()) {
                    row.add(value.name());
                } else {
                    row.addNull();
                }
            }
            node.add(row);
        }
        return node;
    }
}
