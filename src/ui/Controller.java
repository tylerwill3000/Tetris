package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.SwingUtilities;
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
			if (UIBox.gameBoardPanel.currentPiece.canMove(1,0)) 
				UIBox.gameBoardPanel.lowerPiece();
	 
			else {
				
				// Release space bar lock
				GameBoardModel.spacePressed = false;
				
				// Obtain a list of all complete lines (if any) that
				// result from adding this piece
				final List<Integer> completeLines = GameBoardModel.addPiece(UIBox.gameBoardPanel.currentPiece);
				
				if (!completeLines.isEmpty()) {
					
					// TODO I need to fix the flashing task
					// Execute a new flashing rows task for these complete lines
					// GameFrame.THREAD_EXECUTOR.execute(UIBox.gameBoardPanel.new FlashRowsTask(completeLines));
				
					// Actually remove the rows from the model
					GameBoardModel.removeCompleteLines(completeLines);
					
					AudioManager.playClearLineSound(completeLines.size());
					
					// Show new score
					UIBox.scorePanel.refreshScoreInfo();
					
					// Repaint lines on the game grid
					updateGameGrid(completeLines);
					
					// Process level up functions if just leveled
					if (GameBoardModel.justLeveled)
						processLevelUp();
					
				}
				
				// Move the conveyor belt along
				moveConveyorBelt();
				
				// If next piece can't emerge, it's game over
				if (!UIBox.gameBoardPanel.currentPiece.canEmerge())
					processGameOver();
				else {
					UIBox.gameBoardPanel.paintCurrentAndGhost();
					UIBox.nextPiecePanel.clear();
					UIBox.nextPiecePanel.paintCurrentPiece();
				}

			}
			
		}
		
	});
	
	// Move the factory conveyor belt along 1 piece, setting
	// the game board panel's piece to the first piece in line and
	// the next piece panel's to the one directly following it
	private static void moveConveyorBelt() {
		
		UIBox.gameBoardPanel.currentPiece = PieceFactory.receiveNextPiece();
		UIBox.nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
		
	}
	
	// Repaints the rows according to the new piece configuration after
	// removing lines
	private static void updateGameGrid(List<Integer> removedLines) {
		
		// Should only have to paint upwards from the bottom removed line
		for (int line = removedLines.get(0); line >= 0; line--)
			UIBox.gameBoardPanel.paintRow(line);

	}
	
	// For if removing these lines causes a level up
	private static void processLevelUp() {
		
		if (GameBoardModel.getLevel() == 11)
			processGameComplete();
		
		else {
			
			// Begin playing the new soundtrack
			AudioManager.beginCurrentSoundtrack();
			
			fallTimer.setDelay(GameBoardModel.getTimerDelay());
			UIBox.scorePanel.flashLevelLabel();
			GameBoardModel.justLeveled = false;
		}
		
	}
	
	// For if removing these lines causes game complete
	private static void processGameComplete() {
		
		fallTimer.stop();
		
		UIBox.scorePanel.flashWinMessage();
		UIBox.gameBoardPanel.disablePieceMovementInput();
		
		// Disable all buttons but the start button
		UIBox.menuPanel.enableStartButton();
		UIBox.menuPanel.disablePauseButton();
		UIBox.menuPanel.disableResumeButton();
		UIBox.menuPanel.disableGiveUpButton();
		
		// Disable cbx listeners
		UIBox.settingsPanel.disableCbxListeners();
		
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
		UIBox.scorePanel.flashGameOverMessage();
		UIBox.gameBoardPanel.startSpiralAnimation();
		
		// Disable keyboard
		UIBox.gameBoardPanel.disablePieceMovementInput();
		
		// Disable the pause, resume, and give up buttons
		// They will re-enable once the next game starts
		UIBox.menuPanel.disablePauseButton();
		UIBox.menuPanel.disableResumeButton();
		UIBox.menuPanel.disableGiveUpButton();
		
		// Disable the checkbox listeners
		UIBox.settingsPanel.disableCbxListeners();
		
	}	
	
}
