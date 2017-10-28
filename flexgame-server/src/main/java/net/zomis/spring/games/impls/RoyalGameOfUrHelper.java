package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.zomis.spring.games.Hashids;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.generic.v2.ActionResult;
import net.zomis.spring.games.generic.v2.GameHelper2;
import net.zomis.spring.games.generic.v2.LobbyGame;
import net.zomis.spring.games.generic.v2.RunningGame;

import java.util.concurrent.atomic.AtomicInteger;

public class RoyalGameOfUrHelper implements GameHelper2<RoyalGameOfUr> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Hashids idGenerator = new Hashids(getClass().getSimpleName());
    private final AtomicInteger gameId = new AtomicInteger();

    @Override
    public LobbyGame<RoyalGameOfUr> createGame(Object gameConfiguration) {
        String id = idGenerator.encrypt(gameId.incrementAndGet());
        return new LobbyGame<>(id, new TokenGenerator(), this, gameConfiguration);
    }

    @Override
    public ActionResult playerJoin(LobbyGame<RoyalGameOfUr> game, Object playerConfiguration) {
        if (game.getPlayers().size() < 2) {
            return new ActionResult(true, "OK");
        }
        return new ActionResult(false, "Game is full");
    }

    @Override
    public RoyalGameOfUr startGame(LobbyGame<RoyalGameOfUr> lobbyGame) {
        if (lobbyGame.getPlayers().size() != 2) {
            return null;
        }
        return new RoyalGameOfUr();
    }

    @Override
    public ActionResult performAction(RunningGame<RoyalGameOfUr> running, PlayerInGame player, String actionType, Object actionData) {
        RoyalGameOfUr game = running.getGame();
        switch (actionType) {
            case "roll":
                if (game.getCurrentPlayer() != player.getIndex()) {
                    return new ActionResult(false, "Not your turn");
                }
                if (!game.isRollTime()) {
                    return new ActionResult(false, "You need to move");
                }
                int roll = game.roll();
                return new ActionResult(true, "Rolled " + roll);
            case "move":
                int pos = mapper.convertValue(actionData, Integer.class);
                if (game.getCurrentPlayer() != player.getIndex()) {
                    return new ActionResult(false, "Not your turn");
                }
                if (!game.isMoveTime()) {
                    return new ActionResult(false, "Roll first");
                }
                boolean ok = game.move(player.getIndex(), pos, game.getRoll());
                return new ActionResult(ok, ok ? "" : "Cannot move that");
            default:
                return new ActionResult(false, "Unknown action");
        }
    }

    @Override
    public Object gameSummary(RunningGame<RoyalGameOfUr> game) {
        return new Object();
    }

    @Override
    public Object gameDetails(RunningGame<RoyalGameOfUr> running, PlayerInGame fromWhosPerspective) {
        RoyalGameOfUr game = running.getGame();
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode root = nodeFactory.objectNode();
        root.set("turn", nodeFactory.numberNode(game.getCurrentPlayer()));
        root.set("roll", nodeFactory.numberNode(game.getRoll()));
        ArrayNode pos = nodeFactory.arrayNode(2);
        int[][] pieces = game.getPieces();
        for (int i = 0; i < pieces.length; i++) {
            ArrayNode piece2 = nodeFactory.arrayNode(pieces[i].length);
            for (int j = 0; j < pieces[i].length; j++) {
                piece2.add(pieces[i][j]);
            }
            pos.add(piece2);
        }
        root.set("positions", pos);
        return root;
    }

}