package net.zomis.monopoly.spring.messages;

public class JoinGameRequest {

    private String playerName;
    private String piece;

    public String getPlayerName() {
        return playerName;
    }

    public String getPiece() {
        return piece;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

}
