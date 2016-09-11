package net.zomis.spring.games.messages;

import java.util.UUID;

public class CreateGameResponse {

    private final UUID gameId;
    private final UUID privateKey;

    public CreateGameResponse(UUID gameId, UUID privateKey) {
        this.gameId = gameId;
        this.privateKey = privateKey;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getPrivateKey() {
        return privateKey;
    }

}
