package net.zomis.spring.games.impls.ur;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.zomis.spring.games.Hashids;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.generic.v2.*;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.*;
import static net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs.leaveSafety;

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
    public Optional<PlayerController<RoyalGameOfUr>> inviteAI(LobbyGame<RoyalGameOfUr> game, String aiName, Object aiConfig, Object playerConfiguration) {
        if (aiName.equals("KFE521S3")) {
            URScorer ai = new URScorer("KFE521S3", RoyalGameOfUrAIs.scf()
                    .withScorer(knockout, 5)
                    .withScorer(gotoFlower, 2)
                    .withScorer(gotoSafety, 0.1)
                    .withScorer(leaveSafety, -0.1)
                    .withScorer(riskOfBeingTaken, -0.1)
                    .withScorer(exit));
            return Optional.of(ai);
        }
        if (aiName.equals("KFE521S2")) {
            URScorer ai = new URScorer("KFE521S2", RoyalGameOfUrAIs.scf()
                    .withScorer(knockout, 5)
                    .withScorer(gotoFlower, 2)
                    .withScorer(gotoSafety, 0.1)
                    .withScorer(leaveSafety, -0.1)
                    .withScorer(exit));
            return Optional.of(ai);
        }
        return Optional.empty();
    }

    @Override
    public RoyalGameOfUr startGame(LobbyGame<RoyalGameOfUr> lobbyGame) {
        if (lobbyGame.getPlayers().size() != 2) {
            return null;
        }
        return new RoyalGameOfUr();
    }

    @Override
    public InternalActionResult performAction(RunningGame<RoyalGameOfUr> running, PlayerInGame player, String actionType, Object actionData) {
        RoyalGameOfUr game = running.getGame();
        return performAction(game, player.getIndex(), actionType, actionData);
    }

    public InternalActionResult performAction(RoyalGameOfUr game, int index, String actionType, Object actionData) {
        if (game.isFinished()) {
            return new InternalActionResult(false, "Game Over. Winner is " + game.getWinner());
        }
        switch (actionType) {
            case "roll":
                if (game.getCurrentPlayer() != index) {
                    return new InternalActionResult(false, "Not your turn");
                }
                if (!game.isRollTime()) {
                    return new InternalActionResult(false, "You need to move");
                }
                int roll = game.roll();
                return new InternalActionResult(true, "Rolled " + roll, roll);
            case "move":
                int pos = mapper.convertValue(actionData, Integer.class);
                if (game.getCurrentPlayer() != index) {
                    return new InternalActionResult(false, "Not your turn");
                }
                if (!game.isMoveTime()) {
                    return new InternalActionResult(false, "Roll first");
                }
                boolean ok = game.move(index, pos, game.getRoll());
                return new InternalActionResult(ok, ok ? "" : "Cannot move that");
            default:
                return new InternalActionResult(false, "Unknown action");
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
