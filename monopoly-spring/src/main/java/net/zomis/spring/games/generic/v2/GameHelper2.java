package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.PlayerInGame;

public interface GameHelper2<G> {

    LobbyGame<G> createGame(Object gameConfiguration);
    ActionResult playerJoin(LobbyGame<G> game, Object playerConfiguration);
    G startGame(LobbyGame<G> game);
    ActionResult performAction(RunningGame<G> game, PlayerInGame player, String actionType, Object actionData);
    Object gameSummary(RunningGame<G> game);
    Object gameDetails(RunningGame<G> game, PlayerInGame fromWhosPerspective);

}
