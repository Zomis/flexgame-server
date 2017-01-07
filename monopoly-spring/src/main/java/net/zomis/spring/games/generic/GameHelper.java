package net.zomis.spring.games.generic;

import com.fasterxml.jackson.core.TreeNode;
import net.zomis.spring.games.messages.GameMoveResult;

public interface GameHelper<C, G> {

    G constructGame(C configuration);
    void addPlayer(Object playerConfig);
    G start();
    GameMoveResult performAction(PlayerInGame playerInGame, String actionType, TreeNode action);
    Object gameDetails(G game);

}
