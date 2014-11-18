package ui;

import ui.GameBoardPanel;
import ui.MenuPanel;
import ui.NextPiecePanel;
import ui.ScorePanel;
import ui.SettingsPanel;

// Package-private box that I stick all the GUI components in so they can
// "talk" to each other
public class GUI {
	
	static GameBoardPanel gameBoardPanel = new GameBoardPanel();
	static NextPiecePanel nextPiecePanel = new NextPiecePanel("Next Piece");
	static NextPiecePanel holdPanel = new NextPiecePanel("Hold");
	static ScorePanel scorePanel = new ScorePanel();
	static MenuPanel menuPanel = new MenuPanel();
	
	// This is public so it can be accessed by the AudioManager
	public static SettingsPanel settingsPanel = new SettingsPanel();
	
}
