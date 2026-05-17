package com.followtheritm.config;

import java.awt.Color;
import java.awt.Font;

/** Singleton — one source of truth for all magic numbers and pt-PT strings. */
public final class GameConfig {

    private static final GameConfig INSTANCE = new GameConfig();

    private GameConfig() {}

    public static GameConfig getInstance() { return INSTANCE; }

    // ── Window ────────────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH  = 800;
    public static final int WINDOW_HEIGHT = 800;

    // ── Audio ─────────────────────────────────────────────────────────────
    public static final int SAMPLE_RATE          = 44100;
    public static final int ERROR_FREQ_HZ        = 110;
    public static final int ERROR_DURATION_MS    = 300;
    public static final int SUCCESS_NOTE_DURATION_MS = 150;
    public static final int[] SUCCESS_FREQS_HZ   = {262, 330, 392}; // C4-E4-G4

    // ── Animation ─────────────────────────────────────────────────────────
    public static final int FLASH_DURATION_MS    = 400;  // how long a button stays lit
    public static final int ERROR_FLASH_COUNT    = 3;    // screen flashes on mistake

    // ── Speed strategy intervals (pause between flashes in ms) ───────────
    // Gaps are intentionally large between modes so users with cognitive
    // difficulties feel a clearly different pace, not just a slight change.
    public static final int SLOW_INTERVAL_MS     = 2500;  // very patient
    public static final int NORMAL_INTERVAL_MS   = 950;
    public static final int FAST_INTERVAL_MS     = 450;

    // ── Colours (UI chrome, not button colours) ───────────────────────────
    public static final Color BACKGROUND_COLOR   = new Color(30, 30, 40);
    public static final Color ERROR_OVERLAY_COLOR = new Color(200, 0, 0, 160);
    public static final Color STAR_LIT_COLOR     = new Color(255, 215, 0);
    public static final Color STAR_DIM_COLOR     = new Color(80, 80, 80);
    public static final Color CONTROL_BG_COLOR   = new Color(45, 45, 60);

    // ── Fonts ─────────────────────────────────────────────────────────────
    public static final Font ICON_FONT      = new Font("Dialog", Font.BOLD, 72);
    public static final Font LABEL_FONT     = new Font("Dialog", Font.BOLD, 22);
    public static final Font STAR_FONT      = new Font("Dialog", Font.BOLD, 28);
    public static final Font BUTTON_FONT    = new Font("Dialog", Font.BOLD, 20);
    public static final Font SPEED_FONT     = new Font("Dialog", Font.PLAIN, 18);

    // ── pt-PT UI strings (all player-visible text lives here) ─────────────
    public static final String LABEL_START          = "▶  Iniciar";
    public static final String LABEL_FULLSCREEN     = "Ecrã inteiro";
    public static final String LABEL_WINDOWED       = "Janela";
    public static final String LABEL_ROUND          = "Ronda";
    public static final String LABEL_PERSONAL_BEST  = "Recorde pessoal";
    public static final String LABEL_ERROR          = "Erro!";
    public static final String LABEL_TRY_AGAIN      = "Tenta de novo";
    public static final String LABEL_SLOW           = "🐢  Lento";
    public static final String LABEL_NORMAL         = "🚶  Normal";
    public static final String LABEL_FAST           = "🐇  Rápido";
    public static final String LABEL_CLASSIC_MODE   = "Clássico";
    public static final String LABEL_RANDOM_MODE    = "Aleatório";
    public static final String LABEL_CHOOSE_MODE    = "Escolhe o modo de jogo";
    public static final String LABEL_MENU           = "← Menu";
    public static final String ICON_ERROR           = "✗";
    public static final String ICON_SUCCESS         = "✓";
    public static final String ICON_STAR_LIT        = "★";
    public static final String ICON_STAR_DIM        = "☆";
}
