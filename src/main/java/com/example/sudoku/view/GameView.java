package com.example.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

/**
 * Singleton stage that acts as the primary window for the Sudoku game.
 * Loads {@code game-view.fxml}, applies the scene, and configures the window.
 *
 * <p>Implements {@link IGameView} to expose the minimal lifecycle surface
 * required by the MVC architecture.</p>
 *
 * @author Juan Rosero
 * @version 1.0
 */
public class GameView extends Stage implements IGameView {

    /** The single live instance of this stage. */
    private static GameView instance;

    // -------------------------------------------------------------------------
    // Constructor (private — use getInstance())
    // -------------------------------------------------------------------------

    /**
     * Initialises the stage by loading the FXML layout, creating the scene,
     * and configuring the window.
     *
     * @throws IOException if the FXML resource cannot be found or parsed
     */
    private GameView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                        getClass().getResource("/com/example/sudoku/game-view.fxml"),
                        "FXML resource not found: /com/example/sudoku/game-view.fxml"
                )
        );
        Parent root = loader.load();

        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Sudoku 6×6  |  FPOE");
        setResizable(false);
    }

    // -------------------------------------------------------------------------
    // Singleton access
    // -------------------------------------------------------------------------

    /**
     * Returns the singleton instance, creating it on the first call.
     *
     * @return the single {@link GameView} instance
     * @throws IOException if the FXML cannot be loaded on first creation
     */
    public static GameView getInstance() throws IOException {
        if (instance == null) {
            instance = new GameView();
        }
        return instance;
    }

    /**
     * Nullifies the singleton reference so a fresh instance can be created
     * on the next {@link #getInstance()} call.
     */
    public static void deleteInstance() {
        instance = null;
    }

    // -------------------------------------------------------------------------
    // IGameView
    // -------------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public void displayStage() {
        show();
        toFront();
    }

    /** {@inheritDoc} */
    @Override
    public void closeStage() {
        close();
        deleteInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void setWindowTitle(String title) {
        setTitle(title);
    }
}
