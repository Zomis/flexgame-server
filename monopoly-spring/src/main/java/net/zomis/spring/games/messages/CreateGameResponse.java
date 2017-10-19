package net.zomis.spring.games.messages;

public class CreateGameResponse {

    private final String gameId;
    private final String privateKey;

    public CreateGameResponse(String gameId, String privateKey) {
        this.gameId = gameId;
        this.privateKey = privateKey;
    }

    public String getGameId() {
        return gameId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

}
