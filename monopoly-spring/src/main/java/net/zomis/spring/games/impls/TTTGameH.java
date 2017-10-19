package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.zomis.spring.games.Hashids;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.generic.v2.ActionResult;
import net.zomis.spring.games.generic.v2.GameHelper2;
import net.zomis.spring.games.generic.v2.LobbyGame;
import net.zomis.spring.games.generic.v2.RunningGame;

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
    public TTTGame startGame(LobbyGame<TTTGame> lobbyGame) {
        if (lobbyGame.getPlayers().size() != 2) {
            return null;
        }
        // TODO: ? lobbyGame.shufflePlayerOrder();
        TTTGame gameObj = new TTTGame();
        return gameObj;
    }

    @Override
    public ActionResult performAction(RunningGame<TTTGame> running, PlayerInGame player, String actionType, Object actionData) {
        switch (actionType) {
            case "move":
                TTTGame game = running.getGame();
                Point point = mapper.convertValue(actionData, Point.class);
                if (game.getTurn().ordinal() != player.getIndex()) {
                    return new ActionResult(false, "Not your turn");
                }
                TTPlayer pieceAtPos = game.getBoard()[point.y][point.x];
                if (pieceAtPos != null) {
                    return new ActionResult(false, "Position already taken");
                }
                game.move(game.getPlayerByIndex(player.getIndex()), point.x, point.y);
                return new ActionResult(true, "");
            default:
                return new ActionResult(false, "Unknown action");
        }
    }

    @Override
    public Object gameSummary(RunningGame<TTTGame> game) {
        return new Object();
    }

    @Override
    public Object gameDetails(RunningGame<TTTGame> game, PlayerInGame fromWhosPerspective) {
        return null;
    }
}
