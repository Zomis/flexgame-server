package net.zomis.spring.games.messages;

public class GameMoveResult {

    private final String status;

    public GameMoveResult(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "GameMoveResult{" +
                "status='" + status + '\'' +
                '}';
    }

}
