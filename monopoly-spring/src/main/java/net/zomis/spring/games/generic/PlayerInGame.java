package net.zomis.spring.games.generic;

public class PlayerInGame {

    private final String name;
    private final int index;
    private final String authToken;
    private final Object game;

    public PlayerInGame(String name, int index, String authToken, Object game) {
        this.name = name;
        this.index = index;
        this.authToken = authToken;
        this.game = game;
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
                ", authToken='" + authToken + '\'' +
//                ", game=" + game +
                '}';
    }

}
