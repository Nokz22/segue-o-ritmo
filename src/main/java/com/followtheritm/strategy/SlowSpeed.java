package com.followtheritm.strategy;

import com.followtheritm.config.GameConfig;

/**
 * Very patient: each button stays lit for 1.2 s and there is a 2.5 s gap
 * between steps — noticeable even for users with slow processing speeds.
 */
public final class SlowSpeed implements SpeedStrategy {
    @Override public int intervalMs()      { return GameConfig.SLOW_INTERVAL_MS; }
    @Override public int flashDurationMs() { return 1200; }
}
