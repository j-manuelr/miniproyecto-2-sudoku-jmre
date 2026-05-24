package com.example.sudoku.controller;

/**
 * Contract for the Sudoku game controller.
 * Exposes the action handlers wired to the FXML buttons and defines the
 * interface through which external components can trigger game operations.
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public interface IGameController {

    /**
     * Starts a brand-new random puzzle, discarding the current game state.
     * Resets the timer and clears any status messages.
     */
    void handleNewGame();

    /**
     * Restarts the current puzzle from its initial state (fixed clues only).
     * Resets the timer and clears all user-entered values.
     */
    void handleRestart();

    /**
     * Provides a hint by automatically filling one empty cell with its
     * correct solution value and highlighting it on the board.
     * If a cell is currently selected and empty, the hint is placed there;
     * otherwise a random empty cell is chosen.
     */
    void handleHelp();

    /**
     * Undoes the most recent user move, restoring the previous cell value.
     * Delegates to the model's undo-history stack.
     */
    void handleUndo();
}
