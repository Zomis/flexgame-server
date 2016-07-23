package net.zomis.monopoly.spring;

import net.zomis.monopoly.model.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameDetails {

    private List<GamePlayer> players;
    private List<GameProperty> properties = new ArrayList<>();
    private int currentPlayer;
    private String state;

    private Map<String, Object> settings;

    public static GameDetails forGame(Game game) {
        GameDetails details = new GameDetails();
        details.players = game.getPlayers().stream().map(GamePlayer::new).collect(Collectors.toList());
        details.properties = IntStream.range(0, game.getTileCount())
            .mapToObj(game::getProperty)
            .map(GameProperty::new)
            .collect(Collectors.toList());
        details.currentPlayer = game.getCurrentPlayer().getIndex();
        // details.state = game stack...
        details.settings = new HashMap<>();
        details.settings.put("speedDie", game.getSetup().isSpeedDie());

        return details;
    }

}
