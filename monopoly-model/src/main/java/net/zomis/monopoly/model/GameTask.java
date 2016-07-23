package net.zomis.monopoly.model;

import java.util.Arrays;

public class GameTask {

    public Player getActor() {
        return actor;
    }

    public Property getProperty() {
        return property;
    }

    public static enum GameTaskType {
        BUY_OR_NOT,
        CHOOSE_NUMBER,
        PAY_MONEY,
        ESCAPE_JAIL_OPTIONS, ROLL;
    }

    private final GameTaskType type;
    private Player actor;
    private Player target;
    private Property property;
    private int amount;
    private int[] options;

    public int getAmount() {
        return amount;
    }

    public Player getTarget() {
        return target;
    }

    public int[] getOptions() {
        return Arrays.copyOf(options, options.length);
    }

    public GameTask(GameTaskType type) {
        this.type = type;
    }

    public GameTaskType getType() {
        return type;
    }

    public static GameTask buyOrNot(Player player, Property tile) {
        GameTask task = new GameTask(GameTaskType.BUY_OR_NOT);
        task.actor = player;
        task.property = tile;
        return task;
    }

}
