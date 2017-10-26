package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.PlayerInGame;

import java.util.Optional;

public interface GameHelper2<G> {

    /**
     * Validate game configuration and return whether or not it's allowed.
     *
     * @param gameConfiguration Game configuration object
     * @return A LobbyGame if success, otherwise anything else.
     */
    // TODO: Should this maybe throw an Exception? Or return ActionResult?
    // TODO: Make an interface for ActionResult to easier refactor later on
    LobbyGame<G> createGame(Object gameConfiguration);
    ActionResult playerJoin(LobbyGame<G> game, Object playerConfiguration);

    default Optional<PlayerController<G>> inviteAI(LobbyGame<G> game, String aiName, Object aiConfig, Object playerConfiguration) {
        return Optional.empty();
    }

    // TODO: Make a flexible ECS GameHelper. See also: Server, Lachesis, Cardshifter
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
    // TODO: Web sockets should only recieve updates, not full-game status every time. Use some listener?

}
