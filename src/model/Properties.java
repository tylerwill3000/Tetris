package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.PieceFactory.PieceType;

/**
 *  Provides an interface to the game's properties
 * @author Tyler
 */
public final class Properties {
	
	private static final String PROPERTIES_FILE_PATH = System.getProperty("user.home") + "/.tetrisconfig"; 
	private static final java.util.Properties GAME_PROPERTIES = new java.util.Properties();
	static {
		try {
			loadPropertiesFromDisk();
		} catch (IOException e) {
			// I'd rather not add in GUI code in a model class, but I don't really have a choice
			// here since I need to statically load this
			JOptionPane.showMessageDialog(null, "Properties could not be loaded from disk: " + e);
		}
	}
	
	// Centralize management of all property keys
	private final static String DB_HOST_KEY = "db.host";
	private final static String DB_NAME_KEY = "db.name";
	private final static String DB_USER_KEY = "db.user";
	private final static String DB_PASS_KEY = "db.pass";
	private final static String GHOST_SQUARES_KEY = "ghost.squares";
	private final static String MUSIC_KEY = "music.on";
	private final static String SOUNDS_EFFECTS_KEY = "sound.effects.on";
	private final static String SAVE_SCORES_KEY = "save.scores";
	private final static String TIME_ATTACK_KEY = "time.attack";
	private final static String DIFFICULTY_KEY = "difficulty";
	private final static String PLAYER_SAVE_NAME_KEY = "player.save.name";
	private final static String PIECE_BORDER_STYLE_KEY = "piece.border.style";
	private final static String HIGH_SCORES_RECORD_KEY = "highscores.record.count";
	private final static String HIGH_SCORES_DIFFICULTY_KEY = "highscores.difficulty";
	
	public static String getDBHostProperty() {
		return GAME_PROPERTIES.getProperty(DB_HOST_KEY);
	}
	
	public static void setDBHostProperty(String host) {
		GAME_PROPERTIES.setProperty(DB_HOST_KEY, host);
	}
	
	public static String getDBNameProperty() {
		return GAME_PROPERTIES.getProperty(DB_NAME_KEY);
	}
	
	public static void setDBNameProperty(String name) {
		GAME_PROPERTIES.setProperty(DB_NAME_KEY, name);
	}
	
	public static String getDBUserProperty() {
		return GAME_PROPERTIES.getProperty(DB_USER_KEY);
	}
	
	public static void setDBUserProperty(String user) {
		GAME_PROPERTIES.setProperty(DB_USER_KEY, user);
	}
	
	public static String getDBPassProperty() {
		return GAME_PROPERTIES.getProperty(DB_PASS_KEY);
	}
	
	public static void setDBPassProperty(String pass) {
		GAME_PROPERTIES.setProperty(DB_PASS_KEY, pass);
	}
	
	private static boolean getCbxProperty(String key) {
		return GAME_PROPERTIES.getProperty(key).equals("true");
	}
	
	private static void setCbxProperty(String key, boolean on) {
		GAME_PROPERTIES.setProperty(key, String.valueOf(on));
	}
	
	public static boolean getGhostSquaresProperty() {
		return getCbxProperty(GHOST_SQUARES_KEY);
	}
	
	public static void setGhostSquaresProperty(boolean on) {
		setCbxProperty(GHOST_SQUARES_KEY, on);
	}
	
	public static boolean getMusicProperty() {
		return getCbxProperty(MUSIC_KEY);
	}
	
	public static void setMusicProperty(boolean on) {
		setCbxProperty(MUSIC_KEY, on);
	}
	
	public static boolean getSoundEffectsProperty() {
		return getCbxProperty(SOUNDS_EFFECTS_KEY);
	}
	
	public static void setSoundEffectsProperty(boolean on) {
		setCbxProperty(SOUNDS_EFFECTS_KEY, on);
	}
	
	public static boolean getSaveScoresProperty() {
		return getCbxProperty(SAVE_SCORES_KEY);
	}
	
	public static void setSaveScoresProperty(boolean on) {
		setCbxProperty(SAVE_SCORES_KEY, on);
	}
	
	public static boolean getTimeAttackProperty() {
		return getCbxProperty(TIME_ATTACK_KEY);
	}
	
	public static void setTimeAttackProperty(boolean on) {
		setCbxProperty(TIME_ATTACK_KEY, on);
	}
	
	public static int getDifficultyProperty() {
		return Integer.parseInt(GAME_PROPERTIES.getProperty(DIFFICULTY_KEY));
	}
	
	public static void setDifficultyProperty(int diff) {
		GAME_PROPERTIES.setProperty(DIFFICULTY_KEY, String.valueOf(diff));
	}
	
	public static String getPlayerSaveName() {
		return GAME_PROPERTIES.getProperty(PLAYER_SAVE_NAME_KEY);
	}
	
	public static void setPlayerSaveName(String name) {
		GAME_PROPERTIES.setProperty(PLAYER_SAVE_NAME_KEY, name);
	}
	
	public static int getPieceBorderProperty() {
		return Integer.parseInt(GAME_PROPERTIES.getProperty(PIECE_BORDER_STYLE_KEY));
	}
	
	public static void setPieceBorderProperty(int pieceBorder) {
		GAME_PROPERTIES.setProperty(PIECE_BORDER_STYLE_KEY, String.valueOf(pieceBorder));
	}
	
	public static boolean getActivePieceProperty(PieceType type) {
		String propertyKey = getPieceTypePropertyKey(type);
		return GAME_PROPERTIES.getProperty(propertyKey).equals("true");
	}
	
	public static void setActivePieceProperty(PieceType type, boolean active) {
		String propertyKey = getPieceTypePropertyKey(type);
		GAME_PROPERTIES.setProperty(propertyKey, String.valueOf(active));
	}
	
	public static int getHighScoreRecordCount() {
		return Integer.parseInt(GAME_PROPERTIES.getProperty(HIGH_SCORES_RECORD_KEY));
	}
	
	public static void setHighScoreRecordCount(int count) {
		GAME_PROPERTIES.setProperty(HIGH_SCORES_RECORD_KEY, String.valueOf(count));
	}
	
	public static int getHighScoreDifficulty() {
		return Integer.parseInt(GAME_PROPERTIES.getProperty(HIGH_SCORES_DIFFICULTY_KEY));
	}
	
	public static void setHighScoresDifficulty(int diff) {
		GAME_PROPERTIES.setProperty(HIGH_SCORES_DIFFICULTY_KEY, String.valueOf(diff));
	}
	
	/**
	 * Returns the property key for a piece type
	 * @param type The type of piece
	 * @return The property key in the properties file
	 */
	private static String getPieceTypePropertyKey(PieceType type) {
		return "special.piece." + type.name().toLowerCase();
	}
	
	public static List<PieceType> getSavedSpecialPieces() {
		
		List<PieceType> specials = new ArrayList<>();
		
		for (PieceType piece : PieceType.getSpecialPieces()) {
			if (getActivePieceProperty(piece)) specials.add(piece);
		}
		
		return specials;
		
	}
	
	private static void initDefaultPropertiesFile(File propsFile) throws IOException {
		
		propsFile.createNewFile();
		
		setDBHostProperty("your database host");
		setDBNameProperty("your database name");
		setDBUserProperty("your database user");
		setDBPassProperty("");
		setGhostSquaresProperty(true);
		setMusicProperty(true);
		setSoundEffectsProperty(true);
		setSaveScoresProperty(true);
		setTimeAttackProperty(false);
		setDifficultyProperty(1);
		setPlayerSaveName("player1");
		setPieceBorderProperty(0);
		
		for (PieceType piece : PieceType.getSpecialPieces()) {
			setActivePieceProperty(piece, false);
		}
		
		setHighScoreRecordCount(0);
		setHighScoresDifficulty(0);
		
		saveCurrentProperties();
	}
	
	/*
	 * Persists the current properties stored in the game properties object to the
	 * properties file.
	 */
	public static void saveCurrentProperties() throws IOException {
		GAME_PROPERTIES.store(new FileOutputStream(PROPERTIES_FILE_PATH), "Tetris Settings");
	}
	
	/**
	 * Reloads the properties object with the properties stored on disk
	 * @throws IOException 
	 */
	public static void loadPropertiesFromDisk() throws IOException {
		GAME_PROPERTIES.clear();
		File propsFile = new File(PROPERTIES_FILE_PATH);
		if (!propsFile.exists()) {
			initDefaultPropertiesFile(propsFile);
		} else {
			GAME_PROPERTIES.load(new FileInputStream(propsFile));
		}
	}
	
}
