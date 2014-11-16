package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;

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
			UIBox.gameBoardPanel.fullReprint();
			
			// Clears all old score info from previous games
			UIBox.scorePanel.refreshScoreInfo();
			
			// Reset timer delay to default value
			UIBox.gameBoardPanel.fallTimer.setDelay(GameBoardModel.INITIAL_TIMER_DELAY);
			
			// Set initial pieces
			UIBox.gameBoardPanel.currentPiece = PieceFactory.receiveNextPiece();
			UIBox.nextPiecePanel.eraseCurrentPiece(); // In case the piece is still painted from a previous game
			UIBox.nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
			
			// Paint initial pieces
			UIBox.gameBoardPanel.paintCurrentAndGhost();
			UIBox.nextPiecePanel.paintCurrentPiece();
			
			UIBox.gameBoardPanel.enablePieceMovementInput();;
			
			// Enable pause, resume and give up buttons.
			// There is no reason for these to be enabled before the game starts
			enablePauseButton();
			enableResumeButton();
			enableGiveUpButton();
			
			// Start button is disabled once pressed. It will re-enable
			// after game over
			disableStartButton();
			
			UIBox.gameBoardPanel.fallTimer.start();
			
			AudioManager.beginCurrentSoundtrack();
			
		}
	
	};
	
	private ActionListener pauseButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			UIBox.gameBoardPanel.fallTimer.stop();
			
			AudioManager.stopCurrentSoundtrack();			
			AudioManager.playPauseSound();
			
			UIBox.gameBoardPanel.disablePieceMovementInput();;
			disableGiveUpButton();
			
		}
	
	};
	
	private ActionListener resumeButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			UIBox.gameBoardPanel.fallTimer.start();
			
			AudioManager.resumeCurrentSoundtrack();
			
			UIBox.gameBoardPanel.enablePieceMovementInput();;
			enableGiveUpButton();
			
		}
		
	};
	
	private ActionListener giveUpButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			FallTimerListener.processGameOver();
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
		
		for (JButton b : new JButton[]{start, pause, resume, giveUp}) {
			
			// Set all buttons un-focusable
			b.setFocusable(false);
			add(b);
			
		}
		
		enableStartButton();
		
	}
	
	
}
