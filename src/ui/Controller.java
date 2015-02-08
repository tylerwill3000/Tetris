package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;

/**
 *  Class is dedicated to controlling the game flow
 * @author Tyler
 */
public class Controller {
	
	/**
	 * Fall timer that controls the speed at which pieces
	 * move down the screen. Speed is set in the listener for
	 * the start button, so there is no need to provide a 
	 * default value here
	 */
	static Timer _fallTimer = new Timer(0, new ActionListener() {

		public void actionPerformed(ActionEvent e) {

			// If the piece can be lowered, lower it
			if (GameFrame._gameBoardPanel._currentPiece.canMove(1, 0))
				GameFrame._gameBoardPanel.lowerPiece();
			
			// Piece cannot be lowered, so must add it to the log of
			// current pieces on the board
			else {
				
				// Obtain a list of all complete lines (if any) that
				// result from permanently adding this piece to the board
				List<Integer> completeLines = GameBoardModel.addPiece(GameFrame._gameBoardPanel._currentPiece);
				
				if (!completeLines.isEmpty()) {
					
					// TODO I need to fix the flashing task
					// Execute a new flashing rows task for these complete lines
					// Thread flash = new Thread(GUI.gameBoardPanel.new FlashRowsTask(completeLines));
					// GameFrame.THREAD_EXECUTOR.execute(flash);

					GameBoardModel.removeCompleteLines(completeLines);
					AudioManager.playClearLineSound(completeLines.size());
					GameFrame._scorePanel.refreshScoreInfo();
					
					// Update game grid display to reflect new configuration
					// after removing lines
					updateGameGrid(completeLines);

					if (GameBoardModel._justLeveled) processLevelUp();
					
				}
				
				// Move the conveyor belt along to set the new pieces for both piece panels
				moveConveyorBelt();
				
				// If next piece can't emerge, it's game over
				if (!GameFrame._gameBoardPanel._currentPiece.canEmerge())
					processGameOver();
				
				else {
					GameFrame._gameBoardPanel.paintCurrentAndGhost();
					GameFrame._nextPiecePanel.clear();
					GameFrame._nextPiecePanel.paintCurrentPiece();
				}

			}
			
		}
		
	});
	
	/**
	 * Moves the factory conveyor belt along 1 piece, setting
	 * the game board panel's piece to the first piece in line and
	 * the next piece panel's to the one directly following it.
	 * Package-private so it can be accessed by the start button listener
	 * to set the initial pieces on game load
	 */
	static void moveConveyorBelt() {
		GameFrame._gameBoardPanel._currentPiece = PieceFactory.receiveNextPiece();
		GameFrame._nextPiecePanel._currentPiece = PieceFactory.peekAtNextPiece();
	}
	
	/**
	 *  Repaints the game grid rows according to the new piece configuration after
	 *  removing lines
	 * @param removedLines Number of lines removed
	 */
	private static void updateGameGrid(List<Integer> removedLines) {
		
		// Should only have to paint upwards from the bottom removed line
		for (int line = removedLines.get(0); line >= 0; line--)
			GameFrame._gameBoardPanel.paintRow(line);

	}
	
	// For if removing lines causes a level up
	private static void processLevelUp() {
		
		if (GameBoardModel.getLevel() == 11)
			processGameComplete();
		else {
			_fallTimer.setDelay(GameBoardModel.getCurrentTimerDelay());
			GameFrame._scorePanel.flashLevelLabel();
		}
		
		GameBoardModel._justLeveled = false;
		
	}
	
	// For if removing lines causes game complete
	private static void processGameComplete() {
		
		_fallTimer.stop();
		
		GameFrame._scorePanel.flashWinMessage();
		GameFrame._gameBoardPanel.disablePieceMovementInput();
		GameFrame._gameBoardPanel.startClearAnimation();
		
		AudioManager.playVictoryFanfare();
		AudioManager.resetSoundtrackFramePositions();
		
		GameFrame._menuPanel.disablePauseButton();
		GameFrame._menuPanel.disableResumeButton();
		GameFrame._menuPanel.disableGiveUpButton();
		
		GameFrame._settingsPanel.disableCbxListeners();
		GameFrame._settingsPanel.enableDifficultyList();
		GameFrame._settingsPanel.enableSpecialPiecesButton();
		GameFrame._settingsPanel.enableBlockStylesButton();
		GameFrame._settingsPanel.enableDatabaseConnectivityButton();
		
	}
	
	/**
	 * What happens when the next piece can't emerge. Package-private so
	 * it can be accessed by the game over listener
	 */
	static void processGameOver() {

		_fallTimer.stop();
		
		AudioManager.stopCurrentSoundtrack();
		AudioManager.playGameOverSound();
		
		AudioManager.resetSoundtrackFramePositions();
		
		GameFrame._scorePanel.flashGameOverMessage();
		GameFrame._gameBoardPanel.startSpiralAnimation();
		
		GameFrame._gameBoardPanel.disablePieceMovementInput();
		
		GameFrame._menuPanel.disablePauseButton();
		GameFrame._menuPanel.disableResumeButton();
		GameFrame._menuPanel.disableGiveUpButton();
		
		GameFrame._settingsPanel.disableCbxListeners();
		GameFrame._settingsPanel.enableDifficultyList();
		GameFrame._settingsPanel.enableSpecialPiecesButton();
		GameFrame._settingsPanel.enableBlockStylesButton();
		GameFrame._settingsPanel.enableDatabaseConnectivityButton();
		
	}
	
}
