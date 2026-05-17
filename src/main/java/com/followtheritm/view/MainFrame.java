package com.followtheritm.view;

import com.followtheritm.config.GameConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Root window.
 *
 * Layout uses CardLayout to switch between the main menu and the game screen.
 * Full-screen toggle: dispose → setUndecorated(true) → GraphicsDevice.setFullScreenWindow()
 * Windowed:          GraphicsDevice.setFullScreenWindow(null) → dispose → setUndecorated(false)
 *
 * The game area is always square regardless of screen aspect ratio; SquareWrapper
 * centres the largest possible square and fills the remainder with the dark background.
 */
public class MainFrame extends JFrame {

    private static final String CARD_MENU = "menu";
    private static final String CARD_GAME = "game";

    private final GraphicsDevice  graphicsDevice;
    private final SimonPanel      simonPanel;
    private final RoundIndicator  roundIndicator;
    private final ControlPanel    controlPanel;
    private final MainMenuPanel   mainMenuPanel;

    private final CardLayout cardLayout    = new CardLayout();
    private final JPanel     cardContainer = new JPanel(cardLayout);

    private boolean fullScreen = false;

    public MainFrame() {
        graphicsDevice = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        setTitle("Segue o Ritmo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setJMenuBar(null);

        roundIndicator = new RoundIndicator();
        simonPanel     = new SimonPanel();
        controlPanel   = new ControlPanel();
        mainMenuPanel  = new MainMenuPanel();

        buildContentPane();
        bindKeys();
    }

    // ── Layout ────────────────────────────────────────────────────────────

    private void buildContentPane() {
        // Root panel with dark gradient fill
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(28, 28, 40),
                                              0, getHeight(), new Color(18, 18, 28)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(true);

        // Card 1: main menu
        cardContainer.setOpaque(false);
        cardContainer.add(mainMenuPanel, CARD_MENU);

        // Card 2: game screen
        cardContainer.add(buildGamePanel(), CARD_GAME);

        root.add(cardContainer, BorderLayout.CENTER);
        setContentPane(root);

        showMainMenu();
    }

    private JPanel buildGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout(0, 0));
        gamePanel.setOpaque(false);
        gamePanel.add(roundIndicator,             BorderLayout.NORTH);
        gamePanel.add(new SquareWrapper(simonPanel), BorderLayout.CENTER);
        gamePanel.add(controlPanel,               BorderLayout.SOUTH);
        return gamePanel;
    }

    // ── Card switching ────────────────────────────────────────────────────

    public void showMainMenu() { cardLayout.show(cardContainer, CARD_MENU); }
    public void showGame()     { cardLayout.show(cardContainer, CARD_GAME); }

    // ── Full-screen toggle ────────────────────────────────────────────────

    public void toggleFullScreen() {
        if (!fullScreen) {
            dispose();
            setUndecorated(true);
            if (graphicsDevice.isFullScreenSupported()) {
                graphicsDevice.setFullScreenWindow(this);
            } else {
                setVisible(true);
                setExtendedState(MAXIMIZED_BOTH);
            }
            fullScreen = true;
        } else {
            if (graphicsDevice.isFullScreenSupported()) {
                graphicsDevice.setFullScreenWindow(null);
            }
            dispose();
            setUndecorated(false);
            setSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
            setLocationRelativeTo(null);
            setVisible(true);
            fullScreen = false;
        }
        controlPanel.setFullScreenLabel(!fullScreen);
    }

    public boolean isFullScreen() { return fullScreen; }

    // ── Key bindings ──────────────────────────────────────────────────────

    private void bindKeys() {
        Action toggle = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { toggleFullScreen(); }
        };
        Action exitFs = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (fullScreen) toggleFullScreen();
            }
        };
        JRootPane rp = getRootPane();
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
          .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFs");
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
          .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitFs");
        rp.getActionMap().put("toggleFs", toggle);
        rp.getActionMap().put("exitFs",   exitFs);
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public SimonPanel     getSimonPanel()     { return simonPanel; }
    public RoundIndicator getRoundIndicator() { return roundIndicator; }
    public ControlPanel   getControlPanel()   { return controlPanel; }
    public MainMenuPanel  getMainMenuPanel()  { return mainMenuPanel; }

    // ── Inner: square-preserving wrapper ─────────────────────────────────

    private static final class SquareWrapper extends JPanel {

        SquareWrapper(JComponent child) {
            super(null);
            setOpaque(false);
            add(child);
        }

        @Override
        public void doLayout() {
            if (getComponentCount() == 0) return;
            int w    = getWidth();
            int h    = getHeight();
            int side = Math.min(w, h);
            int x    = (w - side) / 2;
            int y    = (h - side) / 2;
            getComponent(0).setBounds(x, y, side, side);
        }
    }
}
