package model;

import java.io.IOException;

import javax.sound.sampled.*;

import ui.GameFrame;

/**
 * Provides an interface to the game's audio
 * @author Tyler
 *
 */
public class AudioManager {
	
	// Provides a handle to the class directory of the AudioManger.class file.
	// Used to obtain audio file resources
	private static final Class<?> RESOURCES = new AudioManager().getClass();
	
	// Soundtrack for the game
	private static final Clip[] SOUNDTRACK = {
		getAudioClip("audio/soundtrack/tetris-theme.wav"),
		getAudioClip("audio/soundtrack/bean-machine-1-4.wav"),
		getAudioClip("audio/soundtrack/tetris-music-3.wav"),
		getAudioClip("audio/soundtrack/metroid-kraid.wav"),
		getAudioClip("audio/soundtrack/sonic-scrap-brain-zone.wav"),
		getAudioClip("audio/soundtrack/chrono-trigger-bike-theme.wav"),
		getAudioClip("audio/soundtrack/mega-man-dr-wily.wav"),
		getAudioClip("audio/soundtrack/sonic-ice-cap-zone.wav"),
		getAudioClip("audio/soundtrack/bean-machine-9-12.wav"),
		getAudioClip("audio/soundtrack/chrono-trigger-final-battle.wav")
	};
	
	// Non-looping soundtrack clips
	private static final Clip GAME_OVER = getAudioClip("audio/soundtrack/zelda-game-over.wav");
	private static final Clip VICTORY_FANFARE = getAudioClip("audio/soundtrack/ff1-victory-fanfare.wav");
	
	// Effects
	private static final Clip PAUSE = getAudioClip("audio/effects/mario-64-pause.wav");
	private static final Clip PLACE_PIECE = getAudioClip("audio/effects/pipe.wav");
	private static final Clip CLEAR_LINE = getAudioClip("audio/effects/laser.wav");
	private static final Clip ULTRA_LINE = getAudioClip("audio/effects/explosion.wav");	
	private static final Clip SWIPE_UP = getAudioClip("audio/effects/swish-up.wav");
	private static final Clip SWIPE_DOWN = getAudioClip("audio/effects/swish-down.wav");
	private static final Clip SUPERSLIDE = getAudioClip("audio/effects/superslide.wav");
	private static final Clip HOLD = getAudioClip("audio/effects/clang.wav");
	private static final Clip RELEASE = getAudioClip("audio/effects/water-drop.wav");
	
	private AudioManager() {}
	
	/**
	 * Starts the current level's soundtrack from the beginning. This will have no effect
	 * if music is turned off.
	 */
	public static void beginCurrentSoundtrack() {
		
		if (GameFrame._settingsPanel.musicOn() && SOUNDTRACK[GameBoardModel.getLevel()-1] != null) {
			
			// In case a new game is started before the victory jingle is finished
			// from a previous game (rare occurrence, but possible)
			if (VICTORY_FANFARE.isRunning()) VICTORY_FANFARE.stop();			
			
			SOUNDTRACK[GameBoardModel.getLevel()-1].setFramePosition(0);
			SOUNDTRACK[GameBoardModel.getLevel()-1].loop(Clip.LOOP_CONTINUOUSLY);
			
		}
		
	}
	
	/**
	 * Resumes the current level's soundtrack from where it left off. This will have no effect
	 * if music is turned off.
	 */
	public static void resumeCurrentSoundtrack() {
		if (GameFrame._settingsPanel.musicOn() && SOUNDTRACK[GameBoardModel.getLevel()-1] != null)
			SOUNDTRACK[GameBoardModel.getLevel()-1].loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	/**
	 * Stops the current level's soundtrack.
	 */
	public static void stopCurrentSoundtrack() {
		if (SOUNDTRACK[GameBoardModel.getLevel()-1] != null)
			SOUNDTRACK[GameBoardModel.getLevel()-1].stop();
	}
	
	public static void playGameOverSound() {
		if (GameFrame._settingsPanel.musicOn() && GAME_OVER != null) {
			GAME_OVER.start();
			GAME_OVER.setFramePosition(0);
		}
	}
	
	public static void playVictoryFanfare() {
		if (GameFrame._settingsPanel.musicOn() && VICTORY_FANFARE != null) {
			VICTORY_FANFARE.start();
			VICTORY_FANFARE.setFramePosition(0);
		}
	}
	
	/**
	 *  Use this for playing small effect sounds. This resets the clip back to the starting
	 *  frame position after playing.
	 * @param effect The effect to play
	 */
	private static void playEffect(Clip effect) {

		if (GameFrame._settingsPanel.effectsOn() && effect != null) {
			effect.start();
			effect.setFramePosition(0);
		}

	}
	
	public static void playPauseSound() { playEffect(PAUSE); }
	public static void playPiecePlacementSound() { playEffect(PLACE_PIECE); }
	public static void playHoldSound() { playEffect(HOLD); }
	public static void playReleaseSound() { playEffect(RELEASE); }
	
	/**
	 * Plays the audio used when lines are cleared
	 * @param lineCount Number of lines cleared. If equal to 4, a special sound is played
	 */
	public static void playClearLineSound(int lineCount) {
		if (lineCount == 4)
			playEffect(ULTRA_LINE);
		else
			playEffect(CLEAR_LINE);
	}
	
	public static void playCWRotationSound() { playEffect(SWIPE_UP); }
	public static void playCCWRotationSound() { playEffect(SWIPE_DOWN); }
	public static void playSuperslideSound() { playEffect(SUPERSLIDE); }
	
	/**
	 *  Iterates over all clips and resets their frame positions back to the start.
	 *  This is called to prepare soundtracks for the next game.
	 */
	public static void resetSoundtrackFramePositions() {
		for (Clip c : SOUNDTRACK)
			if (c != null) c.setFramePosition(0);
	}
	
	/**
	 *  Returns a clip audio output device input line from the specified file string
	 * @param file Pathname to the audio file to receive a Clip dataline for
	 * @return A clip object with the audio file as its source input stream
	 */
	private static Clip getAudioClip(String file) {
		
		// Attempt to initialize clip input object. If there are no supported lines,
		// null is returned for the clip
		Clip c;
		
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER) || AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
		
			try {
				
				c = AudioSystem.getClip();
				
				// Add a new audio stream to the clip data line. Since I'm
				// using a clip object, all data is loaded into memory at
				// once as opposed to being read into a buffer and streamed
				c.open(AudioSystem.getAudioInputStream(RESOURCES.getResource(file)));
				return c;
				
			}
			catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {} // Munch
			
		}
		
		return null;
		
	}
	
}
