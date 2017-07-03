package tetris;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Provides an interface to the game's audio
 * @author Tyler
 */
public final class TetrisAudioSystem {
	
	// Soundtrack for the game
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
	private static final Clip SUPERSLIDE = createClip("/audio/effects/superslide.wav");
	private static final Clip HOLD = createClip("/audio/effects/clang.wav");
	private static final Clip RELEASE = createClip("/audio/effects/water-drop.wav");
	
	private boolean soundtrackMuted = false;
	private boolean effectsMuted = false;
	
	public TetrisAudioSystem() {}
	
	public void setSoundtrackMuted(boolean muted) {
		this.soundtrackMuted = muted;
	}
	
	public void setEffectsMuted(boolean muted) {
		this.effectsMuted = muted;
	}
	
	public void startSoundtrack(int level) {
		if (!soundtrackMuted) {
			Clip track = getSoundtrack(level);
			if (track != null) {
				track.setFramePosition(0);
				track.loop(Clip.LOOP_CONTINUOUSLY);
			}
		}
	}
	
	public void resumeSoundtrack(int level) {
		if (!soundtrackMuted) {
			Clip track = getSoundtrack(level);
			if (track != null) {
				track.loop(Clip.LOOP_CONTINUOUSLY);
			}
		}
	}
	
	public void stopSoundtrack(int level) {
		if (!soundtrackMuted) {
			Clip track = getSoundtrack(level);
			if (track != null && track.isRunning()) {
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
		play(SUPERSLIDE);
	}

	public void stopVictoryFanfare() {
		stop(VICTORY_FANFARE);
	}
	
	public void stopGameOverSound() {
		stop(GAME_OVER);
	}
	
	private void play(Clip effect) {
		if (effect != null && !effectsMuted) {
			effect.start();
			effect.setFramePosition(0);
		}
	}
	
	private void stop(Clip effect) {
		if (effect != null && effect.isActive()) {
			effect.stop();
			effect.setFramePosition(0);
		}
	}
	
	private Clip getSoundtrack(int level) {
		return SOUNDTRACK[level - 1];
	}
	
	/** @returns A clip audio output device input line from the specified file string */
	private static Clip createClip(String file) {
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER) || AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
			try {
				URL audioFile = TetrisAudioSystem.class.getResource(file);
				if (audioFile == null) {
					throw new RuntimeException("Audio file not found for path " + file);
				}
				AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
				Clip clip = AudioSystem.getClip();
				clip.open(audioIn);
				return clip;
			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}
	
}
