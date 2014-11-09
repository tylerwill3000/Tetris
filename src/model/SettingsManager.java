package model;

// Provides an interface to the game's current settings. This is basically
// a proxy class for other files to read the current checkbox settings, instead
// of allowing them to interact with the JCheckBox elements directly
public class SettingsManager {
	
	private static boolean usingGhostSquares = true;
	private static boolean playingMusic = true;
	
	public static void toggleGhostSquares() { usingGhostSquares = !usingGhostSquares; }
	public static void toggleMusic() { playingMusic = !playingMusic; }
	
	public static boolean isUsingGhostSquares() { return usingGhostSquares; }
	public static boolean isPlayingMusic() { return playingMusic; }
	
}
