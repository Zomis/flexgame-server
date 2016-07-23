package net.zomis.monopoly.spring;

import net.zomis.monopoly.model.Player;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.springframework.http.ResponseEntity;

public class ServerGamePlayer {

    public ServerGamePlayer(ServerGame serverGame, Player p) {

    }

    public ResponseEntity<GameMoveResult> makeMove(RollDiceAction roll) {
        return null;
    }

}
