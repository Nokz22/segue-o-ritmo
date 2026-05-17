package com.followtheritm.model;

import java.util.List;

/**
 * Immutable snapshot of what the player has pressed so far this round.
 * Records keep this as a pure value object with no mutation risk.
 */
public record PlayerInput(List<SimonColor> inputs) {

    public PlayerInput(List<SimonColor> inputs) {
        this.inputs = List.copyOf(inputs);
    }

    public int size() {
        return inputs.size();
    }

    public SimonColor get(int index) {
        return inputs.get(index);
    }

    /** Returns a new PlayerInput with the given colour appended. */
    public PlayerInput withAdded(SimonColor color) {
        var next = new java.util.ArrayList<>(inputs);
        next.add(color);
        return new PlayerInput(next);
    }

    public static PlayerInput empty() {
        return new PlayerInput(List.of());
    }
}
