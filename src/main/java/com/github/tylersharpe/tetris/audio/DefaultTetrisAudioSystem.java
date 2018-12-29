package com.github.tylersharpe.tetris.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

class DefaultTetrisAudioSystem implements TetrisAudioSystem {

  private static DefaultTetrisAudioSystem INSTANCE;

  private static final Clip SOUNDTRACK_CLIP = retrieveSystemAudioClip();

  private static final String[] SOUNDTRACK_FILE_PATHS = {
    "/audio/soundtrack/tetris-theme.wav",
    "/audio/soundtrack/bean-machine-1-4.wav",
    "/audio/soundtrack/tetris-music-3.wav",
    "/audio/soundtrack/metroid-kraid.wav",
    "/audio/soundtrack/sonic-scrap-brain-zone.wav",
    "/audio/soundtrack/chrono-trigger-bike-theme.wav",
    "/audio/soundtrack/mega-man-dr-wily.wav",
    "/audio/soundtrack/sonic-ice-cap-zone.wav",
    "/audio/soundtrack/bean-machine-9-12.wav",
    "/audio/soundtrack/chrono-trigger-final-battle.wav"
  };

  private static final String GAME_OVER_FILE_PATH = "/audio/soundtrack/zelda-game-over.wav";
  private static final String VICTORY_FANFARE_FILE_PATH = "/audio/soundtrack/ff1-victory-fanfare.wav";

  // Fully buffered effects clips
  private static final Clip PAUSE = createBufferedClip("/audio/effects/mario-64-pause.wav");
  private static final Clip PLACE_BLOCK = createBufferedClip("/audio/effects/pipe.wav");
  private static final Clip CLEAR_LINE = createBufferedClip("/audio/effects/laser.wav");
  private static final Clip ULTRA_LINE = createBufferedClip("/audio/effects/explosion.wav");
  private static final Clip SWIPE_UP = createBufferedClip("/audio/effects/swish-up.wav");
  private static final Clip SWIPE_DOWN = createBufferedClip("/audio/effects/swish-down.wav");
  private static final Clip SUPER_SLIDE = createBufferedClip("/audio/effects/superslide.wav");
  private static final Clip HOLD = createBufferedClip("/audio/effects/clang.wav");
  private static final Clip RELEASE = createBufferedClip("/audio/effects/water-drop.wav");

  private boolean soundtrackEnabled = true;
  private boolean effectsEnabled = true;

  public DefaultTetrisAudioSystem() {}

  static DefaultTetrisAudioSystem getInstance() {
    return INSTANCE == null ? INSTANCE = new DefaultTetrisAudioSystem() : INSTANCE;
  }

  public void setSoundtrackEnabled(boolean enabled) {
    this.soundtrackEnabled = enabled;
  }

  public void setEffectsEnabled(boolean enabled) {
    this.effectsEnabled = enabled;
  }

  public void startSoundtrack(int level) {
    if (soundtrackEnabled) {
      SOUNDTRACK_CLIP.close(); // Close out stream to current level
      openAudioStream(SOUNDTRACK_CLIP, SOUNDTRACK_FILE_PATHS[level - 1]);
      SOUNDTRACK_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public void resumeCurrentSoundtrack() {
    if (soundtrackEnabled) {
      SOUNDTRACK_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public void stopCurrentSoundtrack() {
    if (soundtrackEnabled) {
      SOUNDTRACK_CLIP.stop();
    }
  }

  public void playGameOverSound() {
    if (soundtrackEnabled) {
      SOUNDTRACK_CLIP.close();
      openAudioStream(SOUNDTRACK_CLIP, GAME_OVER_FILE_PATH);
      SOUNDTRACK_CLIP.start();
    }
  }

  public void playVictoryFanfare() {
    if (soundtrackEnabled) {
      SOUNDTRACK_CLIP.close();
      openAudioStream(SOUNDTRACK_CLIP, VICTORY_FANFARE_FILE_PATH);
      SOUNDTRACK_CLIP.start();
    }
  }

  public void playPauseSound() {
    play(PAUSE);
  }

  public void playBlockPlacementSound() {
    play(PLACE_BLOCK);
  }

  public void playHoldSound() {
    play(HOLD);
  }

  public void playReleaseSound() {
    play(RELEASE);
  }

  public void playClearLineSound(int lineCount) {
    play(lineCount == 4 ? ULTRA_LINE : CLEAR_LINE);
  }

  public void playCWRotationSound() {
    play(SWIPE_UP);
  }

  public void playCCWRotationSound() {
    play(SWIPE_DOWN);
  }

  public void playSuperSlideSound() {
    play(SUPER_SLIDE);
  }

  private void play(Clip effect) {
    if (effectsEnabled) {
      effect.setFramePosition(0);
      effect.start();
    }
  }

  private static Clip createBufferedClip(String audioFilePath) {
    Clip clip = retrieveSystemAudioClip();
    openAudioStream(clip, audioFilePath);
    return clip;
  }

  private static void openAudioStream(Clip clip, String audioFilePath) {
    URL audioFile = DefaultTetrisAudioSystem.class.getResource(audioFilePath);
    if (audioFile == null) {
      throw new AudioFileNotFound(audioFilePath);
    }

    try {
      clip.open(AudioSystem.getAudioInputStream(audioFile));
    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
      throw new RuntimeException("Could not open audio stream for path '" + audioFilePath + "'", e);
    }
  }

  private static Clip retrieveSystemAudioClip() {
    try {
      return AudioSystem.getClip();
    } catch (LineUnavailableException e) {
      throw new RuntimeException("Could not retrieve system audio clip", e);
    }
  }

}
