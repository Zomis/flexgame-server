package net.zomis.spring.games.v2

import net.zomis.spring.test.RestTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TTTV2Test {

    @Value('${local.server.port}')
    private int port;

    private RestTest test;

    @Before
    void setup() {
        test = RestTest.localhost(port);
    }

    @Test
    public void tttExists() {
        def result = test.get('games2')
        assert result.contains('ttt')
    }

    @Test
    public void playFullGame() {
        def result = test.post('games2/ttt', {
            playerName 'Zomis'
        })
        assert result
        def gameKey = result.gameId
        def p1key = result.privateKey

        // Setup game
        result = test.get("games2/ttt/$gameKey")
        assert result.id == gameKey
        assert result.players == ['Zomis']
        assert result.started == false

        // Player 2 joins
        result = test.post("games2/ttt/$gameKey/join", {
            playerName 'Test2'
        })
        assert result
        assert result.privateKey
        def p2key = result.privateKey
        result = test.get("games2/ttt/$gameKey")
        assert result.players == ['Zomis', 'Test2']

        // Game starts
        result = test.post("games2/ttt/$gameKey/start", {})
        assert result.started == true

        result = test.get("games2/ttt/$gameKey")
        assert result.id == gameKey
        assert result.players == ['Zomis', 'Test2']
        assert result.started == true

        // Play moves
        def move1 = "games2/ttt/$gameKey/actions/move?token=$p1key"
        def move2 = "games2/ttt/$gameKey/actions/move?token=$p2key"
        move(move1, 0, 0)
        move(move2, 0, 1)

        move(move1, 1, 0)
        move(move2, 1, 1)
        move(move1, 2, 2)
        move(move2, 2, 1) // 'O' wins by horizontal center (01 11 21)

        result = test.get("games2/ttt/$gameKey/details")
        assert result
        // Game has ended
    }

    private void move(String url, int posX, int posY) {
        def result = test.post(url, { x posX; y posY })
        assert result.ok
        def details = url.substring(0, url.indexOf("/actions")) + '/details'
        assert test.get(details)
    }

    @Test
    public void illegalMove() {

    }

}
