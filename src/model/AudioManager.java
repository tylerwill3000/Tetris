package model;

import java.applet.Applet;
import java.applet.AudioClip;

// Singleton class to interface to the game's audio
public class AudioManager {
	
	// Provides a handle to the resource directory (aka class directory)
	private static final Class resources = new AudioManager().getClass();

	// Soundtrack for the game. Current level is used to index into
	// the array, so the first element (0) is null.
	private static final AudioClip[] soundtrack = {
		null,
		Applet.newAudioClip(resources.getResource("audio/metroid-kraid.wav")),
		Applet.newAudioClip(resources.getResource("audio/metroid-kraid.wav")),
		Applet.newAudioClip(resources.getResource("audio/metroid-kraid.wav")),
	};
	
	// All game sounds
	private static final AudioClip gameOver = Applet.newAudioClip(resources.getResource("audio/zelda-game-over.wav"));
	private static final AudioClip placePiece = Applet.newAudioClip(resources.getResource("audio/pipe.wav"));
	private static final AudioClip clearLine = Applet.newAudioClip(resources.getResource("audio/laser.wav"));
	private static final AudioClip swipeUp = Applet.newAudioClip(resources.getResource("audio/swish-up.wav"));
	private static final AudioClip swipeDown = Applet.newAudioClip(resources.getResource("audio/swish-down.wav"));
	
	private AudioManager() {}
	
	public static void playCurrentSoundtrack() { soundtrack[GameBoardModel.getLevel()].loop(); }
	public static void stopCurrentSoundtrack() { soundtrack[GameBoardModel.getLevel()].stop(); }
	
	public static void playGameOverSound() { gameOver.play(); }
	public static void playPiecePlacementSound() { placePiece.play(); }
	public static void playClearLineSound() { clearLine.play(); }
	public static void playCWRotationSound() { swipeUp.play(); }
	public static void playCCWRotationSound() { swipeDown.play(); }

}
