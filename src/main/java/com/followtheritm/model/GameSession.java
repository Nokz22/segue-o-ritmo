package com.followtheritm.model;

import com.followtheritm.observer.GameEvent;
import com.followtheritm.observer.GameEventListener;

import java.util.ArrayList;
import java.util.List;

/** Mutable game state: round counter, personal best, current state machine node. */
public class GameSession {

    private int currentRound   = 0;
    private int personalBest   = 0;
    private GameState state    = GameState.IDLE;

    private final List<GameEventListener> listeners = new ArrayList<>();

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    private void fire(GameEvent event) {
        listeners.forEach(l -> l.onGameEvent(event, this));
    }

    public void setState(GameState newState) {
        this.state = newState;
        fire(GameEvent.STATE_CHANGED);
    }

    public void startNewRound() {
        currentRound++;
        fire(GameEvent.ROUND_STARTED);
    }

    public void recordSuccess() {
        if (currentRound > personalBest) {
            personalBest = currentRound;
            fire(GameEvent.PERSONAL_BEST_UPDATED);
        }
    }

    public void reset() {
        currentRound = 0;
        state        = GameState.IDLE;
        fire(GameEvent.GAME_RESET);
    }

    public GameState getState()     { return state; }
    public int getCurrentRound()    { return currentRound; }
    public int getPersonalBest()    { return personalBest; }
}
