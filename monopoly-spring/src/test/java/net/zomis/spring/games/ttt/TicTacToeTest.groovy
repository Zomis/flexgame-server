package net.zomis.spring.games.ttt

import net.zomis.spring.test.RestTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicTacToeTest {

    @Value('${local.server.port}')
    private int port;

    private RestTest test;


    @Before
    void setup() {
        test = RestTest.localhost(port);
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
        String key2 = result.privateKey;

        test.post("games/ttt/$gameId/start", {
            privateKey key1
        }).with {
            assert it;
        }

        test.post("games/ttt/$gameId/action", {
            privateKey key1
            action 'move'
            actionParams {
                x 1
                y 1
            }
        }).with {
            actionResult 'ok'
        }
    }

}
