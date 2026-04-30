package com.example.sudoku;

import com.example.sudoku.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application entry point for the Sudoku 6x6 game.
 * Bootstraps the JavaFX runtime and delegates stage initialization
 * to {@link GameView}.
 *
 * @author Juan Rosero
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Optional JVM entry point for IDE/direct execution.
     *
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX lifecycle start method.
     * Obtains the singleton {@link GameView} instance and displays the window.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     * @throws Exception if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameView.getInstance().displayStage();
    }


}
