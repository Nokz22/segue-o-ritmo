package com.followtheritm.service;

import com.followtheritm.config.GameConfig;
import com.followtheritm.model.SimonColor;

import javax.sound.sampled.*;

/** Singleton — SourceDataLine resources are shared across the JVM lifetime. */
public final class AudioServiceImpl implements AudioService {

    private static final AudioServiceImpl INSTANCE = new AudioServiceImpl();

    private AudioServiceImpl() {}

    public static AudioServiceImpl getInstance() { return INSTANCE; }

    @Override
    public void playColorTone(SimonColor color) {
        playTone(color.getFrequencyHz(), GameConfig.FLASH_DURATION_MS, false);
    }

    @Override
    public void playError() {
        // Descending pitch: play note then drop by one semitone to signal failure
        playTone(GameConfig.ERROR_FREQ_HZ + 30, GameConfig.ERROR_DURATION_MS / 2, false);
        playTone(GameConfig.ERROR_FREQ_HZ,      GameConfig.ERROR_DURATION_MS / 2, false);
    }

    @Override
    public void playSuccess() {
        for (int freq : GameConfig.SUCCESS_FREQS_HZ) {
            playTone(freq, GameConfig.SUCCESS_NOTE_DURATION_MS, false);
        }
    }

    /** Generates and immediately plays a sine-wave tone on a background thread. */
    private void playTone(int frequencyHz, int durationMs, boolean blocking) {
        Runnable task = () -> {
            try {
                byte[] buffer = generateSineWave(frequencyHz, durationMs);
                AudioFormat format = new AudioFormat(GameConfig.SAMPLE_RATE, 8, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) return;

                try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                    line.open(format);
                    line.start();
                    line.write(buffer, 0, buffer.length);
                    line.drain();
                }
            } catch (LineUnavailableException ex) {
                System.err.println("Audio line unavailable: " + ex.getMessage());
            }
        };

        if (blocking) {
            task.run();
        } else {
            Thread t = new Thread(task, "audio-tone");
            t.setDaemon(true);
            t.start();
        }
    }

    private byte[] generateSineWave(int frequencyHz, int durationMs) {
        int samples = (int) (GameConfig.SAMPLE_RATE * durationMs / 1000.0);
        byte[] buffer = new byte[samples];
        double angleStep = 2.0 * Math.PI * frequencyHz / GameConfig.SAMPLE_RATE;
        // Envelope: 10ms fade-in/out prevents clicking artefacts
        int fadeFrames = Math.min(GameConfig.SAMPLE_RATE / 100, samples / 4);
        for (int i = 0; i < samples; i++) {
            double amplitude = 127.0;
            if (i < fadeFrames)            amplitude *= (double) i / fadeFrames;
            else if (i > samples - fadeFrames) amplitude *= (double) (samples - i) / fadeFrames;
            buffer[i] = (byte) (amplitude * Math.sin(angleStep * i));
        }
        return buffer;
    }
}
