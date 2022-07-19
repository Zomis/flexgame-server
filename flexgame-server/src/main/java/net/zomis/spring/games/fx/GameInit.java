package net.zomis.spring.games.fx;

import net.zomis.tttultimate.games.TTController;

import java.util.function.Supplier;

public class GameInit {

    private final String name;
    private final Supplier<TTController> controller;
    private final boolean twoPlayer;

    public GameInit(String name, Supplier<TTController> controller, boolean twoPlayer) {
        this.name = name;
        this.controller = controller;
        this.twoPlayer = twoPlayer;
    }

    @Override
    public String toString() {
        return name;
    }

    public TTController create() {
        return controller.get();
    }

    public boolean isTwoPlayer() {
        return twoPlayer;
    }

}
