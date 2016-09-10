package net.zomis.spring.games.monopoly;

import net.zomis.monopoly.model.*;

public class GamePlayer {

    private final String name;
    private final int index;
    private final String piece;
    private final int position;
    private final int money;

    public GamePlayer(Player player) {
        this.name = player.getName();
        this.index = player.getIndex();
        this.piece = player.getPiece().toString();
        this.position = player.getPosition();
        this.money = player.getMoney();
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int getMoney() {
        return money;
    }

    public int getPosition() {
        return position;
    }

    public String getPiece() {
        return piece;
    }

}
