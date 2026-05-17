package com.followtheritm;

import com.followtheritm.model.GameSession;
import com.followtheritm.model.GameState;
import com.followtheritm.observer.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private GameSession session;
    private List<GameEvent> firedEvents;

    @BeforeEach
    void setUp() {
        session     = new GameSession();
        firedEvents = new ArrayList<>();
        session.addListener((event, s) -> firedEvents.add(event));
    }

    @Test
    void initialStateIsIdle() {
        assertEquals(GameState.IDLE, session.getState());
    }

    @Test
    void setState_firesStateChangedEvent() {
        session.setState(GameState.SHOWING);
        assertTrue(firedEvents.contains(GameEvent.STATE_CHANGED));
    }

    @Test
    void startNewRound_incrementsRound() {
        session.startNewRound();
        assertEquals(1, session.getCurrentRound());
        session.startNewRound();
        assertEquals(2, session.getCurrentRound());
    }

    @Test
    void startNewRound_firesRoundStartedEvent() {
        session.startNewRound();
        assertTrue(firedEvents.contains(GameEvent.ROUND_STARTED));
    }

    @Test
    void recordSuccess_updatesPersonalBest() {
        session.startNewRound();
        session.recordSuccess();
        assertEquals(1, session.getPersonalBest());
    }

    @Test
    void recordSuccess_doesNotDecreasePersonalBest() {
        session.startNewRound();
        session.recordSuccess();   // best = 1
        session.reset();
        session.startNewRound();   // round = 1 again, not 2
        session.recordSuccess();   // should not fire PERSONAL_BEST_UPDATED
        assertEquals(1, session.getPersonalBest());
    }

    @Test
    void recordSuccess_firesPersonalBestUpdated_onlyWhenBeaten() {
        session.startNewRound();
        session.recordSuccess();
        long initialCount = firedEvents.stream()
                .filter(e -> e == GameEvent.PERSONAL_BEST_UPDATED).count();

        session.reset();
        session.startNewRound(); // round = 1, best = 1 → no new best
        session.recordSuccess();
        long newCount = firedEvents.stream()
                .filter(e -> e == GameEvent.PERSONAL_BEST_UPDATED).count();

        assertEquals(initialCount, newCount); // no extra PERSONAL_BEST_UPDATED
    }

    @Test
    void reset_resetsRoundToZero() {
        session.startNewRound();
        session.startNewRound();
        session.reset();
        assertEquals(0, session.getCurrentRound());
    }

    @Test
    void reset_setsStateToIdle() {
        session.setState(GameState.GAME_OVER);
        session.reset();
        assertEquals(GameState.IDLE, session.getState());
    }

    @Test
    void reset_doesNotClearPersonalBest() {
        session.startNewRound();
        session.startNewRound();
        session.recordSuccess();  // best = 2
        session.reset();
        assertEquals(2, session.getPersonalBest());
    }

    @Test
    void removeListener_stopsReceivingEvents() {
        var listener = (com.followtheritm.observer.GameEventListener)
                (event, s) -> firedEvents.add(event);
        session.addListener(listener);
        session.removeListener(listener);
        int before = firedEvents.size();
        session.setState(GameState.SHOWING);
        // Only the original listener fires, not the removed one
        assertEquals(before + 1, firedEvents.size());
    }
}
