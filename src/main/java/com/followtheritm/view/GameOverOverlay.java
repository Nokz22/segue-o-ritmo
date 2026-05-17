package com.followtheritm.view;

import com.followtheritm.config.GameConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Semi-transparent overlay shown on GAME_OVER and SUCCESS states.
 * Uses icons only (✗ / ✓) so it is literacy-free.
 * The "Tenta de novo" / "Erro!" text assists literate caregivers.
 */
public class GameOverOverlay extends JPanel {

    public enum OverlayType { ERROR, SUCCESS }

    private OverlayType type       = OverlayType.ERROR;
    private boolean     errorFlash = false;

    public GameOverOverlay() {
        setOpaque(false);
        setVisible(false);
    }

    public void showError() {
        type = OverlayType.ERROR;
        setVisible(true);
        animateErrorFlash();
    }

    public void showSuccess() {
        type = OverlayType.SUCCESS;
        setVisible(true);
        repaint();
        Timer t = new Timer(750, e -> setVisible(false));
        t.setRepeats(false);
        t.start();
    }

    // Named 'dismiss' to avoid overriding java.awt.Component.hide() → StackOverflowError
    public void dismiss() {
        setVisible(false);
    }

    private void animateErrorFlash() {
        final int[] count = {0};
        Timer flashTimer = new Timer(110, null);
        flashTimer.addActionListener(e -> {
            errorFlash = !errorFlash;
            repaint();
            if (++count[0] >= GameConfig.ERROR_FLASH_COUNT * 2) {
                flashTimer.stop();
                errorFlash = false;
                repaint();
            }
        });
        flashTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int cx = w / 2,    cy = h / 2;

        // Full-screen dimming layer
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, w, h);

        // Central card
        int cardW = 300, cardH = 200;
        int cardX = cx - cardW / 2, cardY = cy - cardH / 2;
        RoundRectangle2D card = new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 32, 32);

        if (type == OverlayType.ERROR) {
            Color cardBg = errorFlash ? new Color(230, 20, 20, 240) : new Color(180, 20, 20, 230);
            g2.setColor(cardBg);
            g2.fill(card);
            g2.setColor(new Color(255, 100, 100, 120));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(card);

            // ✗ icon
            g2.setFont(new Font("Dialog", Font.BOLD, 80));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String icon = GameConfig.ICON_ERROR;
            g2.drawString(icon, cx - fm.stringWidth(icon) / 2, cy + 22);

            // "Erro!" label
            g2.setFont(new Font("Dialog", Font.BOLD, 26));
            fm = g2.getFontMetrics();
            String err = GameConfig.LABEL_ERROR;
            g2.drawString(err, cx - fm.stringWidth(err) / 2, cardY + cardH - 50);

            // "Tenta de novo" hint
            g2.setFont(new Font("Dialog", Font.PLAIN, 16));
            g2.setColor(new Color(255, 200, 200));
            fm = g2.getFontMetrics();
            String retry = GameConfig.LABEL_TRY_AGAIN;
            g2.drawString(retry, cx - fm.stringWidth(retry) / 2, cardY + cardH - 22);

        } else {
            g2.setColor(new Color(20, 160, 50, 230));
            g2.fill(card);
            g2.setColor(new Color(80, 220, 120, 120));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(card);

            g2.setFont(new Font("Dialog", Font.BOLD, 90));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String icon = GameConfig.ICON_SUCCESS;
            g2.drawString(icon, cx - fm.stringWidth(icon) / 2, cy + 28);
        }

        g2.dispose();
    }
}
