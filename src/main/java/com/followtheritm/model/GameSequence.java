package com.followtheritm.model;

import java.util.List;

/**
 * Immutable snapshot of the current colour sequence.
 * List.copyOf prevents external mutation after construction.
 */
public record GameSequence(List<SimonColor> colors) {

    public GameSequence(List<SimonColor> colors) {
        this.colors = List.copyOf(colors);
    }

    public int length() {
        return colors.size();
    }

    public SimonColor get(int index) {
        return colors.get(index);
    }
}
