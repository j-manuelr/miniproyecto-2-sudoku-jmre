package com.example.sudoku.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
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
    private int secondsElapsed;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a timer bound to the given display label.
     *
     * @param timerLabel the {@link Label} that will show the time
     */
    public GameTimer(Label timerLabel) {
        this.timerLabel = timerLabel;
        this.secondsElapsed = 0;
    }

    // -------------------------------------------------------------------------
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
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
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
        return secondsElapsed;
    }

    /**
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
    }
}
