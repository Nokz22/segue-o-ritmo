package com.followtheritm.view;

import com.followtheritm.config.GameConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Displays round counter (★ row) and personal best on a polished dark pill background. */
public class RoundIndicator extends JPanel {

    private int currentRound = 0;
    private int personalBest = 0;

    public RoundIndicator() {
        setOpaque(false);
        setPreferredSize(new Dimension(800, 96));
    }

    public void update(int round, int best) {
        this.currentRound = round;
        this.personalBest = best;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Subtle pill background
        int pillW = Math.min(w - 40, 500);
        int pillH = h - 16;
        int pillX = (w - pillW) / 2;
        int pillY = 8;
        RoundRectangle2D pill = new RoundRectangle2D.Float(pillX, pillY, pillW, pillH, 24, 24);
        g2.setColor(new Color(255, 255, 255, 12));
        g2.fill(pill);
        g2.setColor(new Color(255, 255, 255, 25));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(pill);

        int cx = w / 2;

        // "Ronda X" — bold, large
        g2.setFont(new Font("Dialog", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String roundLabel = GameConfig.LABEL_ROUND + "  " + currentRound;
        g2.drawString(roundLabel, cx - fm.stringWidth(roundLabel) / 2, pillY + 28);

        // Star row for current round
        drawStarRow(g2, cx, pillY + 60, currentRound);

        // Personal best — subtler, smaller
        g2.setFont(new Font("Dialog", Font.PLAIN, 13));
        g2.setColor(new Color(160, 160, 180));
        fm = g2.getFontMetrics();
        String pbLabel = GameConfig.LABEL_PERSONAL_BEST + ": " + personalBest;
        g2.drawString(pbLabel, cx - fm.stringWidth(pbLabel) / 2, pillY + pillH - 6);

        g2.dispose();
    }

    private void drawStarRow(Graphics2D g2, int cx, int y, int lit) {
        int maxStars = 20;
        int shown    = Math.min(Math.max(lit, 1), maxStars);
        g2.setFont(new Font("Dialog", Font.BOLD, 22));
        FontMetrics fm = g2.getFontMetrics();
        int starW  = fm.stringWidth(GameConfig.ICON_STAR_LIT) + 3;
        int startX = cx - (shown * starW) / 2;

        for (int i = 0; i < shown; i++) {
            boolean isLit = (i < lit);
            g2.setColor(isLit ? GameConfig.STAR_LIT_COLOR : GameConfig.STAR_DIM_COLOR);
            g2.drawString(isLit ? GameConfig.ICON_STAR_LIT : GameConfig.ICON_STAR_DIM,
                          startX + i * starW, y);
        }
    }
}
