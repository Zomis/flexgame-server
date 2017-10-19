package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.PlayerInGame;

public interface GameHelper2<G> {

    /**
     * Validate game configuration and return whether or not it's allowed.
     *
     * @param gameConfiguration Game configuration object
     * @return A LobbyGame if success, otherwise anything else.
     */
    // TODO: Should this maybe throw an Exception? Or return ActionResult?
    LobbyGame<G> createGame(Object gameConfiguration);
    ActionResult playerJoin(LobbyGame<G> game, Object playerConfiguration);

    /**
     * Validate Lobby game (number of players specifically) and return created game
     *
     * @param game LobbyGame that wants to be started
     * @return Game that was created, or null if game was not allowed to be started
     */
    // TODO: Use Either<G, String> or something to return failure status
    G startGame(LobbyGame<G> game);
    ActionResult performAction(RunningGame<G> game, PlayerInGame player, String actionType, Object actionData);
    Object gameSummary(RunningGame<G> game);
    Object gameDetails(RunningGame<G> game, PlayerInGame fromWhosPerspective);

}
