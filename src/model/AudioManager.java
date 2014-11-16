package model;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import ui.UIBox;


// Singleton class to interface to the game's audio
public class AudioManager {
	
	// Soundtrack for the game
	private static final Clip[] soundtrack = {
		getAudioClip("C:/Users/Tyler/audio/soundtrack/tetris-theme.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/bean-machine-1-4.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/tetris-music-3.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/metroid-kraid.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/sonic-scrap-brain-zone.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/chrono-trigger-bike-theme.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/mega-man-dr-wily.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/sonic-ice-cap-zone.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/bean-machine-9-12.wav"),
		getAudioClip("C:/Users/Tyler/audio/soundtrack/chrono-trigger-final-battle.wav")
	};

	private static final Clip gameOver = getAudioClip("C:/Users/Tyler/audio/effects/zelda-game-over.wav");
	private static final Clip pause = getAudioClip("C:/Users/Tyler/audio/effects/mario-64-pause.wav");
	private static final Clip placePiece = getAudioClip("C:/Users/Tyler/audio/effects/pipe.wav");
	private static final Clip clearLine = getAudioClip("C:/Users/Tyler/audio/effects/laser.wav");
	private static final Clip ultraLine = getAudioClip("C:/Users/Tyler/audio/effects/explosion.wav");	
	private static final Clip swipeUp = getAudioClip("C:/Users/Tyler/audio/effects/swish-up.wav");
	private static final Clip swipeDown = getAudioClip("C:/Users/Tyler/audio/effects/swish-down.wav");
	
	private AudioManager() {}
	
	// Used when you want to start the soundtrack from the beginning
	public static void beginCurrentSoundtrack() {
		if (UIBox.settingsPanel.musicOn()) {
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
	public static void playClearLineSound() { playEffect(clearLine); }
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
			c.open(AudioSystem.getAudioInputStream(new File(file)));
			return c;
			
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	
		return null;
		
	}
	
}
