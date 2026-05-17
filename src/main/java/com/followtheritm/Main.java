package com.followtheritm;

import com.followtheritm.controller.SimonController;
import com.followtheritm.model.GameSession;
import com.followtheritm.service.AudioServiceImpl;
import com.followtheritm.service.SequenceServiceImpl;
import com.followtheritm.view.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::bootstrap);
    }

    private static void bootstrap() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        GameSession         session  = new GameSession();
        SequenceServiceImpl seqSvc   = new SequenceServiceImpl();
        AudioServiceImpl    audioSvc = AudioServiceImpl.getInstance();

        MainFrame frame = new MainFrame();

        SimonController controller = new SimonController(
                session,
                seqSvc,
                audioSvc,
                frame.getSimonPanel(),
                frame.getRoundIndicator(),
                frame.getControlPanel()
        );

        // Menu → game: set the chosen mode then flip to the game card
        frame.getMainMenuPanel().setOnModeSelected(mode -> {
            controller.setGameMode(mode);
            frame.showGame();
        });

        // ← Menu button: stop any running game and return to the menu
        frame.getControlPanel().setOnMenuPressed(() -> {
            controller.stop();
            frame.showMainMenu();
        });

        frame.getControlPanel().setOnStart(controller::onStartPressed);
        frame.getControlPanel().setOnFullScreenToggle(frame::toggleFullScreen);

        // Start full-screen — ideal for touch-screen kiosk use
        if (Boolean.getBoolean("windowed")) frame.setVisible(true);
        else frame.toggleFullScreen();
    }
}
