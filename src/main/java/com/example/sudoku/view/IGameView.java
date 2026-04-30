package com.example.sudoku.view;

/**
 * Contract for the game view layer.
 * Defines the minimum lifecycle operations that the main game window
 * must expose so the rest of the application can control its visibility.
 *
 * @author Juan Rosero
 * @version 1.0
 */
public interface IGameView {

    /**
     * Makes the game window visible and brings it to the front.
     */
    void displayStage();

    /**
     * Closes the game window and releases its resources.
     */
    void closeStage();

    /**
     * Updates the title bar text of the game window.
     *
     * @param title the new window title
     */
    void setWindowTitle(String title);
}
