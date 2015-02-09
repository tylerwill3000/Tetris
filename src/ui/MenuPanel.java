package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import ui.components.TetrisButton;
import ui.secondaryWindows.HighScoreFrame;
import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;
import model.ScoreModel;

/**
 *  Holds all the main menu buttons
 * @author Tyler
 */
public class MenuPanel extends JPanel {
	
	TetrisButton _btnStart = new TetrisButton("Start");
	TetrisButton _btnPause = new TetrisButton("Pause");
	TetrisButton _btnResume = new TetrisButton("Resume");
	TetrisButton _btnGiveUp = new TetrisButton("Give Up");
	TetrisButton _btnHighScores = new TetrisButton("High Scores");
	
	// Button listeners are declared as their own concrete classes since I need to
	// control when they are enabled / disabled
	private ActionListener _startButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			// Clear old data from previous game
			GameBoardModel.reset();
			ScoreModel.reset();
			
			// Reprint all squares on the game board. Since
			// the model was reset, they will all be blank
			GameFrame._gameBoardPanel.fullReprint();
			
			
			if (GameFrame._settingsPanel.timeAttackOn()) {
				GameFrame._scorePanel.showProgressBar();
				GameFrame._scorePanel.refreshProgressBar();
			}
			else {
				GameFrame._scorePanel.hideProgressBar();
			}
			
			GameFrame._scorePanel.refreshScoreInfo();
			
			Controller._fallTimer.setDelay(Controller.INITIAL_TIMER_DELAY);
			
			PieceFactory.solidifyActivePieces();
			
			// This is extremely important in case the player changes which special
			// pieces are active between games. For example, there could still be
			// certain special pieces on the conveyor belt from the last game that
			// do not correspond to the special pieces set for this game. Resetting
			// the conveyor belt on each game start ensures no inappropriate pieces
			// make their way onto the belt
			PieceFactory.resetConveyorBelt();
			
			GameFrame._nextPiecePanel.clear();
			GameFrame._holdPanel._currentPiece = null;
			GameFrame._holdPanel.clear();
			
			// Sets initial pieces
			Controller.moveConveyorBelt();
			
			GameFrame._settingsPanel.disableDifficultyList();
			GameFrame._settingsPanel.disableSpecialPiecesButton();
			GameFrame._settingsPanel.disableBlockStylesButton();
			GameFrame._settingsPanel.disableDatabaseConnectivityButton();
			GameFrame._settingsPanel.disableTimeAttackCheckbox();
			
			GameFrame._gameBoardPanel.paintCurrentAndGhost();
			GameFrame._nextPiecePanel.paintCurrentPiece();
			
			GameFrame._gameBoardPanel.enablePieceMovementInput();
			
			enablePauseButton();
			enableGiveUpButton();
			disableStartButton();
			
			GameFrame._settingsPanel.enableCbxListeners();
			
			Controller._fallTimer.start();
			ScoreModel.restartGameTimer();
			
			AudioManager.beginCurrentSoundtrack();
			
		}
	
	};
	
	private ActionListener _pauseButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			Controller._fallTimer.stop();
			ScoreModel.stopGameTimer();
			
			// Don't allow sound to be turned on / off when game is paused
			GameFrame._settingsPanel.disableMusicCbxListener();
			
			AudioManager.stopCurrentSoundtrack();			
			AudioManager.playPauseSound();
			
			GameFrame._gameBoardPanel.disablePieceMovementInput();;
			
			// Re-enable the resume button and disable pausing / give up
			enableResumeButton();
			disablePauseButton();
			disableGiveUpButton();
			
		}
	
	};
	
	private ActionListener _resumeButtonListener = new ActionListener() {
				
		public void actionPerformed(ActionEvent e) {
			
			Controller._fallTimer.start();
			ScoreModel.startGameTimer();
			
			// Re-enable sound to be turned on / off instantly on
			// checkbox change
			GameFrame._settingsPanel.enableMusicCbxListener();
			
			AudioManager.resumeCurrentSoundtrack();
			
			GameFrame._gameBoardPanel.enablePieceMovementInput();;
			
			// Re-enable pause and give up buttons and disable resume
			enablePauseButton();
			enableGiveUpButton();
			disableResumeButton();
			
		}
		
	};
	
	private ActionListener _giveUpButtonListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			Controller.processGameOver();
		}
		
	};
	
	private ActionListener _highScoresListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			new HighScoreFrame(-1);
		}
		
	};
	
	// Handy methods for enabling / disabling buttons
	void enableStartButton() { _btnStart.addActionListener(_startButtonListener); }
	void enablePauseButton() { _btnPause.addActionListener(_pauseButtonListener); }
	void enableResumeButton() { _btnResume.addActionListener(_resumeButtonListener); }
	void enableGiveUpButton() { _btnGiveUp.addActionListener(_giveUpButtonListener); }
	void enableHighScoresButton() { _btnHighScores.addActionListener(_highScoresListener); }
	
	void disableStartButton() { _btnStart.removeActionListener(_startButtonListener); }
	void disablePauseButton() { _btnPause.removeActionListener(_pauseButtonListener); }
	void disableResumeButton() { _btnResume.removeActionListener(_resumeButtonListener); }
	void disableGiveUpButton() { _btnGiveUp.removeActionListener(_giveUpButtonListener); }
	void disableHighScoresButton() { _btnHighScores.removeActionListener(_highScoresListener); }
	
	MenuPanel() {
		
		setLayout(new FlowLayout());
		
		Map<TetrisButton, Character> mnemonicMap = new HashMap<TetrisButton, Character>();
		mnemonicMap.put(_btnStart, 's');
		mnemonicMap.put(_btnPause, 'p');
		mnemonicMap.put(_btnResume, 'r');
		mnemonicMap.put(_btnGiveUp, 'g');
		mnemonicMap.put(_btnHighScores, 'h');
		
		for (TetrisButton b : Arrays.asList(_btnStart, _btnPause, _btnResume, _btnGiveUp, _btnHighScores)) {
			b.setMnemonic(mnemonicMap.get(b));
			add(b);
		}
		
		enableStartButton();
		enableHighScoresButton();
		
	}
	
}
