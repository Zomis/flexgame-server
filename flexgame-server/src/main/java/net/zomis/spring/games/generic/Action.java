package net.zomis.spring.games.generic;

public class Action {

    private final Object game;
    private final int playerIndex;
    private final Object actionData;

    public Action(Object game, PlayerInGame player, Object actionData) {
        this.game = game;
        this.playerIndex = player.getIndex();
        this.actionData = actionData;
    }

    public Object getGame() {
        return game;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public Object getActionData() {
        return actionData;
    }

}
