package net.zomis.spring.games.generic;

import net.zomis.spring.games.messages.GameMoveResult;

public interface GameHelper<C, G> {

    G constructGame(C configuration);
    void addPlayer(Object playerConfig);
    G start();
    GameMoveResult performAction(int playerIndex, Object action);
    Object gameDetails(G game);

}
