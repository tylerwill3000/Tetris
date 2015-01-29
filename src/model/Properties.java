package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

// Provides an interface to the game's properties
public class Properties {
	
	public static final String PROPERTIES_FILE_PATH = System.getProperty("user.home") + "/.tetrisconfig"; 
	public static final java.util.Properties GAME_PROPERTIES = new java.util.Properties();
	
	// Load game properties
	static {
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
	
	private static void initDefaultPropertiesFile(File propsFile) throws IOException {
		propsFile.createNewFile();
		GAME_PROPERTIES.setProperty("db.host", "your database host");
		GAME_PROPERTIES.setProperty("db.name", "your database name");
		GAME_PROPERTIES.setProperty("db.user", "your database user");
		GAME_PROPERTIES.setProperty("db.pass", "your database pass");
		GAME_PROPERTIES.store(new FileOutputStream(propsFile), "Tetris Settings");
	}

	public static void saveCurrentProperties() throws IOException {
		GAME_PROPERTIES.store(new FileOutputStream(PROPERTIES_FILE_PATH), "Tetris Settings");
	}
	
}
