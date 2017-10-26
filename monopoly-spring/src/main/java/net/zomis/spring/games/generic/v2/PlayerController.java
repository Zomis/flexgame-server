package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.PlayerInGame;

import java.util.Optional;

public interface PlayerController<G> {

    Optional<ActionV2> control(G game, PlayerInGame player);

}
