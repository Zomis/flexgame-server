package net.zomis.spring.games

import net.zomis.spring.games.generic.GroovyGames
import org.junit.Before
import org.junit.Test

class GamesTest {

    GroovyGames games;

    @Before
    void setup() {
        games = new GroovyGames();
        games.initialize(new InputStreamReader(this.class.classLoader.getResourceAsStream("monopoly.groovy")));
    }

    @Test
    void monopolyShouldBeAvailable() {
        assert games.getGame('monopoly')
    }

}
