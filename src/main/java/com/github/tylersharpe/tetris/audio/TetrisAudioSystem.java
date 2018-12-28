package com.github.tylersharpe.tetris.audio;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

public interface TetrisAudioSystem {

    // Used when running the no-audio distribution
    class NoopTetrisAudioSystem implements TetrisAudioSystem {

        private static NoopTetrisAudioSystem INSTANCE;

        private static NoopTetrisAudioSystem getInstance() {
            return INSTANCE == null ? INSTANCE = new NoopTetrisAudioSystem() : INSTANCE;
        }

        public void setSoundtrackEnabled(boolean enabled) {}
        public void setEffectsEnabled(boolean enabled) {}
        public void startSoundtrack(int level) {}
        public void resumeCurrentSoundtrack() {}
        public void stopCurrentSoundtrack() {}
        public void playGameOverSound() {}
        public void playVictoryFanfare() {}
        public void playPauseSound() {}
        public void playBlockPlacementSound() {}
        public void playHoldSound() {}
        public void playReleaseSound() {}
        public void playClearLineSound(int lineCount) {}
        public void playCWRotationSound() {}
        public void playCCWRotationSound() {}
        public void playSuperSlideSound() {}
        public void stopVictoryFanfare() {}
        public void stopGameOverSound() {}
    }

    static TetrisAudioSystem getInstance() {
        URL manifestUrl = TetrisAudioSystem.class.getResource("/META-INF/MANIFEST.MF");
        if (manifestUrl == null) {
            throw new RuntimeException("jar manifest file not found");
        }

        try (var manifestStream = manifestUrl.openStream()) {
            Manifest manifest = new Manifest(manifestStream);
            String audioEnabledAttr = manifest.getMainAttributes().getValue("Audio-Enabled");
            boolean audioEnabled = audioEnabledAttr == null || Boolean.parseBoolean(audioEnabledAttr);
            return audioEnabled ? DefaultTetrisAudioSystem.getInstance() : NoopTetrisAudioSystem.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Could not read JAR manifest file", e);
        }
    }

    void setSoundtrackEnabled(boolean enabled);

    void setEffectsEnabled(boolean enabled);

    void startSoundtrack(int level);

    void resumeCurrentSoundtrack();

    void stopCurrentSoundtrack();

    void playGameOverSound();

    void playVictoryFanfare();

    void playPauseSound();

    void playBlockPlacementSound();

    void playHoldSound();

    void playReleaseSound();

    void playClearLineSound(int lineCount);

    void playCWRotationSound();

    void playCCWRotationSound();

    void playSuperSlideSound();

    void stopVictoryFanfare();

    void stopGameOverSound();
}