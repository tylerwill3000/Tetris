package com.github.tylersharpe.tetris.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class TetrisAudioSystem {
    private static final TetrisAudioSystem INSTANCE = new TetrisAudioSystem();

    private static final Clip SOUNDTRACK_CLIP = getNewSystemAudioClip();
    private static final AudioFile[] SOUNDTRACK = {
        AudioFile.TETRIS_THEME,
        AudioFile.BEAN_MACHINE_BEGINNER,
        AudioFile.TETRIS_3,
        AudioFile.METROID1_KRAID_THEME,
        AudioFile.SONIC_SCRAP_BRAIN_ZONE,
        AudioFile.CHRONO_TRIGGER_BIKE_THEME,
        AudioFile.MEGA_MAN_DR_WILY_THEME,
        AudioFile.SONIC_ICE_CAP_ZONE,
        AudioFile.BEAN_MACHINE_ADVANCED,
        AudioFile.CHRONO_TRIGGER_FINAL_BATTLE
    };

    private static final Clip PAUSE = createBufferedClip(AudioFile.MARIO_64_PAUSE);
    private static final Clip PLACE_BLOCK = createBufferedClip(AudioFile.PIPE);
    private static final Clip CLEAR_LINE = createBufferedClip(AudioFile.LASER);
    private static final Clip ULTRA_LINE = createBufferedClip(AudioFile.EXPLOSION);
    private static final Clip SWISH_UP = createBufferedClip(AudioFile.SWISH_UP);
    private static final Clip SWISH_DOWN = createBufferedClip(AudioFile.SWISH_DOWN);
    private static final Clip SUPER_SLIDE = createBufferedClip(AudioFile.SUPER_SLIDE);
    private static final Clip HOLD = createBufferedClip(AudioFile.CLANG);
    private static final Clip RELEASE = createBufferedClip(AudioFile.WATER_DROP);

    private boolean soundtrackEnabled = true;
    private boolean effectsEnabled = true;

    public static TetrisAudioSystem getInstance() {
        return INSTANCE;
    }

    public void setSoundtrackEnabled(boolean enabled) {
        this.soundtrackEnabled = enabled;
    }

    public boolean isSoundtrackEnabled() {
        return soundtrackEnabled;
    }

    public void setEffectsEnabled(boolean enabled) {
        this.effectsEnabled = enabled;
    }

    public boolean isEffectsEnabled() {
        return effectsEnabled;
    }

    public void startSoundtrack(int level) {
        loadAudioStream(SOUNDTRACK_CLIP, SOUNDTRACK[level - 1]);
        resumeCurrentSoundtrack();
    }

    public void resumeCurrentSoundtrack() {
        if (soundtrackEnabled) {
            SOUNDTRACK_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopCurrentSoundtrack() {
        SOUNDTRACK_CLIP.stop();
    }

    public void playGameOverSound() {
        loadAudioStream(SOUNDTRACK_CLIP, AudioFile.ZELDA_GAME_OVER);

        if (soundtrackEnabled) {
            SOUNDTRACK_CLIP.start();
        }
    }

    public void playVictoryFanfare() {
        loadAudioStream(SOUNDTRACK_CLIP, AudioFile.FINAL_FANTASY_VICTORY_FANFARE);

        if (soundtrackEnabled) {
            SOUNDTRACK_CLIP.start();
        }
    }

    public void playPauseSound() {
        playEffect(PAUSE);
    }

    public void playTetronimoPlacementSound() {
        playEffect(PLACE_BLOCK);
    }

    public void playHoldSound() {
        playEffect(HOLD);
    }

    public void playReleaseSound() {
        playEffect(RELEASE);
    }

    public void playClearLineSound(int lineCount) {
        playEffect(lineCount == 4 ? ULTRA_LINE : CLEAR_LINE);
    }

    public void playClockwiseRotationSound() {
        playEffect(SWISH_UP);
    }

    public void playCounterClockwiseRotationSound() {
        playEffect(SWISH_DOWN);
    }

    public void playSuperSlideSound() {
        playEffect(SUPER_SLIDE);
    }

    private void playEffect(Clip effect) {
        if (effectsEnabled) {
            effect.setFramePosition(0);
            effect.start();
        }
    }

    private static Clip createBufferedClip(AudioFile audioFile) {
        Clip clip = getNewSystemAudioClip();
        loadAudioStream(clip, audioFile);
        return clip;
    }

    private static void loadAudioStream(Clip clip, AudioFile audioFile) {
        try {
            clip.close();
            clip.open(AudioSystem.getAudioInputStream(audioFile.getUrl()));
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException("Could not open audio stream for " + audioFile.getUrl(), e);
        }
    }

    private static Clip getNewSystemAudioClip() {
        try {
            return AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Could not retrieve system audio clip", e);
        }
    }
}
