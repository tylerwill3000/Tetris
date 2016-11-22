package tetris;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

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
	
	// Non-looping clips
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
		if (!soundtrackMuted) {
			stopRunningSoundtracks();
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
	
	/**
	 * Stops any currently running soundtracks including the looping level soundtracks and end of game soundtracks 
	 */
	private void stopRunningSoundtracks() {
		stopEffect(VICTORY_FANFARE);
		stopEffect(GAME_OVER);
		Arrays.stream(SOUNDTRACK)
		      .filter(clip -> clip != null)
		      .filter(Clip::isActive)
		      .forEach(Clip::stop);
	}
	
	public void playGameOverSound() {
		stopRunningSoundtracks();
		play(GAME_OVER);
	}

	public void playVictoryFanfare() {
		stopRunningSoundtracks();
		play(VICTORY_FANFARE);
	}
	
	public void playPauseSound() {
		play(PAUSE);
	}
	
	public void playPiecePlacementSound() {
		play(PLACE_PIECE);
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
	
	private void play(Clip effect) {
		if (effect != null && !effectsMuted) {
			effect.start();
			effect.setFramePosition(0);
		}
	}
	
	private void stopEffect(Clip effect) {
		if (effect != null && effect.isActive()) {
			effect.stop();
			effect.setFramePosition(0);
		}
	}
	
	private Clip getSoundtrack(int level) {
		return SOUNDTRACK[level - 1];
	}
	
	/**
	 * Returns a clip audio output device input line from the specified file string
	 */
	private static Clip getAudioClip(String file) {
		
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER) || AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
			try {
				URL audioFile = TetrisAudioSystem.class.getResource(file);
				if (audioFile == null) {
					throw new ExceptionInInitializerError("Audio file not found for path " + file);
				}
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(audioFile));
				return clip;
			}
			catch (LineUnavailableException | IOException | UnsupportedAudioFileException ignore) {
				// Will have no audio
			}
		}
		
		return null;
	}
	
}
