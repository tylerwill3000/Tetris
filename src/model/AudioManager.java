package model;

import javax.sound.sampled.*;

import java.applet.Applet;
import java.applet.AudioClip;

// Singleton class to interface to the game's audio
public class AudioManager {
	
	// Provides a handle to the resource directory (aka class directory)
	@SuppressWarnings("rawtypes")
	private static final Class resources = new AudioManager().getClass();
	
	// Soundtrack for the game. Current level is used to index into
	// the array, so the first element (0) is null.
	private static final AudioClip[] soundtrack = {
		Applet.newAudioClip(resources.getResource("audio/tetris-theme.wav")),
		Applet.newAudioClip(resources.getResource("audio/bean-machine-1-4.wav")),
		Applet.newAudioClip(resources.getResource("audio/tetris-music-3.wav")),
		Applet.newAudioClip(resources.getResource("audio/metroid-kraid.wav")),
		Applet.newAudioClip(resources.getResource("audio/sonic-scrap-brain-zone.wav")),
		Applet.newAudioClip(resources.getResource("audio/chrono-trigger-bike-theme.wav")),
		Applet.newAudioClip(resources.getResource("audio/mega-man-dr-wily.wav")),
		Applet.newAudioClip(resources.getResource("audio/sonic-ice-cap-zone.wav")),
		Applet.newAudioClip(resources.getResource("audio/bean-machine-9-12.wav")),
		Applet.newAudioClip(resources.getResource("audio/chrono-trigger-final-battle.wav"))
	};
	
	// Sound effects
	private static final AudioClip gameOver = Applet.newAudioClip(resources.getResource("audio/zelda-game-over.wav"));
	private static final AudioClip placePiece = Applet.newAudioClip(resources.getResource("audio/effects/pipe.wav"));
	private static final AudioClip clearLine = Applet.newAudioClip(resources.getResource("audio/effects/laser.wav"));
	private static final AudioClip ultraLine = Applet.newAudioClip(resources.getResource("audio/effects/explosion.wav"));	
	private static final AudioClip swipeUp = Applet.newAudioClip(resources.getResource("audio/effects/swish-up.wav"));
	private static final AudioClip swipeDown = Applet.newAudioClip(resources.getResource("audio/effects/swish-down.wav"));
	
	private AudioManager() {}
	
	public static void playCurrentSoundtrack() {
		if (!SettingsManager.isPlayingMusic()) return;
		soundtrack[GameBoardModel.getLevel()-1].loop();
	}
	
	public static void stopCurrentSoundtrack() { soundtrack[GameBoardModel.getLevel()-1].stop(); }
	
	public static void playGameOverSound() { gameOver.play(); }
	public static void playPiecePlacementSound() { placePiece.play(); }
	public static void playClearLineSound() { clearLine.play(); }
	public static void playUltraLineSound() { ultraLine.play(); }
	public static void playCWRotationSound() { swipeUp.play(); }
	public static void playCCWRotationSound() { swipeDown.play(); }
	
}
