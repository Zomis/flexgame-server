package net.zomis.monopoly.spring.messages;

import java.util.UUID;

public class JoinGameResponse {

    private final UUID uuid;

    public JoinGameResponse(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid.toString();
    }

}
