package ui;

import javax.swing.JCheckBox;

// Package-private box that I stick all the UI components in so they can
// "talk" to each other
public class UIBox {
	
	static GameBoardPanel gameBoardPanel = new GameBoardPanel();
	static NextPiecePanel nextPiecePanel = new NextPiecePanel();
	static ScorePanel scorePanel = new ScorePanel();
	static MenuPanel menuPanel = new MenuPanel();
	
	static JCheckBox ghostSquaresCbx = new JCheckBox("Ghost Squares", true);
	
	// These are public so they can be seen by the AudioManager
	public static JCheckBox musicCbx = new JCheckBox("Music", true);
	public static JCheckBox soundEffectsCbx = new JCheckBox("Sound Effects", true);
	
}
