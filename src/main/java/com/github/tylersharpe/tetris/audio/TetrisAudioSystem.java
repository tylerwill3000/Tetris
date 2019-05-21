package com.github.tylersharpe.tetris.audio;

import java.io.IOException;
import java.io.InputStream;
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
        public void playClockwiseRotationSound() {}
        public void playCounterClockwiseRotationSound() {}
        public void playSuperSlideSound() {}
    }

    static TetrisAudioSystem getInstance() {
      String audioEnabledAttr;

      URL manifestUrl = TetrisAudioSystem.class.getResource("/META-INF/MANIFEST.MF");
      if (manifestUrl == null) { // Running through IDE (meaning JAR has not been built) - use system properties to determine audio type
        audioEnabledAttr = System.getProperty("audio.enabled");
      } else {
        try (InputStream manifestStream = manifestUrl.openStream()) {
          audioEnabledAttr = new Manifest(manifestStream).getMainAttributes().getValue("Audio-Enabled");
        } catch (IOException e) {
          throw new RuntimeException("Could not read JAR manifest file", e);
        }
      }

      boolean audioEnabled = audioEnabledAttr == null || Boolean.parseBoolean(audioEnabledAttr);
      return audioEnabled ? DefaultTetrisAudioSystem.getInstance() : NoopTetrisAudioSystem.getInstance();
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

    void playClockwiseRotationSound();

    void playCounterClockwiseRotationSound();

    void playSuperSlideSound();

}