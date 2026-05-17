package com.followtheritm.observer;

import com.followtheritm.model.GameSession;

@FunctionalInterface
public interface GameEventListener {
    void onGameEvent(GameEvent event, GameSession session);
}
