package com.github.tylerwill.tetris;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/** Provides an interface to the game's audio */
public final class TetrisAudioSystem {

  private static final Clip[] SOUNDTRACK = {
    createClip("/audio/soundtrack/tetris-theme.wav"),
    createClip("/audio/soundtrack/bean-machine-1-4.wav"),
    createClip("/audio/soundtrack/tetris-music-3.wav"),
    createClip("/audio/soundtrack/metroid-kraid.wav"),
    createClip("/audio/soundtrack/sonic-scrap-brain-zone.wav"),
    createClip("/audio/soundtrack/chrono-trigger-bike-theme.wav"),
    createClip("/audio/soundtrack/mega-man-dr-wily.wav"),
    createClip("/audio/soundtrack/sonic-ice-cap-zone.wav"),
    createClip("/audio/soundtrack/bean-machine-9-12.wav"),
    createClip("/audio/soundtrack/chrono-trigger-final-battle.wav")
  };

  // Non-looping clips
  private static final Clip GAME_OVER = createClip("/audio/soundtrack/zelda-game-over.wav");
  private static final Clip VICTORY_FANFARE = createClip("/audio/soundtrack/ff1-victory-fanfare.wav");

  // Effects
  private static final Clip PAUSE = createClip("/audio/effects/mario-64-pause.wav");
  private static final Clip PLACE_BLOCK = createClip("/audio/effects/pipe.wav");
  private static final Clip CLEAR_LINE = createClip("/audio/effects/laser.wav");
  private static final Clip ULTRA_LINE = createClip("/audio/effects/explosion.wav");
  private static final Clip SWIPE_UP = createClip("/audio/effects/swish-up.wav");
  private static final Clip SWIPE_DOWN = createClip("/audio/effects/swish-down.wav");
  private static final Clip SUPER_SLIDE = createClip("/audio/effects/superslide.wav");
  private static final Clip HOLD = createClip("/audio/effects/clang.wav");
  private static final Clip RELEASE = createClip("/audio/effects/water-drop.wav");

  private boolean soundtrackEnabled = true;
  private boolean effectsEnabled = true;

  public TetrisAudioSystem() {}

  public void setSoundtrackEnabled(boolean muted) {
    this.soundtrackEnabled = muted;
  }

  public void setEffectsEnabled(boolean muted) {
    this.effectsEnabled = muted;
  }

  public void startSoundtrack(int level) {
    if (soundtrackEnabled) {
      Clip track = getSoundtrack(level);
      track.setFramePosition(0);
      track.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public void resumeSoundtrack(int level) {
    if (soundtrackEnabled) {
      getSoundtrack(level).loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public void stopSoundtrack(int level) {
    if (soundtrackEnabled) {
      Clip track = getSoundtrack(level);
      if (track.isRunning()) {
        track.stop();
      }
    }
  }

  public void playGameOverSound() {
    play(GAME_OVER);
  }

  public void playVictoryFanfare() {
    play(VICTORY_FANFARE);
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

  public void playSuperslideSound() {
    play(SUPER_SLIDE);
  }

  public void stopVictoryFanfare() {
    stop(VICTORY_FANFARE);
  }

  public void stopGameOverSound() {
    stop(GAME_OVER);
  }

  private void play(Clip effect) {
    if (effectsEnabled) {
      effect.setFramePosition(0);
      effect.start();
    }
  }

  private void stop(Clip effect) {
    if (effect.isActive()) {
      effect.setFramePosition(0);
      effect.stop();
    }
  }

  private Clip getSoundtrack(int level) {
    return SOUNDTRACK[level - 1];
  }

  private static Clip createClip(String resourcePath) {
    try {
      URL audioFile = TetrisAudioSystem.class.getResource(resourcePath);
      if (audioFile == null) {
        throw new RuntimeException("Audio file not found for path " + resourcePath);
      }
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
      Clip clip = AudioSystem.getClip();
      clip.open(audioIn);
      return clip;
    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
      throw new RuntimeException(e);
    }
  }

}
