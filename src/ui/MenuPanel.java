package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.AudioManager;
import model.GameBoardModel;

// Holds all the main menu buttons
public class MenuPanel extends JPanel {
	
	JButton start = new JButton("Start");
	JButton pause = new JButton("Pause");
	JButton resume = new JButton("Resume");
	JButton giveUp = new JButton("Give Up");
	
	// Button listeners are declared as their own concrete classes since I need to
	// control when they are enabled / disabled
	private ActionListener startButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			// Clear all old data from the model
			GameBoardModel.reset();
			
			// Reprint all squares on the game board. Since
			// the model was reset, they will all be blank
			GUI.gameBoardPanel.fullReprint();
			
			// Clears all old score info from previous games
			GUI.scorePanel.refreshScoreInfo();
			
			// Reset timer delay to default value
			Controller.fallTimer.setDelay(GameBoardModel.INITIAL_TIMER_DELAY);
			
			// Set initial pieces
			Controller.moveConveyorBelt();
			
			// Paint initial pieces
			GUI.gameBoardPanel.paintCurrentAndGhost();
			GUI.nextPiecePanel.clear(); // In case the piece is still painted from a previous game
			GUI.nextPiecePanel.paintCurrentPiece();
			
			GUI.gameBoardPanel.enablePieceMovementInput();
			
			// Enable pause, resume and give up buttons.
			// There is no reason for these to be enabled before the game starts
			enablePauseButton();
			enableResumeButton();
			enableGiveUpButton();
			
			// Start button is disabled once pressed. It will re-enable
			// after game over
			disableStartButton();
			
			// Both checkbox listeners get enabled
			GUI.settingsPanel.enableCbxListeners();
			
			Controller.fallTimer.start();
			
			AudioManager.beginCurrentSoundtrack();
			
		}
	
	};
	
	private ActionListener pauseButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			Controller.fallTimer.stop();
			
			// Don't allow sound to be turned on / off when game is paused
			GUI.settingsPanel.disableMusicCbxListener();
			
			AudioManager.stopCurrentSoundtrack();			
			AudioManager.playPauseSound();
			
			GUI.gameBoardPanel.disablePieceMovementInput();;
			disableGiveUpButton();
			
		}
	
	};
	
	private ActionListener resumeButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			Controller.fallTimer.start();
			
			// Re-enable sound to be turned on / off
			GUI.settingsPanel.enableMusicCbxListener();
			
			AudioManager.resumeCurrentSoundtrack();
			
			GUI.gameBoardPanel.enablePieceMovementInput();;
			enableGiveUpButton();
			
		}
		
	};
	
	private ActionListener giveUpButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			Controller.processGameOver();
		}
		
	};
	
	// Handy methods for enabling / disabling buttons
	void enableStartButton() { start.addActionListener(startButtonListener); }
	void enablePauseButton() { pause.addActionListener(pauseButtonListener); }
	void enableResumeButton() { resume.addActionListener(resumeButtonListener); }
	void enableGiveUpButton() { giveUp.addActionListener(giveUpButtonListener); }
	void disableStartButton() { start.removeActionListener(startButtonListener); }
	void disablePauseButton() { pause.removeActionListener(pauseButtonListener); }
	void disableResumeButton() { resume.removeActionListener(resumeButtonListener); }
	void disableGiveUpButton() { giveUp.removeActionListener(giveUpButtonListener); }
	
	MenuPanel() {
		
		setLayout(new FlowLayout());
		
		Map<JButton, Character> mnemonicMap = new HashMap<JButton, Character>();
		mnemonicMap.put(start, 's');
		mnemonicMap.put(pause, 'p');
		mnemonicMap.put(resume, 'r');
		mnemonicMap.put(giveUp, 'g');
		
		for (JButton b : new JButton[]{start, pause, resume, giveUp}) {
			
			// Set all buttons un-focusable
			b.setFocusable(false);
			b.setMnemonic(mnemonicMap.get(b));
			add(b);
			
		}
		
		enableStartButton();
		
	}
	
}
