package net.zomis.spring.games.fx;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import net.zomis.spring.games.impls.MyQLearning;
import net.zomis.spring.games.impls.qlearn.QStore;
import net.zomis.spring.games.impls.qlearn.QStoreMap;
import net.zomis.spring.games.impls.qlearn.TTTQLearn;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTFactories;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTClassicController;
import net.zomis.tttultimate.games.TTClassicControllerWithGravity;
import net.zomis.tttultimate.games.TTController;
import net.zomis.tttultimate.games.TTOthello;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class FxMainController {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final ComboBox<GameInit> gameChoice;
    private final CheckBox learningEnabled;
    private GridPane grid;
    private TTController controller;
    private final FlowPane parent;
    private QStore<String> qstore = new QStoreMap<>();
    private MyQLearning<TTController, String> learner;
    private ToIntFunction<TTController> aiX;
    private ToIntFunction<TTController> aiO;
    private final ToIntFunction<TTController> learnAI = tt -> learner.pickAction(tt);
    private Label label;

    public FxMainController() {
        this.parent = new FlowPane(Orientation.VERTICAL);

        label = new Label("Welcome!");
        parent.getChildren().add(label);

        FlowPane buttons = new FlowPane();

        gameChoice = new ComboBox<>();
        TTFactories ttf = new TTFactories();
        gameChoice.getItems().add(new GameInit("TicTacToe", () -> new TTClassicController(ttf.classicMNK(3)), true));
        gameChoice.getItems().add(new GameInit("Connect4", () -> new TTClassicControllerWithGravity(ttf.classicMNK(7, 6, 4)), true));
        gameChoice.getItems().add(new GameInit("Othello", () -> new TTOthello(8), true));
        gameChoice.getItems().add(new GameInit("Click and Turn 3x3", () -> ClickAndTurnGame.cross(3), false));
        gameChoice.getItems().add(new GameInit("Click and Turn 4x4", () -> ClickAndTurnGame.cross(4), false));
        gameChoice.getItems().add(new GameInit("Click and Turn 4x4 Diagonals", () -> ClickAndTurnGame.diag(4), false));
        gameChoice.getSelectionModel().select(0);
        buttons.getChildren().add(gameChoice);

        learningEnabled = new CheckBox("Learning");
        learningEnabled.setSelected(true);
        learningEnabled.setOnAction(eh -> learner.setEnabled(learningEnabled.isSelected()));
        buttons.getChildren().add(learningEnabled);

        CheckBox opponentX = new CheckBox("AI for X");
        opponentX.setOnAction(eh -> {
            aiX = opponentX.isSelected() ? learnAI : null;
            scheduleAI();
        });
        buttons.getChildren().add(opponentX);

        CheckBox opponentO = new CheckBox("AI for O");
        opponentO.setOnAction(eh -> {
            aiO = opponentO.isSelected() ? learnAI : null;
            scheduleAI();
        });
        buttons.getChildren().add(opponentO);

        Button switchGame = new Button("Switch game");
        switchGame.setOnAction(eh -> switchGame());
        buttons.getChildren().add(switchGame);

        Button learnButton = new Button("Learn Play");
        learnButton.setOnAction(eh -> learnPlay());
        buttons.getChildren().add(learnButton);

        learnButton = new Button("Learn Until End");
        learnButton.setOnAction(eh -> learnRound());
        buttons.getChildren().add(learnButton);

        learnButton = new Button("Learn 1000");
        learnButton.setOnAction(eh -> learnRounds(1000));
        buttons.getChildren().add(learnButton);

        parent.getChildren().add(buttons);
        grid = new GridPane();
        parent.getChildren().add(grid);
        switchGame();
    }

    private void learnRounds(int games) {
        for (int i = 0; i < games; i++) {
            learnRound();
            switchGame();
        }
    }

    private void learnRound() {
        if (controller.isGameOver() || isDraw()) {
            switchGame();
            showLearnScores(-1);
            return;
        }
        int steps = 0;
        while (true) {
            if (controller.isGameOver() || isDraw()) {
                break;
            }
            steps++;
            int action = learner.pickAction(controller);
            learner.step(controller, TTTQLearn.performAction, action);
        }
        label.setText("Learned in " + steps + " steps");
    }

    private boolean isDraw() {
        return controller instanceof TTClassicController && TTTQLearn.isDraw(controller);
    }

    private void learnPlay() {
        if (controller.isGameOver() || isDraw()) {
            switchGame();
            showLearnScores(-1);
            return;
        }
        int action = learner.pickAction(controller);
        learner.step(controller, TTTQLearn.performAction, action);
        showLearnScores(action);
    }

    private void showLearnScores(int lastAction) {
        double[] actionScores = learner.getActionScores(controller);
        updateButtons(b -> String.format("%.3f", actionScores[b.getIndex()]));
        grid.getChildren().stream()
                .map(n -> (GridButton) n)
                .filter(n -> n.getIndex() == lastAction)
                .forEach(n -> n.setText("! " + n.getText()));
    }

    private void switchGame() {
        GameInit selected = this.gameChoice.getSelectionModel().selectedItemProperty().get();
        controller = selected.create();
        learner = TTTQLearn.newLearner(controller, qstore);
        learner.setDiscountFactor(selected.isTwoPlayer() ? -0.9 : 0.9);
        learner.setRandomMoveProbability(0.1);
        learner.setEnabled(learningEnabled.isSelected());
        setupGame();
    }

    private void setupGame() {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        TTBase board = controller.getGame();
        for (int x = 0; x < board.getSizeX(); x++) {
            grid.getColumnConstraints().add(new ColumnConstraints(100));
        }
        grid.getRowConstraints().clear();
        for (int y = 0; y < board.getSizeY(); y++) {
            grid.getRowConstraints().add(new RowConstraints(100));
        }

        for (int y = 0; y < board.getSizeY(); y++) {
            for (int x = 0; x < board.getSizeX(); x++) {
                GridButton.create(grid, controller, x, y, ttb -> {
                    learner.step(controller, TTTQLearn.performAction, ttb.getIndex());
                    this.showLearnScores(ttb.getIndex());
                    if (controller.getGame().getWonBy() != TTPlayer.NONE) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game over");
                        alert.headerTextProperty().set("Game won by " + controller.getGame().getWonBy());
                        alert.show();
                    }
                    scheduleAI();
                });
            }
        }
        showLearnScores(-1);
        scheduleAI();
    }

    private void scheduleAI() {
        ToIntFunction<TTController> ai = controller.getCurrentPlayer() == TTPlayer.X ? aiX : aiO;
        if (ai == null) {
            return;
        }
        executor.schedule(() -> {
            int action = ai.applyAsInt(controller);
            learner.step(controller, TTTQLearn.performAction, action);
            Platform.runLater(() -> {
                showLearnScores(action);
                scheduleAI();
            });
        }, 1, TimeUnit.SECONDS);
    }

    private void updateButtons(Function<GridButton, String> extra) {
        grid.getChildren().forEach(b -> {
            GridButton gb = (GridButton) b;
            gb.update();
            if (!gb.isDisabled()) {
                gb.setText(gb.getText() + "\n" + extra.apply(gb));
            }
        });
    }

    public Parent getRoot() {
        return parent;
    }
}
