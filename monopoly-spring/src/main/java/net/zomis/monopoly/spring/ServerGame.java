package net.zomis.monopoly.spring;

import net.zomis.monopoly.model.Game;
import net.zomis.monopoly.model.Player;
import net.zomis.monopoly.model.actions.RollDiceAction;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ServerGame {

    private final UUID uuid;
    private final Game object;
    private final Map<String, UUID> playerKeys = new HashMap<>();
    private final Random random = new Random(42);

    public ServerGame(Game object) {
        this.uuid = UUID.randomUUID();
        this.object = object;
    }

    public GameInfo getGameInfo() {
        return new GameInfo(uuid, object.getPlayers().stream().map(Player::getName).collect(Collectors.toList()), Instant.now().toEpochMilli(), object.getPlayers().size(), true);
    }

    public UUID getUUID() {
        return uuid;
    }

    public ResponseEntity<JoinGameResponse> addPlayer(String playerName, String piece) {
        if (playerKeys.containsKey(playerName)) {
            return ResponseEntity.badRequest().body(null);
        }
        UUID playerKey = UUID.randomUUID();
        playerKeys.put(playerName, playerKey);

        return ResponseEntity.ok(new JoinGameResponse(playerKey));
    }

    public GameDetails getGameDetails() {
        return GameDetails.forGame(object);
    }

    public Random getRandom() {
        return random;
    }

    public Optional<ServerGamePlayer> authorize(String playerUUID) {
        return playerKeys.entrySet().stream()
            .filter(e -> e.getValue().toString().equals(playerUUID))
            .findFirst()
            .map(Map.Entry::getKey)
            .map(this::getGamePlayerForName);
    }

    private ServerGamePlayer getGamePlayerForName(String name) {
        return object.getPlayers().stream().filter(p -> p.getName().equals(name))
            .findFirst()
            .map(p -> new ServerGamePlayer(this, p))
            .orElseThrow(() -> new IllegalArgumentException("No player found with name " + name));
    }

}
