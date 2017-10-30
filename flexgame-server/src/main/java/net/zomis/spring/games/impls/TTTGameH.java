package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.zomis.spring.games.Hashids;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.generic.v2.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TTTGameH implements GameHelper2<TTTGame> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Hashids idGenerator = new Hashids(getClass().getSimpleName());
    private final AtomicInteger gameId = new AtomicInteger();

    @Override
    public LobbyGame<TTTGame> createGame(Object gameConfiguration) {
        String id = idGenerator.encrypt(gameId.incrementAndGet());
        return new LobbyGame<>(id, new TokenGenerator(), this, gameConfiguration);
    }

    @Override
    public ActionResult playerJoin(LobbyGame<TTTGame> game, Object playerConfiguration) {
        if (game.getPlayers().size() < 2) {
            return new ActionResult(true, "OK");
        }
        return new ActionResult(false, "Game is full");
    }

    @Override
    public Optional<PlayerController<TTTGame>> inviteAI(LobbyGame<TTTGame> game, String aiName, Object aiConfig, Object playerConfiguration) {
        if (game.getPlayers().size() >= 2) {
            return Optional.empty();
        }

        if (aiName.equals("FixedMove")) {
            return Optional.of(new FixedMoveTTTAI(aiConfig));
        }
        return Optional.empty();
    }

    @Override
    public TTTGame startGame(LobbyGame<TTTGame> lobbyGame) {
        if (lobbyGame.getPlayers().size() != 2) {
            return null;
        }
        // TODO: ? lobbyGame.shufflePlayerOrder();
        TTTGame gameObj = new TTTGame();
        return gameObj;
    }

    @Override
    public InternalActionResult performAction(RunningGame<TTTGame> running, PlayerInGame player, String actionType, Object actionData) {
        switch (actionType) {
            case "move":
                TTTGame game = running.getGame();
                Point point = mapper.convertValue(actionData, Point.class);
                if (game.getTurn().ordinal() != player.getIndex()) {
                    return new InternalActionResult(false, "Not your turn");
                }
                TTPlayer pieceAtPos = game.getBoard()[point.y][point.x];
                if (pieceAtPos != null) {
                    return new InternalActionResult(false, "Position already taken");
                }
                game.move(game.getPlayerByIndex(player.getIndex()), point.x, point.y);
                return new InternalActionResult(true, "");
            default:
                return new InternalActionResult(false, "Unknown action");
        }
    }

    @Override
    public Object gameSummary(RunningGame<TTTGame> game) {
        return new Object();
    }

    @Override
    public Object gameDetails(RunningGame<TTTGame> game, PlayerInGame fromWhosPerspective) {
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ArrayNode node = nodeFactory.arrayNode(3);
        TTTGame board = game.getGame();
        for (int y = 0; y < 3; y++) {
            ArrayNode row = nodeFactory.arrayNode(3);
            for (int x = 0; x < 3; x++) {
                TTPlayer value = board.getBoard()[y][x];
                if (value != null) {
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
