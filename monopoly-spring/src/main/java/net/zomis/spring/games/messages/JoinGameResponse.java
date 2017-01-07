package net.zomis.spring.games.messages;

import java.util.UUID;

public class JoinGameResponse {

    private final UUID privateKey;

    public JoinGameResponse(UUID uuid) {
        this.privateKey = uuid;
    }

    public String getPrivateKey() {
        return privateKey.toString();
    }

}
