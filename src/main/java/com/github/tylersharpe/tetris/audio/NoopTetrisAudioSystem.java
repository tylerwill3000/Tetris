package com.github.tylersharpe.tetris.audio;

public class NoopTetrisAudioSystem implements TetrisAudioSystem {

    static NoopTetrisAudioSystem INSTANCE;

    static NoopTetrisAudioSystem getInstance() {
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
