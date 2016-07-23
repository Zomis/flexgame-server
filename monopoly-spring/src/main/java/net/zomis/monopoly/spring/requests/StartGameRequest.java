package net.zomis.monopoly.spring.requests;

public class StartGameRequest {

    private String playerName;
    private String password;
    private String piece;
    private boolean speedDie;

    public boolean isSpeedDie() {
        return speedDie;
    }

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

    public void setSpeedDie(boolean speedDie) {
        this.speedDie = speedDie;
    }

    @Override
    public String toString() {
        return "StartGameRequest{" +
                "playerName='" + playerName + '\'' +
                ", speedDie=" + speedDie +
                '}';
    }

    public String getPiece() {
        return piece;
    }

}
