package com.followtheritm.service;

import com.followtheritm.model.GameSequence;
import com.followtheritm.model.SimonColor;

public interface SequenceService {
    /** Returns a new sequence extended by one random colour. */
    GameSequence extend(GameSequence current);

    /** Returns a fresh single-element sequence. */
    GameSequence createInitial();

    /** Returns a completely new random sequence of the given length. */
    GameSequence createRandom(int length);

    /** True when every input so far matches the sequence at the same index. */
    boolean isInputCorrectSoFar(GameSequence sequence, int index, SimonColor pressed);
}
