package net.zomis.monopoly.model;

public class GameActionResult {

    private final boolean ok;
    private final String message;

    public GameActionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOk() {
        return ok;
    }

}
