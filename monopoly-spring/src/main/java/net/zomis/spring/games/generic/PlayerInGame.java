package net.zomis.spring.games.generic;

public class PlayerInGame {

    private final String name;
    private final int index;
    private final String authToken;

    public PlayerInGame(String name, int index, String authToken) {
        this.name = name;
        this.index = index;
        this.authToken = authToken;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean hasToken(String authToken) {
        return this.authToken.equals(authToken);
    }

    @Override
    public String toString() {
        return "PlayerInGame{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }

}
