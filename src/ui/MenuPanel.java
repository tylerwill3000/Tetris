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
			GameFrame.gameBoardPanel.fullReprint();
			
			// Clears all old score info from previous games
			GameFrame.scorePanel.refreshScoreInfo();
			
			// Reset timer delay to default value
			Controller.fallTimer.setDelay(GameBoardModel.INITIAL_TIMER_DELAY);
			
			// Set initial pieces
			Controller.moveConveyorBelt();
			
			// In case these still have pieces from previous games
			GameFrame.nextPiecePanel.clear();
			GameFrame.holdPanel.clear();
			GameFrame.holdPanel.currentPiece = null;
			
			// Paint initial pieces
			GameFrame.gameBoardPanel.paintCurrentAndGhost();
			GameFrame.nextPiecePanel.paintCurrentPiece();
			
			GameFrame.gameBoardPanel.enablePieceMovementInput();
			
			// Enable pause and give up buttons.
			// There is no reason for these to be enabled before the game starts
			enablePauseButton();
			enableGiveUpButton();
			disableStartButton();
			
			// Both checkbox listeners get enabled
			GameFrame.settingsPanel.enableCbxListeners();
			
			Controller.fallTimer.start();
			
			AudioManager.beginCurrentSoundtrack();
			
		}
	
	};
	
	private ActionListener pauseButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			Controller.fallTimer.stop();
			
			// Don't allow sound to be turned on / off when game is paused
			GameFrame.settingsPanel.disableMusicCbxListener();
			
			AudioManager.stopCurrentSoundtrack();			
			AudioManager.playPauseSound();
			
			GameFrame.gameBoardPanel.disablePieceMovementInput();;
			
			// Re-enable the resume button and disable pausing / give up
			enableResumeButton();
			disablePauseButton();
			disableGiveUpButton();
			
		}
	
	};
	
	private ActionListener resumeButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			Controller.fallTimer.start();
			
			// Re-enable sound to be turned on / off
			GameFrame.settingsPanel.enableMusicCbxListener();
			
			AudioManager.resumeCurrentSoundtrack();
			
			GameFrame.gameBoardPanel.enablePieceMovementInput();;
			
			// Re-enable pause and give up buttons and disable resume
			enablePauseButton();
			enableGiveUpButton();
			disableResumeButton();
			
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
