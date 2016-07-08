package com.tyler.tetris.model;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Provides an interface to the game's audio
 * @author Tyler
 */
public final class AudioManager {
	
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
	
	public AudioManager() {}
	
	public void startSoundtrack(int level) {
		
		// In case a new game is started before the victory jingle is finished
		// from a previous game (rare occurrence, but possible)
		if (VICTORY_FANFARE != null && VICTORY_FANFARE.isRunning()) {
			VICTORY_FANFARE.stop();			
		}
		
		if (SOUNDTRACK[level] != null) {
			SOUNDTRACK[level].setFramePosition(0);
			SOUNDTRACK[level].loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void resumeSoundtrack(int level) {
		if (SOUNDTRACK[level] != null) {
			SOUNDTRACK[level].loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void stopSoundtrack(int level) {
		if (SOUNDTRACK[level] != null) {
			SOUNDTRACK[level].stop();
		}
	}
	
	/**
	 *  Use this for playing small effect sounds. This resets the clip back to the starting
	 *  frame position after playing.
	 */
	private void playEffect(Clip effect) {
		if (effect != null) {
			effect.start();
			effect.setFramePosition(0);
		}
	}
	
	public void playGameOverSound() {
		playEffect(GAME_OVER);
	}
	
	public void playVictoryFanfare() {
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
	
	/**
	 * Plays the audio used when lines are cleared. If lines cleared is equal to 4, a special sound is played
	 */
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
	
	/**
	 *  Iterates over all clips and resets their frame positions back to the start.
	 *  This is called to prepare soundtracks for the next game.
	 */
	public void resetFramePositions() {
		for (Clip c : SOUNDTRACK) {
			if (c != null) {
				c.setFramePosition(0);
			}
		}
	}
	
	/**
	 *  Returns a clip audio output device input line from the specified file string
	 * @param file Pathname to the audio file to receive a Clip dataline for
	 * @return A clip object with the audio file as its source input stream
	 */
	private static Clip getAudioClip(String file) {
		
		// Attempt to initialize clip input object. If there are no supported lines null is returned for the clip
		Clip c;
		
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER) || AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
		
			try {
				
				c = AudioSystem.getClip();
				
				// Add a new audio stream to the clip data line. Since I'm
				// using a clip object, all data is loaded into memory at
				// once as opposed to being read into a buffer and streamed
				c.open(AudioSystem.getAudioInputStream(AudioManager.class.getResource(file)));
				return c;
				
			}
			catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
				// Will have no audio
			}
			
		}
		
		return null;
	}
	
}
