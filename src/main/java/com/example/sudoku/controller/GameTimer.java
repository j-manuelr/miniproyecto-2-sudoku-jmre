package com.example.sudoku.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages the game countdown timer.
 *
 * <p>Single responsibility: track elapsed seconds, update a {@link Label}
 * every second, and expose formatted-time output. Knows nothing about
 * game logic, board state, or UI layout.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class GameTimer {

    /** Label that displays the running time in {@code mm:ss} format. */
    private final Label timerLabel;

    /** The JavaFX timeline that fires every second. */
    private Timeline timeline;

    /** Total seconds elapsed since the last {@link #start()} or {@link #restart()}. */
    private int secondsElapsed;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a timer that updates the given label.
     *
     * @param timerLabel the {@link Label} to refresh on every tick
     */
    public GameTimer(Label timerLabel) {
        this.timerLabel     = timerLabel;
        this.secondsElapsed = 0;
    }

    // -------------------------------------------------------------------------
    // Timer control
    // -------------------------------------------------------------------------

    /**
     * Resets the elapsed counter to zero and starts the timeline.
     */
    public void start() {
        secondsElapsed = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText(getFormattedTime());
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the running timeline without resetting the elapsed counter.
     * Safe to call even if the timer is not running.
     */
    public void stop() {
        if (timeline != null) timeline.stop();
    }

    /**
     * Stops the timer, resets the label to {@code 00:00}, and starts fresh.
     */
    public void restart() {
        stop();
        timerLabel.setText("00:00");
        start();
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Returns the number of seconds elapsed since the last start/restart.
     *
     * @return elapsed seconds (≥ 0)
     */
    public int getElapsedSeconds() {
        return secondsElapsed;
    }

    /**
     * Returns the current elapsed time as a {@code "mm:ss"} string.
     *
     * @return formatted time, e.g. {@code "03:47"}
     */
    public String getFormattedTime() {
        int m = secondsElapsed / 60;
        int s = secondsElapsed % 60;
        return String.format("%02d:%02d", m, s);
    }
}
