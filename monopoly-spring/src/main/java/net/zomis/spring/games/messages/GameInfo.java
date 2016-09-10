package net.zomis.spring.games.messages;

import java.util.List;
import java.util.UUID;

public class GameInfo {

    private final UUID uuid;
    private final List<String> players;
    private final long lastActivity;
    private final int maxPlayers;
    private final boolean started;

    public GameInfo(UUID uuid, List<String> players, long lastActivity, int maxPlayers, boolean started) {
        this.uuid = uuid;
        this.players = players;
        this.lastActivity = lastActivity;
        this.maxPlayers = maxPlayers;
        this.started = started;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isStarted() {
        return started;
    }

}
