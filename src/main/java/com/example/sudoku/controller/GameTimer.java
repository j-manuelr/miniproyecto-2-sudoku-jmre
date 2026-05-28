package com.example.sudoku.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
<<<<<<< HEAD
 * Manages the live game timer and keeps a {@link Label} in sync.
 *
 * <h2>Single Responsibility</h2>
 * <p>This class has exactly one responsibility: track elapsed game
 * seconds and update the display label every second.  Timer logic
 * was previously embedded inside {@code GameController}; extracting
 * it here reduces that class's surface area and makes the timer
 * independently testable.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 * @see GameController
 */
public class GameTimer {

    /** Label that displays the formatted elapsed time. */
    private final Label timerLabel;

    /** JavaFX animation loop used as the 1-second ticker. */
    private Timeline timeline;

    /** Total seconds elapsed since the last {@link #start()} call. */
=======
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
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    private int secondsElapsed;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
<<<<<<< HEAD
     * Creates a timer bound to the given display label.
     *
     * @param timerLabel the {@link Label} that will show the time
     */
    public GameTimer(Label timerLabel) {
        this.timerLabel = timerLabel;
=======
     * Constructs a timer that updates the given label.
     *
     * @param timerLabel the {@link Label} to refresh on every tick
     */
    public GameTimer(Label timerLabel) {
        this.timerLabel     = timerLabel;
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        this.secondsElapsed = 0;
    }

    // -------------------------------------------------------------------------
<<<<<<< HEAD
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Resets the elapsed counter to zero, updates the label to
     * {@code "00:00"}, and starts the ticker.
     */
    public void start() {
        secondsElapsed = 0;
        timerLabel.setText("00:00");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText(formatTime(secondsElapsed));
=======
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
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
<<<<<<< HEAD
     * Stops the current ticker (if running) and starts a fresh one from
     * zero.
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Stops the ticker.  Safe to call even if the timer has not been
     * started yet.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Returns the total number of seconds recorded since the last
     * {@link #start()} call.
     *
     * @return non-negative elapsed second count
     */
    public int getSecondsElapsed() {
=======
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
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        return secondsElapsed;
    }

    /**
<<<<<<< HEAD
     * Returns the elapsed time formatted as {@code "MM:SS"}.
     *
     * @return formatted time string
     */
    public String getFormattedTime() {
        return formatTime(secondsElapsed);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Converts a raw second count to a {@code "MM:SS"} string.
     *
     * @param totalSeconds non-negative second count
     * @return formatted string, e.g. {@code "03:07"}
     */
    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
=======
     * Returns the current elapsed time as a {@code "mm:ss"} string.
     *
     * @return formatted time, e.g. {@code "03:47"}
     */
    public String getFormattedTime() {
        int m = secondsElapsed / 60;
        int s = secondsElapsed % 60;
        return String.format("%02d:%02d", m, s);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    }
}
