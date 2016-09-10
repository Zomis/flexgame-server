package net.zomis.spring.games.monopoly;

public class GameStatus {

    private final long id;
    private final String format;

    public GameStatus(long id, String format) {
        this.id = id;
        this.format = format;
    }

    public long getId() {
        return id;
    }

    public String getFormat() {
        return format;
    }

}
