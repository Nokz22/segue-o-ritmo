package com.followtheritm.service;

import com.followtheritm.model.GameSequence;
import com.followtheritm.model.SimonColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceServiceImpl implements SequenceService {

    private final Random random;
    private static final SimonColor[] VALUES = SimonColor.values();

    public SequenceServiceImpl() {
        this.random = new Random();
    }

    /** Constructor for tests: inject a seeded Random for determinism. */
    public SequenceServiceImpl(Random random) {
        this.random = random;
    }

    @Override
    public GameSequence createInitial() {
        return new GameSequence(List.of(randomColor()));
    }

    @Override
    public GameSequence extend(GameSequence current) {
        var next = new ArrayList<>(current.colors());
        next.add(randomColor());
        return new GameSequence(next);
    }

    @Override
    public GameSequence createRandom(int length) {
        var colors = new ArrayList<SimonColor>(length);
        for (int i = 0; i < length; i++) colors.add(randomColor());
        return new GameSequence(colors);
    }

    @Override
    public boolean isInputCorrectSoFar(GameSequence sequence, int index, SimonColor pressed) {
        return sequence.get(index) == pressed;
    }

    private SimonColor randomColor() {
        return VALUES[random.nextInt(VALUES.length)];
    }
}
