package net.zomis.spring.games.messages;

public class JoinGameRequest {

    private String playerName;
    private Object playerConfig;

    public String getPlayerName() {
        return playerName;
    }

    public Object getPlayerConfig() {
        return playerConfig;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerConfig(Object playerConfig) {
        this.playerConfig = playerConfig;
    }

}
