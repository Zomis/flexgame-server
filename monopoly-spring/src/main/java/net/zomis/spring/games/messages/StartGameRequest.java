package net.zomis.spring.games.messages;

public class StartGameRequest {

    private String playerName;
    private String password;
    private Object playerConfig;
    private Object gameConfig;

    public String getPassword() {
        return password;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Object getGameConfig() {
        return gameConfig;
    }

    public Object getPlayerConfig() {
        return playerConfig;
    }

    public void setGameConfig(Object gameConfig) {
        this.gameConfig = gameConfig;
    }

    public void setPlayerConfig(Object playerConfig) {
        this.playerConfig = playerConfig;
    }

    @Override
    public String toString() {
        return "StartGameRequest{" +
                "playerName='" + playerName + '\'' +
                ", password='" + password + '\'' +
                ", playerConfig=" + playerConfig +
                ", gameConfig=" + gameConfig +
                '}';
    }

}
