package tetris;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Provides an interface to the game's audio
 * @author Tyler
 */
public final class TetrisAudioSystem {
	
	// Soundtrack for the game
	private static final Clip[] SOUNDTRACK = {
		getAudioClip("/audio/soundtrack/tetris-theme.wav"),
		getAudioClip("/audio/soundtrack/bean-machine-1-4.wav"),
		getAudioClip("/audio/soundtrack/tetris-music-3.wav"),
		getAudioClip("/audio/soundtrack/metroid-kraid.wav"),
		getAudioClip("/audio/soundtrack/sonic-scrap-brain-zone.wav"),
		getAudioClip("/audio/soundtrack/chrono-trigger-bike-theme.wav"),
		getAudioClip("/audio/soundtrack/mega-man-dr-wily.wav"),
		getAudioClip("/audio/soundtrack/sonic-ice-cap-zone.wav"),
		getAudioClip("/audio/soundtrack/bean-machine-9-12.wav"),
		getAudioClip("/audio/soundtrack/chrono-trigger-final-battle.wav")
	};
	
	// Non-looping soundtrack clips
	private static final Clip GAME_OVER = getAudioClip("/audio/soundtrack/zelda-game-over.wav");
	private static final Clip VICTORY_FANFARE = getAudioClip("/audio/soundtrack/ff1-victory-fanfare.wav");
	
	// Effects
	private static final Clip PAUSE = getAudioClip("/audio/effects/mario-64-pause.wav");
	private static final Clip PLACE_PIECE = getAudioClip("/audio/effects/pipe.wav");
	private static final Clip CLEAR_LINE = getAudioClip("/audio/effects/laser.wav");
	private static final Clip ULTRA_LINE = getAudioClip("/audio/effects/explosion.wav");	
	private static final Clip SWIPE_UP = getAudioClip("/audio/effects/swish-up.wav");
	private static final Clip SWIPE_DOWN = getAudioClip("/audio/effects/swish-down.wav");
	private static final Clip SUPERSLIDE = getAudioClip("/audio/effects/superslide.wav");
	private static final Clip HOLD = getAudioClip("/audio/effects/clang.wav");
	private static final Clip RELEASE = getAudioClip("/audio/effects/water-drop.wav");
	
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
		
		// In case a new game is started before the victory jingle is finished from a previous game (rare occurrence, but possible)
		if (VICTORY_FANFARE.isRunning()) {
			VICTORY_FANFARE.stop();
		}

		killActiveSoundtracks();
		getSoundtrack(level).setFramePosition(0);
		resumeSoundtrack(level);
	}
	
	public void resumeSoundtrack(int level) {
		if (!soundtrackMuted) {
			getSoundtrack(level).loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void stopSoundtrack(int level) {
		getSoundtrack(level).stop();
	}
	
	public void killActiveSoundtracks() {
		for (Clip c : SOUNDTRACK) {
			if (c.isActive()) {
				c.stop();
			}
		}
	}
	
	/**
	 *  Iterates over all clips and resets their frame positions back to the start.
	 *  This is called to prepare soundtracks for the next game.
	 */
	public void resetClips() {
		for (Clip c : SOUNDTRACK) {
			c.setFramePosition(0);
		}
	}
	
	public void playGameOverSound() {
		killActiveSoundtracks();
		playEffect(GAME_OVER);
	}

	public void stopGameOverSound() {
		stopEffect(GAME_OVER);
	}
	
	public void playVictoryFanfare() {
		killActiveSoundtracks();
		playEffect(VICTORY_FANFARE);
	}
	
	public void playPauseSound() {
		playEffect(PAUSE);
	}
	
	public void playPiecePlacementSound() {
		playEffect(PLACE_PIECE);
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
	
	public void playCWRotationSound() {
		playEffect(SWIPE_UP);
	}
	
	public void playCCWRotationSound() {
		playEffect(SWIPE_DOWN);
	}
	
	public void playSuperslideSound() {
		playEffect(SUPERSLIDE);
	}
	
	private void playEffect(Clip effect) {
		if (!effectsMuted) {
			effect.start();
			effect.setFramePosition(0);
		}
	}
	
	private void stopEffect(Clip effect) {
		if (effect.isActive()) {
			effect.stop();
			effect.setFramePosition(0);
		}
	}
	
	private Clip getSoundtrack(int level) {
		return SOUNDTRACK[level - 1];
	}
	
	/**
	 *  Returns a clip audio output device input line from the specified file string
	 * @param file Pathname to the audio file to receive a Clip dataline for
	 * @return A clip object with the audio file as its source input stream
	 */
	private static Clip getAudioClip(String file) {
		
		Clip c = null;
		
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER) || AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
			try {
				URL audioFile = TetrisAudioSystem.class.getResource(file);
				if (audioFile != null) {
					c = AudioSystem.getClip();
					c.open(AudioSystem.getAudioInputStream(audioFile));
				}
				else {
					System.out.println("Audio file not found for path " + file);
				}
			}
			catch (LineUnavailableException | IOException | UnsupportedAudioFileException ignore) {
				// Will have no audio
			}
		}
		
		return c;
	}
	
}
