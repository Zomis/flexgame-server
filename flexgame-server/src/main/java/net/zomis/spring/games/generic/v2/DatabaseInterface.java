package net.zomis.spring.games.generic.v2;

public interface DatabaseInterface<G> {

    void startGame(LobbyGame<G> lobbyGame, RunningGame<G> running);
    void action(RunningGame<G> game, ActionV2 action, InternalActionResult result);

}
