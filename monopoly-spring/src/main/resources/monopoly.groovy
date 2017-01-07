import net.zomis.monopoly.model.Game
import net.zomis.monopoly.model.GameSetup
import net.zomis.monopoly.model.MonopolyConfig
import net.zomis.monopoly.model.Piece
import net.zomis.monopoly.model.Player
import net.zomis.monopoly.model.actions.BidAction
import net.zomis.monopoly.model.actions.BuildAction

/*
common parts:
- join games, view current games, lobby chat, in-game chat
- configuration step
- /summary endpoint info
- replays
- observing games (no notifying the players, and no chatting - or possibly opt-in)
- checking if game exists, creating games
- authenticating players (get some token when joining)
*/

/*
make replays more script-like?
return a list of events that are called internally? (public events, private events?)
*/

game("monopoly", Game.class) {
//    stateful true
    players 2 to 8
    setup(MonopolyConfig.class) {MonopolyConfig config ->
        GameSetup setup = new GameSetup()
            .withSpeedDie(config.speedDie);
        return new Game(setup);
    }
    playerConfig(Piece.class)
    actions {
        System.out.println "Initializing Monopoly actions..."
        // ActionController.addAvailableAction(action)
        // some systems can prevent action
        action 'roll', { Game game, Player who ->

        }
        action 'choose', String.class, { Game game, Player who ->
            // action
        }
        action 'jailChoice', String.class, { Game game, Player who ->

        }
        action 'bid', BidAction.class, { Game game, Player who ->
            // action.bid
        }
        action 'build', BuildAction.class, { Game game, Player who ->
            // action.stuff
        }
    }
    gameInfo { Game game, Player who ->
        // http://docs.groovy-lang.org/latest/html/gapi/groovy/json/JsonBuilder.html
        // create a dynamic object
    }
}
/*
class ShipConfig {
    String name
    int x, y
}
class BattleshipConfig {
    List<ShipConfig> ships
}

game("Battleship") {
    players 2
    config(BattleshipConfig.class)
    actions {
        place(PlaceOptions.class) { Game game, Player who ->
            // during setup phase, place ships
        }
        move(PointXY.class) { Game game, Player who ->
            // after setup phase, make a move
        }
    }

}
*/
