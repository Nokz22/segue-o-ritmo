package com.followtheritm.view;

import com.followtheritm.config.GameConfig;
import com.followtheritm.factory.ColorButtonFactory;
import com.followtheritm.model.SimonColor;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 2×2 grid of colour quadrants + a transparent overlay on a higher layer.
 * Uses JLayeredPane so the overlay can cover all buttons without AWT z-order tricks.
 * Layout: RED(TL) BLUE(TR) / GREEN(BL) YELLOW(BR)
 */
public class SimonPanel extends JLayeredPane {

    private static final Integer BUTTON_LAYER  = JLayeredPane.DEFAULT_LAYER;
    private static final Integer OVERLAY_LAYER = JLayeredPane.PALETTE_LAYER;

    private final Map<SimonColor, ColorButton> buttons = new EnumMap<>(SimonColor.class);
    private final JPanel buttonGrid;
    private final GameOverOverlay overlay;

    public SimonPanel() {
        setBackground(GameConfig.BACKGROUND_COLOR);
        setOpaque(true);

        buttonGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        // Groove colour between quadrants — visible gap provides shape cues for colourblind users
        buttonGrid.setBackground(new Color(15, 15, 22));

        SimonColor[] order = {
            SimonColor.RED,   SimonColor.BLUE,
            SimonColor.GREEN, SimonColor.YELLOW
        };
        for (SimonColor c : order) {
            ColorButton btn = ColorButtonFactory.create(c);
            buttons.put(c, btn);
            buttonGrid.add(btn);
        }

        overlay = new GameOverOverlay();

        add(buttonGrid, BUTTON_LAYER);
        add(overlay,    OVERLAY_LAYER);
    }

    // JLayeredPane has null layout — doLayout() is the correct hook for sizing children.
    // ComponentListener.componentResized() is NOT called on the initial layout pass,
    // which left children with zero bounds (hence an invisible black panel).
    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        buttonGrid.setBounds(0, 0, w, h);
        overlay.setBounds(0, 0, w, h);
    }

    public void addColorClickListener(Consumer<SimonColor> handler) {
        buttons.forEach((color, btn) -> btn.addMouseListener(new java.awt.event.MouseAdapter() {
            // mousePressed fires immediately on touch-screen tap even if the finger
            // drifts slightly; mouseClicked requires press+release at the same point
            // which touch events frequently fail to satisfy.
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) handler.accept(color);
            }
        }));
    }

    public void flashButton(SimonColor color, int durationMs) {
        buttons.get(color).flash(durationMs);
    }

    public void setButtonsEnabled(boolean enabled) {
        buttons.values().forEach(b -> b.setEnabled(enabled));
    }

    public GameOverOverlay getOverlay() { return overlay; }
}
