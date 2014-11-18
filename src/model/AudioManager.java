package model;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import ui.UIBox;


// Singleton class to interface to the game's audio
public class AudioManager {
	
	// Provides a handle to the class directory of the AudioManger.class file.
	// Used to obtain audio file resources
	private static final Class resources = new AudioManager().getClass();
	
	// Soundtrack for the game
	private static final Clip[] soundtrack = {
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

	private static final Clip gameOver = getAudioClip("audio/effects/zelda-game-over.wav");
	private static final Clip pause = getAudioClip("audio/effects/mario-64-pause.wav");
	private static final Clip placePiece = getAudioClip("audio/effects/pipe.wav");
	private static final Clip clearLine = getAudioClip("audio/effects/laser.wav");
	private static final Clip ultraLine = getAudioClip("audio/effects/explosion.wav");	
	private static final Clip swipeUp = getAudioClip("audio/effects/swish-up.wav");
	private static final Clip swipeDown = getAudioClip("audio/effects/swish-down.wav");
	
	private AudioManager() {}
	
	// Used when you want to start the soundtrack from the beginning. Loops through all
	// existing clips and closes any that are on
	public static void beginCurrentSoundtrack() {
		
		if (UIBox.settingsPanel.musicOn()) {
			
			for (Clip c : soundtrack)
				if (c.isRunning()) c.stop();
			
			soundtrack[GameBoardModel.getLevel()-1].setFramePosition(0);
			soundtrack[GameBoardModel.getLevel()-1].loop(Clip.LOOP_CONTINUOUSLY);
		}
		
	}
	
	// Used when you want to resume playing the current soundtrack from where you left off
	public static void resumeCurrentSoundtrack() {
		if (UIBox.settingsPanel.musicOn())
			soundtrack[GameBoardModel.getLevel()-1].loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	// Used for both stopping and pausing
	public static void stopCurrentSoundtrack() {
		soundtrack[GameBoardModel.getLevel()-1].stop();
	}
	
	public static void playGameOverSound() {
		if (UIBox.settingsPanel.musicOn()) {
			gameOver.start();
			gameOver.setFramePosition(0);
		}
	}
	
	public static void playPauseSound() { playEffect(pause); }
	public static void playPiecePlacementSound() { playEffect(placePiece); }
	
	// Clear line sound is dependent on number of lines cleared
	public static void playClearLineSound(int lineCount) {
		if (lineCount == 4)
			playEffect(ultraLine);
		else
			playEffect(clearLine);
	}
	
	public static void playUltraLineSound() { playEffect(ultraLine); }
	public static void playCWRotationSound() { playEffect(swipeUp); }
	public static void playCCWRotationSound() { playEffect(swipeDown); }
	
	// For playing small effect sounds. Resets the clip back to the starting
	// frame position after playing
	private static void playEffect(Clip effect) {
		
		if (UIBox.settingsPanel.effectsOn()) {
			effect.start();
			effect.setFramePosition(0);
			
		}
	}
	
	// Returns a clip audio output device input line from the specified file string
	private static Clip getAudioClip(String file) {
		
		// Attempt to initialize clip input object
		Clip c;
		
		try {
			
			c = AudioSystem.getClip();
			
			// Add a new audio stream to the clip data line. Since I'm
			// using a clip object, all data is loaded into memory at
			// once as opposed to being read into a buffer and streamed
			c.open(AudioSystem.getAudioInputStream(resources.getResource(file)));
			return c;
			
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	
		return null;
		
	}
	
}
