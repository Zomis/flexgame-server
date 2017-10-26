package net.zomis.spring.games.messages;

import java.util.UUID;

public class JoinGameResponse {

    private final String privateKey;

    public JoinGameResponse(UUID uuid) {
        this(uuid.toString());
    }

    public JoinGameResponse(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

}
