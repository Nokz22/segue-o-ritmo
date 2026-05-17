package com.followtheritm.model;

public enum GameState {
    IDLE,           // waiting for player to press Iniciar
    SHOWING,        // system is playing the sequence
    AWAITING_INPUT, // player must repeat the sequence
    SUCCESS,        // player completed the round correctly
    GAME_OVER       // player made a wrong input
}
