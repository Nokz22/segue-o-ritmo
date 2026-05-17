package com.followtheritm.view;

import com.followtheritm.model.SimonColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Large coloured quadrant with three distinct visual states:
 *  NORMAL  — very dark gradient (clearly "off")
 *  HOVER   — bright fill + thick white outline (readable without colour recognition)
 *  FLASH   — white-centre radial gradient + animated fading glow border
 *
 * Hover is suppressed while the button is disabled (sequence-playback phase)
 * so it never competes with the system flash for attention.
 */
public class ColorButton extends JPanel {

    private static final int   ARC        = 28;
    private static final float INSET      = 4f;   // gap between panel edge and drawn shape

    private final SimonColor simonColor;
    private boolean flashing  = false;
    private boolean hovered   = false;
    private float   glowAlpha = 0f;   // 1.0 at flash start, decays to 0

    private Timer flashOffTimer;
    private Timer glowDecayTimer;

    public ColorButton(SimonColor simonColor) {
        this.simonColor = simonColor;
        setOpaque(false);   // we own every pixel via paintComponent
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            // Only show hover during AWAITING_INPUT (buttons enabled)
            @Override public void mouseEntered(MouseEvent e) {
                if (isEnabled()) { hovered = true;  repaint(); }
            }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    public SimonColor getSimonColor() { return simonColor; }

    public void flash(int durationMs) {
        stopTimers();
        flashing  = true;
        glowAlpha = 1.0f;
        repaint();

        // Decay the glow border alpha at ~60 fps — gives a "lamp lit" feel
        glowDecayTimer = new Timer(16, e -> {
            glowAlpha = Math.max(0f, glowAlpha - 0.025f);
            repaint();
        });
        glowDecayTimer.start();

        flashOffTimer = new Timer(durationMs, e -> {
            stopTimers();
            flashing  = false;
            glowAlpha = 0f;
            repaint();
        });
        flashOffTimer.setRepeats(false);
        flashOffTimer.start();
    }

    private void stopTimers() {
        if (flashOffTimer  != null) { flashOffTimer.stop();  flashOffTimer  = null; }
        if (glowDecayTimer != null) { glowDecayTimer.stop(); glowDecayTimer = null; }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,          RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,     RenderingHints.VALUE_STROKE_PURE);

        float w = getWidth(), h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(INSET, INSET, w - INSET*2, h - INSET*2, ARC, ARC);

        paintFill(g2, shape, w, h);
        paintBorder(g2, shape);

        g2.dispose();
    }

    private void paintFill(Graphics2D g2, RoundRectangle2D shape, float w, float h) {
        if (flashing) {
            // White centre → flash colour → dark edge: unmistakable "lit" look
            float radius = (float) Math.sqrt(w * w + h * h) / 2f;
            g2.setPaint(new RadialGradientPaint(
                w / 2f, h / 2f, radius,
                new float[]{ 0f, 0.30f, 1f },
                new Color[]{ Color.WHITE, simonColor.getFlashColor(), darken(simonColor.getBaseColor(), 0.7f) }
            ));
        } else if (hovered) {
            // Bright top-to-bottom gradient; white border (painted below) identifies the button shape
            g2.setPaint(new GradientPaint(0, 0, simonColor.getFlashColor().brighter(),
                                          0, h, simonColor.getFlashColor()));
        } else {
            // Clearly "off": top slightly lighter than bottom keeps a sense of depth
            g2.setPaint(new GradientPaint(0, 0, darken(simonColor.getBaseColor(), 0.80f),
                                          0, h, darken(simonColor.getBaseColor(), 0.50f)));
        }
        g2.fill(shape);
    }

    private void paintBorder(Graphics2D g2, RoundRectangle2D shape) {
        if (flashing) {
            // Animated glow — alpha follows glowAlpha so the border fades as the flash holds
            int a = Math.min(255, (int)(glowAlpha * 245));
            g2.setColor(new Color(255, 255, 255, a));
            g2.setStroke(new BasicStroke(7f));
        } else if (hovered) {
            // Thick white outline: shape, not colour, identifies the target
            g2.setColor(new Color(255, 255, 255, 230));
            g2.setStroke(new BasicStroke(5f));
        } else {
            g2.setColor(new Color(0, 0, 0, 90));
            g2.setStroke(new BasicStroke(2f));
        }
        g2.draw(shape);
    }

    private static Color darken(Color c, float factor) {
        return new Color(
            clamp((int)(c.getRed()   * factor)),
            clamp((int)(c.getGreen() * factor)),
            clamp((int)(c.getBlue()  * factor))
        );
    }

    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
