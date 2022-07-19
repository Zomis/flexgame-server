package net.zomis.spring.games.fx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxGame extends Application {

    public static void main(String[] args) {
        Application.launch(FxGame.class);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Game");
        Parent root = new FxMainController().getRoot();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

}
