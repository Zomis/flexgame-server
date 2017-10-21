package net.zomis.spring.games

import net.zomis.spring.games.generic.GroovyGames
import net.zomis.spring.games.messages.CreateGameRequest
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus

class GamesTest {

    GroovyGames games;

    @Before
    void setup() {
        games = new GroovyGames();
        games.initialize(new InputStreamReader(this.class.classLoader.getResourceAsStream("ttt.groovy")));
    }

    @Test
    void ticTacToeShouldBeAvailable() {
        assert games.getGame('ttt')
    }

    @Test
    void ticTacToeShouldBeStartable() {
        def game = games.getGame('ttt')
        def start = new CreateGameRequest();
        start.playerName = 'PlayerX'
        start.playerConfig = new Expando();
        start.playerConfig.player = 'X'
        def result = game.startNewGame(start);
        assert result.statusCode == HttpStatus.CREATED
        assert result.body
    }

}
