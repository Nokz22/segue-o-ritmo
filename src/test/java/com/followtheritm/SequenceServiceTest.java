package com.followtheritm;

import com.followtheritm.model.GameSequence;
import com.followtheritm.model.SimonColor;
import com.followtheritm.service.SequenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SequenceServiceTest {

    private SequenceServiceImpl service;

    @BeforeEach
    void setUp() {
        // Seed 0 produces a deterministic sequence for assertions
        service = new SequenceServiceImpl(new Random(0));
    }

    @Test
    void createInitial_producesLengthOne() {
        GameSequence seq = service.createInitial();
        assertEquals(1, seq.length());
    }

    @Test
    void extend_addsExactlyOneElement() {
        GameSequence seq = service.createInitial();
        GameSequence extended = service.extend(seq);
        assertEquals(seq.length() + 1, extended.length());
    }

    @Test
    void extend_preservesPriorElements() {
        GameSequence seq = service.createInitial();
        GameSequence extended = service.extend(seq);
        assertEquals(seq.get(0), extended.get(0));
    }

    @Test
    void isInputCorrectSoFar_correctColorReturnsTrue() {
        GameSequence seq = new GameSequence(List.of(SimonColor.RED, SimonColor.BLUE));
        assertTrue(service.isInputCorrectSoFar(seq, 0, SimonColor.RED));
        assertTrue(service.isInputCorrectSoFar(seq, 1, SimonColor.BLUE));
    }

    @Test
    void isInputCorrectSoFar_wrongColorReturnsFalse() {
        GameSequence seq = new GameSequence(List.of(SimonColor.RED));
        assertFalse(service.isInputCorrectSoFar(seq, 0, SimonColor.GREEN));
    }

    @Test
    void gameSequence_isImmutable() {
        GameSequence seq = service.createInitial();
        assertThrows(UnsupportedOperationException.class,
                () -> seq.colors().add(SimonColor.YELLOW));
    }
}
