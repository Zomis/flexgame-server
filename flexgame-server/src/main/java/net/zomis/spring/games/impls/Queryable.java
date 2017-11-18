package net.zomis.spring.games.impls;

import java.util.Collection;

public interface Queryable<G> {

    Collection<ActionScore> query(G game);

}
