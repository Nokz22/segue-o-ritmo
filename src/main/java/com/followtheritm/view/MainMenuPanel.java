package com.followtheritm.view;

import com.followtheritm.model.GameMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Full-screen landing panel presented before the game starts.
 * Two large touch-friendly cards let the player choose between
 * Classic (growing sequence) and Random (fresh sequence each round).
 */
public class MainMenuPanel extends JPanel {

    private Consumer<GameMode> onModeSelected = m -> {};

    public MainMenuPanel() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildCardsPanel(), BorderLayout.CENTER);
    }

    public void setOnModeSelected(Consumer<GameMode> h) { onModeSelected = h; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, new Color(28, 28, 40),
                                      0, getHeight(), new Color(18, 18, 28)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    // ── Title ─────────────────────────────────────────────────────────────

    private static JPanel buildTitlePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 20, 40));

        JLabel title = new JLabel("Segue o Ritmo", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 52));
        title.setForeground(new Color(200, 210, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(14));

        JLabel sub = new JLabel("Escolhe o modo de jogo", SwingConstants.CENTER);
        sub.setFont(new Font("Dialog", Font.PLAIN, 22));
        sub.setForeground(new Color(130, 140, 170));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sub);

        return panel;
    }

    // ── Cards ─────────────────────────────────────────────────────────────

    private JPanel buildCardsPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 40, 0));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(30, 80, 80, 80));

        wrapper.add(new ModeCard(
                GameMode.CLASSIC,
                "Clássico",
                "Sequência crescente",
                new Color(25, 155, 75), new Color(15, 110, 48),
                m -> onModeSelected.accept(m)));

        wrapper.add(new ModeCard(
                GameMode.RANDOM,
                "Aleatório",
                "Sequência nova",
                new Color(70, 50, 190), new Color(44, 28, 140),
                m -> onModeSelected.accept(m)));

        return wrapper;
    }

    // ── ModeCard ──────────────────────────────────────────────────────────

    private static final class ModeCard extends JPanel {

        private final GameMode           mode;
        private final String             cardTitle, subtitle;
        private final Color              topColor, botColor;
        private final Consumer<GameMode> onClick;
        private boolean hovered = false;
        private boolean pressed = false;

        ModeCard(GameMode mode, String cardTitle, String subtitle,
                 Color top, Color bot, Consumer<GameMode> onClick) {
            this.mode      = mode;
            this.cardTitle = cardTitle;
            this.subtitle  = subtitle;
            this.topColor  = top;
            this.botColor  = bot;
            this.onClick   = onClick;
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    pressed = true; repaint();
                }
                @Override public void mouseReleased(MouseEvent e) {
                    if (pressed && contains(e.getPoint())) onClick.accept(mode);
                    pressed = false; repaint();
                }
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(6, 6, w - 12, h - 12, 32, 32);

            // Background gradient — pressed inverts it for tactile feel
            Color t = pressed ? botColor : hovered ? brighter(topColor) : topColor;
            Color b = pressed ? topColor : hovered ? brighter(botColor) : botColor;
            g2.setPaint(new GradientPaint(0, 6, t, 0, h - 6, b));
            g2.fill(shape);

            // Top sheen
            g2.setColor(new Color(255, 255, 255, hovered ? 55 : 35));
            g2.fillRoundRect(8, 8, w - 16, (h - 16) / 2, 28, 28);

            // Border
            g2.setColor(new Color(255, 255, 255, hovered ? 200 : 70));
            g2.setStroke(new BasicStroke(hovered ? 3f : 2f));
            g2.draw(shape);

            // Mode icon in the upper portion of the card
            int iconCY = h * 2 / 5;
            if (mode == GameMode.CLASSIC) drawClassicIcon(g2, w / 2, iconCY, w);
            else                          drawRandomIcon (g2, w / 2, iconCY, w);

            // Title
            int titleSize = clamp(w / 9, 18, 30);
            Font titleFont = new Font("Dialog", Font.BOLD, titleSize);
            g2.setFont(titleFont);
            g2.setColor(Color.WHITE);
            FontMetrics tfm = g2.getFontMetrics();
            int titleY = h * 2 / 3 + tfm.getAscent() / 2;
            g2.drawString(cardTitle, (w - tfm.stringWidth(cardTitle)) / 2, titleY);

            // Subtitle
            int subSize = clamp(w / 13, 13, 18);
            Font subFont = new Font("Dialog", Font.PLAIN, subSize);
            g2.setFont(subFont);
            g2.setColor(new Color(220, 230, 255, 170));
            FontMetrics sfm = g2.getFontMetrics();
            int subY = titleY + sfm.getHeight() + 6;
            g2.drawString(subtitle, (w - sfm.stringWidth(subtitle)) / 2, subY);

            g2.dispose();
        }

        // ── Classic icon: row of coloured squares with a downward arrow ───

        private static void drawClassicIcon(Graphics2D g2, int cx, int cy, int cw) {
            int box = clamp(cw / 8, 20, 38);
            int gap = box / 4 + 2;
            Color[] clrs = {
                new Color(255, 90, 90),
                new Color(90, 150, 255),
                new Color(80, 230, 80),
                new Color(255, 235, 85)
            };
            int totalW = 4 * box + 3 * gap;
            int sx = cx - totalW / 2;
            int sy = cy - box / 2;

            for (int i = 0; i < 4; i++) {
                // Each block progressively brighter — last one is newest addition
                int alpha = 110 + i * 36;
                g2.setColor(new Color(clrs[i].getRed(), clrs[i].getGreen(), clrs[i].getBlue(), alpha));
                int bx = sx + i * (box + gap);
                g2.fillRoundRect(bx, sy, box, box, 10, 10);
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(bx, sy, box, box, 10, 10);
            }

            // Down-arrow below the last block indicating "next one added"
            int arrowX = sx + totalW - box / 2;
            int arrowY = sy + box + 10;
            g2.setColor(new Color(255, 255, 255, 185));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(arrowX, arrowY, arrowX, arrowY + 14);
            g2.drawLine(arrowX - 6, arrowY + 8, arrowX, arrowY + 14);
            g2.drawLine(arrowX + 6, arrowY + 8, arrowX, arrowY + 14);
        }

        // ── Random icon: scattered squares + shuffle arrows ───────────────

        private static void drawRandomIcon(Graphics2D g2, int cx, int cy, int cw) {
            int box  = clamp(cw / 9, 18, 32);
            int half = box / 2 + 12;
            Color[] clrs = {
                new Color(255, 90, 90),
                new Color(90, 150, 255),
                new Color(80, 230, 80),
                new Color(255, 235, 85)
            };
            int[][] offsets = {
                {-half - box, -half - box / 2},
                { half,       -half - box / 2},
                { half,        half - box / 2},
                {-half - box,  half - box / 2}
            };
            for (int i = 0; i < 4; i++) {
                g2.setColor(new Color(clrs[i].getRed(), clrs[i].getGreen(), clrs[i].getBlue(), 190));
                g2.fillRoundRect(cx + offsets[i][0], cy + offsets[i][1], box, box, 10, 10);
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(cx + offsets[i][0], cy + offsets[i][1], box, box, 10, 10);
            }

            // Two crossing shuffle arrows in the centre
            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int r = 14;
            // Forward arrow →
            g2.drawLine(cx - r, cy - 7, cx + r, cy - 7);
            g2.drawLine(cx + r - 6, cy - 13, cx + r, cy - 7);
            g2.drawLine(cx + r - 6, cy - 1,  cx + r, cy - 7);
            // Back arrow ←
            g2.drawLine(cx + r, cy + 7, cx - r, cy + 7);
            g2.drawLine(cx - r + 6, cy + 1,  cx - r, cy + 7);
            g2.drawLine(cx - r + 6, cy + 13, cx - r, cy + 7);
        }

        private static Color brighter(Color c) {
            int r = Math.min(255, c.getRed()   + 28);
            int g = Math.min(255, c.getGreen() + 28);
            int b = Math.min(255, c.getBlue()  + 28);
            return new Color(r, g, b);
        }

        private static int clamp(int v, int lo, int hi) {
            return Math.max(lo, Math.min(hi, v));
        }
    }
}
