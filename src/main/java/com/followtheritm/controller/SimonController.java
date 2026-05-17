package com.followtheritm.controller;

import com.followtheritm.model.*;
import com.followtheritm.observer.GameEvent;
import com.followtheritm.service.AudioService;
import com.followtheritm.service.SequenceService;
import com.followtheritm.strategy.*;
import com.followtheritm.view.*;

import java.util.List;

import javax.swing.*;

/**
 * Orchestrates the game loop. Does zero business logic itself — delegates
 * to SequenceService (game rules) and AudioService (sounds).
 * All Swing mutations happen on the EDT via javax.swing.Timer.
 */
public class SimonController {

    private final GameSession      session;
    private final SequenceService  sequenceService;
    private final AudioService     audioService;
    private final SimonPanel       simonPanel;
    private final RoundIndicator   roundIndicator;
    private final ControlPanel     controlPanel;

    private GameSequence  currentSequence = new GameSequence(List.of());
    private PlayerInput   playerInput     = PlayerInput.empty();
    private SpeedStrategy speed           = new NormalSpeed();
    private GameMode      gameMode        = GameMode.CLASSIC;

    // Index pointer used while replaying the sequence to the player
    private int showIndex = 0;
    private Timer showTimer;

    public SimonController(GameSession session,
                           SequenceService sequenceService,
                           AudioService audioService,
                           SimonPanel simonPanel,
                           RoundIndicator roundIndicator,
                           ControlPanel controlPanel) {
        this.session         = session;
        this.sequenceService = sequenceService;
        this.audioService    = audioService;
        this.simonPanel      = simonPanel;
        this.roundIndicator  = roundIndicator;
        this.controlPanel    = controlPanel;

        wireListeners();
        session.addListener(this::onGameEvent);
    }

    // ── Wiring ────────────────────────────────────────────────────────────

    private void wireListeners() {
        simonPanel.addColorClickListener(this::onColorPressed);
        controlPanel.setOnSpeedChanged(s -> speed = resolveSpeed(s));
    }

    // ── Public entry points ───────────────────────────────────────────────

    public void onStartPressed() {
        if (session.getState() == GameState.IDLE || session.getState() == GameState.GAME_OVER) {
            session.reset();
            currentSequence = new GameSequence(List.of());
            startNewRound();
        }
    }

    /** Switch game mode and reset sequence so the next start is clean. */
    public void setGameMode(GameMode mode) {
        this.gameMode       = mode;
        this.currentSequence = new GameSequence(List.of());
    }

    /** Stop any running playback, reset session, and clear UI. Called when returning to the menu. */
    public void stop() {
        if (showTimer != null) showTimer.stop();
        currentSequence = new GameSequence(List.of());
        session.reset();
        simonPanel.setButtonsEnabled(false);
        simonPanel.getOverlay().dismiss();
        updateHud();
    }

    // ── Game flow ─────────────────────────────────────────────────────────

    private void startNewRound() {
        // Increment first so RANDOM mode can use the new round number for sequence length
        playerInput = PlayerInput.empty();
        session.startNewRound();

        if (gameMode == GameMode.RANDOM) {
            currentSequence = sequenceService.createRandom(session.getCurrentRound());
        } else {
            currentSequence = currentSequence.length() == 0
                    ? sequenceService.createInitial()
                    : sequenceService.extend(currentSequence);
        }

        session.setState(GameState.SHOWING);
        updateHud();
        simonPanel.setButtonsEnabled(false);
        simonPanel.getOverlay().dismiss();
        scheduleSequencePlayback();
    }

    private void scheduleSequencePlayback() {
        showIndex = 0;
        // Initial delay gives the player a moment before the sequence starts
        showTimer = new Timer(600, null);
        showTimer.addActionListener(e -> playNextFlash());
        showTimer.setRepeats(true);
        showTimer.setInitialDelay(600);
        showTimer.start();
    }

    private void playNextFlash() {
        if (showIndex >= currentSequence.length()) {
            showTimer.stop();
            transitionToAwaitingInput();
            return;
        }
        SimonColor color = currentSequence.get(showIndex++);
        simonPanel.flashButton(color, speed.flashDurationMs());
        audioService.playColorTone(color);
        showTimer.setDelay(speed.intervalMs());
    }

    private void transitionToAwaitingInput() {
        session.setState(GameState.AWAITING_INPUT);
        simonPanel.setButtonsEnabled(true);
    }

    private void onColorPressed(SimonColor color) {
        if (session.getState() != GameState.AWAITING_INPUT) return;

        int idx = playerInput.size();
        simonPanel.flashButton(color, speed.flashDurationMs());
        audioService.playColorTone(color);

        if (!sequenceService.isInputCorrectSoFar(currentSequence, idx, color)) {
            handleWrongInput();
            return;
        }

        playerInput = playerInput.withAdded(color);

        if (playerInput.size() == currentSequence.length()) {
            handleRoundComplete();
        }
        // else: wait for more input — no state change needed
    }

    private void handleWrongInput() {
        session.setState(GameState.GAME_OVER);
        simonPanel.setButtonsEnabled(false);
        audioService.playError();
        simonPanel.getOverlay().showError();
        currentSequence = new GameSequence(List.of());
    }

    private void handleRoundComplete() {
        session.recordSuccess();
        session.setState(GameState.SUCCESS);
        simonPanel.setButtonsEnabled(false);
        audioService.playSuccess();
        simonPanel.getOverlay().showSuccess();
        updateHud();

        // Short pause then advance to next round
        Timer next = new Timer(1000, e -> startNewRound());
        next.setRepeats(false);
        next.start();
    }

    // ── Observer callback ─────────────────────────────────────────────────

    private void onGameEvent(GameEvent event, GameSession s) {
        SwingUtilities.invokeLater(() -> {
            updateHud();
            boolean canStart = s.getState() == GameState.IDLE
                            || s.getState() == GameState.GAME_OVER;
            controlPanel.setStartEnabled(canStart);
        });
    }

    private void updateHud() {
        roundIndicator.update(session.getCurrentRound(), session.getPersonalBest());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private SpeedStrategy resolveSpeed(ControlPanel.Speed s) {
        return switch (s) {
            case SLOW   -> new SlowSpeed();
            case NORMAL -> new NormalSpeed();
            case FAST   -> new FastSpeed();
        };
    }
}
