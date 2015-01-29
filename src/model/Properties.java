package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.PieceFactory.PieceType;

// Provides an interface to the game's properties
public class Properties {
	
	private static final String PROPERTIES_FILE_PATH = System.getProperty("user.home") + "/.tetrisconfig"; 
	public static final java.util.Properties GAME_PROPERTIES = new java.util.Properties();
	static { loadPropertiesFromDisk(); }
	
	public static String getDBHostProperty() {
		return GAME_PROPERTIES.getProperty("db.host");
	}
	
	public static void setDBHostProperty(String host) {
		GAME_PROPERTIES.setProperty("db.host", host);
	}
	
	public static String getDBNameProperty() {
		return GAME_PROPERTIES.getProperty("db.name");
	}
	
	public static void setDBNameProperty(String name) {
		GAME_PROPERTIES.setProperty("db.name", name);
	}
	
	public static String getDBUserProperty() {
		return GAME_PROPERTIES.getProperty("db.user");
	}
	
	public static void setDBUserProperty(String user) {
		GAME_PROPERTIES.setProperty("db.user", user);
	}
	
	public static String getDBPassProperty() {
		return GAME_PROPERTIES.getProperty("db.pass");
	}
	
	public static void setDBPassProperty(String pass) {
		GAME_PROPERTIES.setProperty("db.pass", pass);
	}
	
	public static String getPlayerSaveName() {
		return GAME_PROPERTIES.getProperty("player.save.name");
	}
	
	public static void setPlayerSaveName(String name) {
		GAME_PROPERTIES.setProperty("player.save.name", name);
	}
	
	public static int getPieceBorderProperty() {
		return Integer.parseInt(GAME_PROPERTIES.getProperty("piece.border.style"));
	}
	
	public static void setPieceBorderProperty(int pieceBorder) {
		GAME_PROPERTIES.setProperty("piece.border.style", String.valueOf(pieceBorder));
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
		return Integer.parseInt(GAME_PROPERTIES.getProperty("highscores.record.count"));
	}
	
	public static void setHighScoreRecordCount(int count) {
		GAME_PROPERTIES.setProperty("highscores.record.count", String.valueOf(count));
	}
	
	public static int getHighScoreDifficulty() {
		return Integer.parseInt(GAME_PROPERTIES.getProperty("highscores.difficulty"));
	}
	
	public static void setHighScoresDifficulty(int diff) {
		GAME_PROPERTIES.setProperty("highscores.difficulty", String.valueOf(diff));
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
		setPlayerSaveName("player1");
		setPieceBorderProperty(0);
		
		for (PieceType piece : PieceFactory.PieceType.getSpecialPieces()) {
			setActivePieceProperty(piece, false);
		}
		
		setHighScoreRecordCount(0);
		setHighScoresDifficulty(0);
		
		saveCurrentProperties(true);
	}
	
	/**
	 * Persists the current properties stored in the game properties object to the
	 * properties file.
	 * @return Whether or not the save was successful
	 */
	public static boolean saveCurrentProperties() {
		return saveCurrentProperties(false);
	}
	
	/**
	 * Persists the current properties stored in the game properties object to the
	 * properties file.
	 * @param quietMode Specifies whether to hide save alerts
	 * @return Whether or not the save was successful
	 */
	public static boolean saveCurrentProperties(boolean quietMode) {
		try {
			GAME_PROPERTIES.store(new FileOutputStream(PROPERTIES_FILE_PATH), "Tetris Settings");
			if (!quietMode) JOptionPane.showMessageDialog(null, "Settings saved.");
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error writing to settings file: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Reloads the properties object with the properties stored on disk
	 */
	public static void loadPropertiesFromDisk() {
		GAME_PROPERTIES.clear();
		try {
			File propsFile = new File(PROPERTIES_FILE_PATH);
			if (!propsFile.exists()) {
				initDefaultPropertiesFile(propsFile);
			}
			else {
				GAME_PROPERTIES.load(new FileInputStream(propsFile));
			}
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while loading properties file: " + e.getMessage());
		}
	}
	
}
