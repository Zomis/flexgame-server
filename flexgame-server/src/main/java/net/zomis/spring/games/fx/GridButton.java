package net.zomis.spring.games.fx;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;

import java.util.function.Consumer;

public class GridButton extends Button {

    private final TTController controller;
    private final GridPane grid;
    private final int x;
    private final int y;

    public GridButton(TTController controller, GridPane grid, int x, int y) {
        this.controller = controller;
        this.grid = grid;
        this.x = x;
        this.y = y;
    }

    public void update() {
        TTBase sub = controller.getGame().getSub(x, y);
        this.setDisable(!controller.isAllowedPlay(controller.getGame().getSub(x, y)));
        TTPlayer player = sub.getWonBy();
        this.setText(player == TTPlayer.NONE ? "" : player.name());
    }

    public TTBase getField() {
        return controller.getGame().getSub(x, y);
    }

    public static GridButton create(GridPane grid, TTController controller, int x, int y, Consumer<GridButton> handler) {
        GridButton button = new GridButton(controller, grid, x, y);
        button.update();
        button.setMaxHeight(100);
        button.setMaxWidth(100);
        button.setStyle("-fx-font: 21 arial;");
        button.setOnAction(eh -> handler.accept(button));
        grid.getChildren().add(button);
        GridPane.setConstraints(button, x, y);

        return button;
    }

    public int getIndex() {
        TTBase sub = this.getField();
        return sub.getY() * sub.getParent().getSizeX() + sub.getX();
    }

}
