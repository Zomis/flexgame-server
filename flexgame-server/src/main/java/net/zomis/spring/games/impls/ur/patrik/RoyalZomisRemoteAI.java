package net.zomis.spring.games.impls.ur.patrik;

import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.impls.ur.RoyalGameOfUr;
import net.zomis.spring.games.impls.ur.RoyalGameOfUrAIs;
import se.stromvap.royal.game.of.ur.grpc.RemoteAi;
import se.stromvap.royal.game.of.ur.model.Game;
import se.stromvap.royal.game.of.ur.model.GamePiece;
import se.stromvap.royal.game.of.ur.model.Player;
import se.stromvap.royal.game.of.ur.model.Tile;

import java.util.List;
import java.util.Optional;

public class RoyalZomisRemoteAI implements RemoteAi {

    private final RoyalGameOfUrAIs.AI ai;

    public RoyalZomisRemoteAI(RoyalGameOfUrAIs.AI ai) {
        this.ai = ai;
    }

    @Override
    public String getName() {
        return "Simon";
    }

    @Override
    public GamePiece yourTurn(Game game) {
        RoyalGameOfUr zomisUR = mapToZomis(game);

        int currentPlayer = game.getStatus().getCurrentTurnPlayer() == game.getPlayer1() ? 0 : 1;
        PlayerInGame playerInGame = new PlayerInGame(getName(), currentPlayer, "", null, ai);
        Optional<ActionV2> action = ai.control(zomisUR, playerInGame);
        int position = action.orElseThrow(() -> new IllegalStateException("No response from AI " + ai))
            .getActionData().asInt();
        return mapToZomisMove(zomisUR, game, position);
    }

    @Override
    public void gameOver(Player player) {

    }

    private GamePiece mapToZomisMove(RoyalGameOfUr zomisUR, Game game, int positionOfBest) {
        List<Tile> tiles = game.getBoard().getTiles().get(game.getStatus().getCurrentTurnPlayer());
        if (positionOfBest == 0) {
            return game.getStatus().getCurrentTurnPlayer().getGamePieces().stream()
                    .filter(piece -> tiles.stream().noneMatch(tile -> tile.getGamePiece() == piece)).findAny()
                    .orElseThrow(() -> new IllegalStateException("No GamePiece found for " + positionOfBest));
        }
        Tile tile = tiles.get(positionOfBest - 1);

        return tile.getGamePiece();
    }

    private RoyalGameOfUr mapToZomis(Game game) {
        int currentPlayer = game.getStatus().getCurrentTurnPlayer() == game.getPlayer1() ? 0 : 1;
        int[][] pieces = new int[2][];
        pieces[0] = mapInPlay(game, game.getPlayer1());
        pieces[1] = mapInPlay(game, game.getPlayer2());
        return new RoyalGameOfUr(currentPlayer, game.getStatus().getLatestRoll(), pieces);
    }

    private int[] mapInPlay(Game game, Player who) {
        int[] result = new int[7];
        int size = who.getGamePieces().size();
        int out = 7 - size;
        for (int i = 0; i < out; i++) {
            result[i] = 15;
        }
        List<Tile> board = game.getBoard().getTiles().get(who);
        int pieceIndex = out;
        for (int i = 0; i < board.size(); i++) {
            Tile tile = board.get(i);
            if (tile.getGamePiece() != null && tile.getGamePiece().getPlayer() == who) {
                result[pieceIndex] = i + 1;
                pieceIndex++;
            }
        }

        return result;
    }

}
