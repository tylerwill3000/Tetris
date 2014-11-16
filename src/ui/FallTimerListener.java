package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.AudioManager;
import model.GameBoardModel;

// What happens each 'tick' of the game clock
public class FallTimerListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
					
		// If the piece can be lowered, lower it and then return
		if (UIBox.gameBoardPanel.currentPiece.canMove(1,0)) {
			UIBox.gameBoardPanel.lowerPiece();
			return; 
		}
		
		// Place the piece and then update the scoring info to reflect
		// any increase in score from lines removed
		UIBox.gameBoardPanel.placePiece();
		UIBox.scorePanel.refreshScoreInfo();
		
		// Release space bar lock
		GameBoardModel.spacePressed = false;
		
		// See if game is complete by placing this piece
		if (GameBoardModel.getLevel() == 11)
			processGameComplete();
		
		else {
			
			// Not the most elegant way to handle level up stuff, this works for now
			if (GameBoardModel.justLeveled) processLevelUp();

			if (!UIBox.gameBoardPanel.currentPiece.canEmerge())
				processGameOver();
			
		}
		
	}
	
	// Resets timer delay / flashes level label
	private void processLevelUp() {
		UIBox.gameBoardPanel.fallTimer.setDelay(GameBoardModel.getTimerDelay());
		UIBox.scorePanel.flashLevelLabel();
		GameBoardModel.justLeveled = false;
	}
	
	// What happens when the final level is cleared
	private void processGameComplete() {
		
		UIBox.gameBoardPanel.fallTimer.stop();
		
		UIBox.scorePanel.flashWinMessage();
		
		//AudioManager.playVictoryJingle(); TODO
		
		UIBox.gameBoardPanel.disablePieceMovementInput();
		
		// Disable all buttons but the start button
		UIBox.menuPanel.enableStartButton();
		UIBox.menuPanel.disablePauseButton();
		UIBox.menuPanel.disableResumeButton();
		UIBox.menuPanel.disableGiveUpButton();

	}
	
	// What happens when the next piece can't emerge. Static so it can
	// be accessed by the give up button listener
	static void processGameOver() {
		
		// Stop the fall timer
		UIBox.gameBoardPanel.fallTimer.stop();
		
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
		
	}

}
