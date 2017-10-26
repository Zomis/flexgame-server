package net.zomis.spring.games.impls;

import com.fasterxml.jackson.databind.JsonNode;
import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.v2.ActionV2;
import net.zomis.spring.games.generic.v2.PlayerController;

import java.util.Optional;

/**
 * AI for Tic-Tac-Toe that always moves according to a set of pre-fixed moves
 */
public class FixedMoveTTTAI implements PlayerController<TTTGame> {

    private final JsonNode config;

    public FixedMoveTTTAI(Object aiConfig) {
        this.config = (JsonNode) aiConfig;
    }

    @Override
    public Optional<ActionV2> control(TTTGame game, PlayerInGame player) {
        for (JsonNode node : config.get("moves")) {
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();
            if (game.getBoard()[y][x] == null) {
                return Optional.of(new ActionV2("move", node));
            }
        }
        return Optional.empty();
    }

}
