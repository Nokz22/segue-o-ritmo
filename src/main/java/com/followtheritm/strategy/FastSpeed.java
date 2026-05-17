package com.followtheritm.strategy;

import com.followtheritm.config.GameConfig;

public final class FastSpeed implements SpeedStrategy {
    @Override public int intervalMs()      { return GameConfig.FAST_INTERVAL_MS; }
    @Override public int flashDurationMs() { return 250; }
}
