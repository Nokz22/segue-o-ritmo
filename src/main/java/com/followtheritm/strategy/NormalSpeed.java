package com.followtheritm.strategy;

import com.followtheritm.config.GameConfig;

public final class NormalSpeed implements SpeedStrategy {
    @Override public int intervalMs()      { return GameConfig.NORMAL_INTERVAL_MS; }
    @Override public int flashDurationMs() { return 480; }
}
