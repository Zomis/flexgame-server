package net.zomis.spring.games.messages;

public class StartGameResponse {

    private final boolean started;

    public StartGameResponse(boolean started) {
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }

}
