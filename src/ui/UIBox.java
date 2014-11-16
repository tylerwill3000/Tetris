package ui;

import javax.swing.JCheckBox;

// Package-private box that I stick all the UI components in so they can
// "talk" to each other
public class UIBox {
	
	static GameBoardPanel gameBoardPanel = new GameBoardPanel();
	static NextPiecePanel nextPiecePanel = new NextPiecePanel();
	static ScorePanel scorePanel = new ScorePanel();
	static MenuPanel menuPanel = new MenuPanel();
	
	// This is public so it can be accessed by the AudioManager
	public static SettingsPanel settingsPanel = new SettingsPanel();
	
}
