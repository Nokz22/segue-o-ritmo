package com.followtheritm.strategy;

/** Strategy: each implementation owns the timing constants for one speed mode. */
public interface SpeedStrategy {
    /** Total period between flash starts (includes flash duration + gap). */
    int intervalMs();
    /** How long a button stays lit. */
    int flashDurationMs();
}
