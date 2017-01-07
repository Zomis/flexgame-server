package net.zomis.spring.games.ttt

import net.zomis.spring.games.generic.GroovyGames
import net.zomis.spring.test.RestTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicTacToeTest {

    @Value('${local.server.port}')
    private int port;

    private RestTest test;

    @Autowired
    private GroovyGames games;

    @Before
    void setup() {
        test = RestTest.localhost(port);
        games.initialize(new InputStreamReader(this.class.classLoader.getResourceAsStream('ttt.groovy')))
    }

    @Test
    void availableGames() {
        def result = test.get('games').with {
            assert (it as Set<String>).contains('ttt')
            assert (it as Set<String>).contains('monopoly')
        }
    }

    @Test
    void playSimpleGame() {
        def result = test.post('games/ttt', {
            playerName 'Player1'
            playerConfig { player 'X' }
        });
        result.with {
            assert it;
            assert it.gameId;
            assert it.privateKey;
        }
        String gameId = result.gameId;
        String key1 = result.privateKey;

        result = test.post("games/ttt/$gameId/join", {
            playerName 'Player2'
        })
        result.with {
            assert it;
            assert it.privateKey;
        }
        String key2 = result.privateKey;

        test.post("games/ttt/$gameId/start", {
            privateKey key1
        }).with {
            assert it.started == true;
        }

        test.post("games/ttt/$gameId/actions/move?token=" + key1, {
            x 1
            y 1
        }).with {
            assert it.status == 'ok'
        }

        test.get("games/ttt/$gameId/details").with {
            assert it.turn == 'O'
            assert it.board == [[null,null,null], [null,'X',null], [null,null,null]];
        }
    }

}
