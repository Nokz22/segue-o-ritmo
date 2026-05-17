package com.followtheritm.model;

import java.awt.Color;

/**
 * Base colors are very dark (~40% brightness) so the lit/unlit contrast
 * is immediately obvious even for users with colour-vision deficiency.
 */
public enum SimonColor {
    RED   (new Color(130, 18, 18),  new Color(255,  80,  80), 220),
    BLUE  (new Color( 18, 45, 170), new Color( 80, 140, 255), 277),
    GREEN (new Color( 18, 130, 18), new Color( 70, 230,  70), 330),
    YELLOW(new Color(165, 145, 12), new Color(255, 235,  75), 440);

    private final Color baseColor;
    private final Color flashColor;
    private final int frequencyHz;

    SimonColor(Color baseColor, Color flashColor, int frequencyHz) {
        this.baseColor = baseColor;
        this.flashColor = flashColor;
        this.frequencyHz = frequencyHz;
    }

    public Color getBaseColor()  { return baseColor; }
    public Color getFlashColor() { return flashColor; }
    public int getFrequencyHz()  { return frequencyHz; }
}
