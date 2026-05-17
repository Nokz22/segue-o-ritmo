package com.followtheritm.view;

import com.followtheritm.config.GameConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Bottom bar: ▶ Iniciar, speed mode selector, and full-screen toggle.
 *
 * Speed buttons use traffic-light colour coding (green/amber/red) and
 * a dramatic lit/dark contrast so users with cognitive difficulties can
 * identify the active mode without reading the label.
 */
public class ControlPanel extends JPanel {

    public enum Speed { SLOW, NORMAL, FAST }

    private final PillButton        menuButton;
    private final PillButton        startButton;
    private final FullScreenButton  fsButton;
    private final DifficultyButton[] modeButtons = new DifficultyButton[3];

    private Speed    selectedSpeed      = Speed.NORMAL;
    private Runnable onStart            = () -> {};
    private Runnable onFullScreenToggle = () -> {};
    private Runnable onMenuPressed      = () -> {};
    private Consumer<Speed> onSpeedChanged = s -> {};

    public ControlPanel() {
        setOpaque(true);
        setPreferredSize(new Dimension(800, 90));
        setLayout(new FlowLayout(FlowLayout.CENTER, 16, 13));

        // ← Menu
        menuButton = new PillButton(GameConfig.LABEL_MENU,
                                    new Color(70, 70, 95), new Color(50, 50, 72));
        menuButton.setPreferredSize(new Dimension(130, 62));
        menuButton.addActionListener(e -> onMenuPressed.run());
        add(menuButton);

        add(buildSeparator());

        // ▶ Iniciar
        startButton = new PillButton(GameConfig.LABEL_START,
                                     new Color(38, 185, 78), new Color(26, 140, 56));
        startButton.setPreferredSize(new Dimension(172, 62));
        startButton.addActionListener(e -> onStart.run());
        add(startButton);

        add(buildSeparator());

        // Speed mode buttons — colour-coded, dramatically different when active
        String[] labels  = { GameConfig.LABEL_SLOW,          GameConfig.LABEL_NORMAL,           GameConfig.LABEL_FAST };
        Color[]  tops    = { new Color( 30, 160,  50),        new Color(200, 140,   0),           new Color(210,  35,  35) };
        Color[]  bots    = { new Color( 18, 115,  30),        new Color(155,  95,   0),           new Color(160,  18,  18) };
        Speed[]  speeds  = { Speed.SLOW,                      Speed.NORMAL,                       Speed.FAST };
        int[]    dots    = { 1,                                2,                                  3 };

        for (int i = 0; i < 3; i++) {
            final int   idx = i;
            final Speed spd = speeds[i];
            modeButtons[i] = new DifficultyButton(labels[i], tops[i], bots[i], dots[i],
                                                   spd == selectedSpeed);
            modeButtons[i].setPreferredSize(new Dimension(148, 62));
            modeButtons[i].setOnSelect(() -> {
                selectedSpeed = spd;
                for (int j = 0; j < 3; j++) modeButtons[j].setSelected(j == idx);
                onSpeedChanged.accept(spd);
            });
            add(modeButtons[i]);
        }

        add(buildSeparator());

        fsButton = new FullScreenButton();
        fsButton.addActionListener(e -> onFullScreenToggle.run());
        add(fsButton);
    }

    // ── Public API ────────────────────────────────────────────────────────

    public void setOnStart(Runnable h)                 { onStart = h; }
    public void setOnSpeedChanged(Consumer<Speed> h)   { onSpeedChanged = h; }
    public void setOnFullScreenToggle(Runnable h)      { onFullScreenToggle = h; }
    public void setOnMenuPressed(Runnable h)           { onMenuPressed = h; }
    public void setStartEnabled(boolean e)             { startButton.setEnabled(e); repaint(); }
    public void setFullScreenLabel(boolean isWindowed) { fsButton.setWindowed(isWindowed); fsButton.repaint(); }
    public Speed getSelectedSpeed()                    { return selectedSpeed; }

    // ── Background ────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, new Color(50, 50, 68), 0, getHeight(), new Color(34, 34, 48)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(255, 255, 255, 18));
        g2.drawLine(0, 0, getWidth(), 0);
        g2.dispose();
    }

    private static Component buildSeparator() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 28));
                g.fillRect(0, 10, 1, getHeight() - 20);
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(1, 62));
        return sep;
    }

    // ── DifficultyButton ─────────────────────────────────────────────────

    /**
     * Custom-painted toggle that communicates its state purely through
     * visual contrast: bright saturated colour when active, near-black when
     * inactive. Difficulty dots (●) beneath the label reinforce the meaning
     * without requiring text comprehension.
     */
    private static final class DifficultyButton extends JPanel {

        private static final Font FONT_ON  = new Font("Dialog", Font.BOLD,  19);
        private static final Font FONT_OFF = new Font("Dialog", Font.PLAIN, 15);
        private static final int  DOT_R    = 5;
        private static final int  DOT_GAP  = 6;

        private final String label;   // full original string (kept for reference)
        private final String emoji;   // "🐢"  — rendered alone so its width doesn't corrupt text measurement
        private final String text;    // "Lento" — rendered alone with reliable FontMetrics
        private final Color  activeTop, activeBot;
        private final int    dotCount;
        private boolean      selected;
        private boolean      hovered;
        private Runnable     onSelect = () -> {};

        DifficultyButton(String label, Color activeTop, Color activeBot, int dotCount, boolean selected) {
            this.label = label;
            // GameConfig labels are "🐢  Lento" — split on the two-space separator
            String[] parts = label.split("  ", 2);
            this.emoji = parts.length == 2 ? parts[0].trim() : "";
            this.text  = parts.length == 2 ? parts[1].trim() : label;
            this.activeTop = activeTop;
            this.activeBot = activeBot;
            this.dotCount  = dotCount;
            this.selected  = selected;
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e)  {
                    if (!DifficultyButton.this.selected) { DifficultyButton.this.selected = true; onSelect.run(); repaint(); }
                }
                @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        void setSelected(boolean s) { selected = s; repaint(); }
        void setOnSelect(Runnable r) { onSelect = r; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 18, 18);

            if (selected) {
                // Bright colour fill — unmistakably active
                g2.setPaint(new GradientPaint(0, 2, activeTop, 0, h - 2, activeBot));
                g2.fill(shape);
                // Sheen
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(3, 3, w - 6, (h - 6) / 2, 16, 16);
                // White border
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2.5f));
                g2.draw(shape);
            } else if (hovered) {
                // Hover: hint of colour, not as strong as selected
                g2.setPaint(new GradientPaint(0, 0,
                        new Color(activeTop.getRed(), activeTop.getGreen(), activeTop.getBlue(), 70),
                        0, h,
                        new Color(activeBot.getRed(), activeBot.getGreen(), activeBot.getBlue(), 50)));
                g2.fill(shape);
                g2.setColor(new Color(activeTop.getRed(), activeTop.getGreen(), activeTop.getBlue(), 140));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(shape);
            } else {
                // Dark / inactive — clearly "off"
                g2.setColor(new Color(26, 26, 36));
                g2.fill(shape);
                g2.setColor(new Color(activeTop.getRed(), activeTop.getGreen(), activeTop.getBlue(), 45));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(shape);
            }

            // Text area = everything above the dots row
            int textAreaH = h - DOT_R * 2 - DOT_GAP * 2 - 6;
            Color textColor = selected ? Color.WHITE : new Color(90, 90, 108);

            // ── Emoji (upper half of text area) ──────────────────────────
            // Emoji and plain text are measured and drawn separately because
            // FontMetrics.stringWidth() is unreliable for emoji glyphs,
            // causing text to spill off the button when measured together.
            if (!emoji.isEmpty()) {
                Font emojiFont = new Font("Dialog", Font.PLAIN, selected ? 20 : 17);
                g2.setFont(emojiFont);
                g2.setColor(textColor);
                FontMetrics efm = g2.getFontMetrics();
                int ex = (w - efm.stringWidth(emoji)) / 2;
                int ey = textAreaH * 2 / 5 + efm.getAscent() / 2;
                g2.drawString(emoji, ex, ey);
            }

            // ── Plain text (lower half of text area) ──────────────────────
            Font textFont = selected ? FONT_ON : FONT_OFF;
            g2.setFont(textFont);
            g2.setColor(textColor);
            FontMetrics tfm = g2.getFontMetrics();
            int tx = (w - tfm.stringWidth(text)) / 2;
            int ty = textAreaH * 3 / 4 + tfm.getAscent() / 2;
            g2.drawString(text, tx, ty);

            // Difficulty dots — quantity encodes mode visually, no reading needed
            int totalDotsW = dotCount * (DOT_R * 2) + (dotCount - 1) * DOT_GAP;
            int dx         = (w - totalDotsW) / 2;
            int dy         = h - DOT_R - 6;
            Color dotColor = selected
                    ? new Color(255, 255, 255, 210)
                    : new Color(activeTop.getRed(), activeTop.getGreen(), activeTop.getBlue(), 75);
            g2.setColor(dotColor);
            for (int i = 0; i < dotCount; i++) {
                g2.fillOval(dx + i * (DOT_R * 2 + DOT_GAP), dy - DOT_R, DOT_R * 2, DOT_R * 2);
            }

            g2.dispose();
        }
    }

    // ── PillButton ────────────────────────────────────────────────────────

    private static class PillButton extends JButton {
        private final Color topColor, botColor;

        PillButton(String text, Color top, Color bot) {
            super(text);
            topColor = top; botColor = bot;
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setFont(GameConfig.BUTTON_FONT);
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, h, h);
            float a = isEnabled() ? 1f : 0.42f;
            Color t = withAlpha(topColor, a), b = withAlpha(botColor, a);
            g2.setPaint(getModel().isPressed()
                    ? new GradientPaint(0, 0, b, 0, h, t)
                    : getModel().isRollover() && isEnabled()
                        ? new GradientPaint(0, 0, t.brighter(), 0, h, t)
                        : new GradientPaint(0, 0, t, 0, h, b));
            g2.fill(shape);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.fillRoundRect(2, 2, w - 4, h / 2, h - 4, h - 4);
            g2.setColor(new Color(0, 0, 0, 55));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(shape);
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color withAlpha(Color c, float a) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255 * a));
        }
    }

    // ── FullScreenButton ──────────────────────────────────────────────────

    private static final class FullScreenButton extends JButton {
        private boolean windowed = false;

        FullScreenButton() {
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setPreferredSize(new Dimension(52, 52));
            setToolTipText(GameConfig.LABEL_FULLSCREEN + " (F11)");
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setWindowed(boolean w) { windowed = w; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D bg = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 12, 12);
            if (getModel().isRollover() || getModel().isPressed()) {
                g2.setColor(new Color(255, 255, 255, 28)); g2.fill(bg);
            }
            g2.setColor(new Color(255, 255, 255, 48));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(bg);
            g2.setColor(new Color(190, 200, 255));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int m = 12, arm = 5;
            if (!windowed) {
                corner(g2, m,     m,     arm,  1,  1);
                corner(g2, w-m-1, m,     arm, -1,  1);
                corner(g2, m,     h-m-1, arm,  1, -1);
                corner(g2, w-m-1, h-m-1, arm, -1, -1);
            } else {
                inCorner(g2, m+arm,     m+arm,     arm,  1,  1);
                inCorner(g2, w-m-arm-1, m+arm,     arm, -1,  1);
                inCorner(g2, m+arm,     h-m-arm-1, arm,  1, -1);
                inCorner(g2, w-m-arm-1, h-m-arm-1, arm, -1, -1);
            }
            g2.dispose();
        }

        private static void corner  (Graphics2D g, int x, int y, int a, int dx, int dy) {
            g.drawLine(x, y, x+a*dx, y); g.drawLine(x, y, x, y+a*dy);
        }
        private static void inCorner(Graphics2D g, int x, int y, int a, int dx, int dy) {
            g.drawLine(x, y, x-a*dx, y); g.drawLine(x, y, x, y-a*dy);
        }
    }
}
