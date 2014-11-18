package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;

// This class is dedicated to controlling the game flow
public class Controller {
	
	// The fall timer that controls the speed at which pieces
	// move down the screen. Speed is set in the listener for
	// the start button, so there is no need to provide a 
	// default value here
	static Timer fallTimer = new Timer(0, new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			// If the piece can be lowered, lower it
			if (GUI.gameBoardPanel.currentPiece.canMove(1, 0))
				GUI.gameBoardPanel.lowerPiece();
	 
			else {
				
				// Obtain a list of all complete lines (if any) that
				// result from adding this piece
				List<Integer> completeLines = GameBoardModel.addPiece(GUI.gameBoardPanel.currentPiece);
				
				if (!completeLines.isEmpty()) {
					
					// TODO I need to fix the flashing task
					/* Execute a new flashing rows task for these complete lines
					Thread flash = new Thread(GUI.gameBoardPanel.new FlashRowsTask(completeLines));
					GameFrame.THREAD_EXECUTOR.execute(flash);*/
					
					// Remove the rows from the model
					GameBoardModel.removeCompleteLines(completeLines);
					
					AudioManager.playClearLineSound(completeLines.size());
					
					// Show new score
					GUI.scorePanel.refreshScoreInfo();
					
					// Repaint lines on the game grid
					updateGameGrid(completeLines);
					
					// Process level up functions if just leveled
					if (GameBoardModel.justLeveled)
						processLevelUp();
					
				}
				
				// Move the conveyor belt along to set the new pieces for both piece panels
				moveConveyorBelt();
				
				// If next piece can't emerge, it's game over
				if (!GUI.gameBoardPanel.currentPiece.canEmerge())
					processGameOver();
				
				else {
					GUI.gameBoardPanel.paintCurrentAndGhost();
					GUI.nextPiecePanel.clear();
					GUI.nextPiecePanel.paintCurrentPiece();
				}

			}
			
		}
		
	});
	
	// Moves the factory conveyor belt along 1 piece, setting
	// the game board panel's piece to the first piece in line and
	// the next piece panel's to the one directly following it.
	// Package-private so it can be accessed by the start button listener
	// to set the initial pieces on game load
	static void moveConveyorBelt() {
		
		GUI.gameBoardPanel.currentPiece = PieceFactory.receiveNextPiece();
		GUI.nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
		
	}
	
	// Repaints the rows according to the new piece configuration after
	// removing lines
	private static void updateGameGrid(List<Integer> removedLines) {
		
		// Should only have to paint upwards from the bottom removed line
		for (int line = removedLines.get(0); line >= 0; line--)
			GUI.gameBoardPanel.paintRow(line);

	}
	
	// For if removing lines causes a level up
	private static void processLevelUp() {
		
		if (GameBoardModel.getLevel() == 11)
			processGameComplete();
		
		else {
			
			fallTimer.setDelay(GameBoardModel.getTimerDelay());
			GUI.scorePanel.flashLevelLabel();
			
		}
		
		GameBoardModel.justLeveled = false;
		
	}
	
	// For if removing lines causes game complete
	private static void processGameComplete() {
		
		fallTimer.stop();
		
		GUI.scorePanel.flashWinMessage();
		GUI.gameBoardPanel.disablePieceMovementInput();
		
		// Disable all buttons but the start button
		GUI.menuPanel.enableStartButton();
		GUI.menuPanel.disablePauseButton();
		GUI.menuPanel.disableResumeButton();
		GUI.menuPanel.disableGiveUpButton();
		
		// Disable cbx listeners
		GUI.settingsPanel.disableCbxListeners();
		
	}
	
	// What happens when the next piece can't emerge. Package-private so
	// it can be accessed by the game over listener
	static void processGameOver() {
		
		// Stop the fall timer
		fallTimer.stop();
		
		// Change audio over to game over sound
		AudioManager.stopCurrentSoundtrack();
		AudioManager.playGameOverSound();		
		
		// Display spiral animation and flash game over message
		GUI.scorePanel.flashGameOverMessage();
		GUI.gameBoardPanel.startSpiralAnimation();
		
		// Disable keyboard
		GUI.gameBoardPanel.disablePieceMovementInput();
		
		// Disable the pause, resume, and give up buttons
		// They will re-enable once the next game starts
		GUI.menuPanel.disablePauseButton();
		GUI.menuPanel.disableResumeButton();
		GUI.menuPanel.disableGiveUpButton();
		
		// Disable the checkbox listeners
		GUI.settingsPanel.disableCbxListeners();
		
	}
	
}
