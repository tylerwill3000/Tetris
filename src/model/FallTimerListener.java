package model;

import java.awt.event.*;

import javax.swing.Timer;

import model.pieces.AbstractPiece;
import ui.GameBoardPanel;
import ui.NextPiecePanel;
import ui.ScorePanel;

// The fall timer listener class defines the logic
// for what needs to happen on each tick of the
// game timer while the game is running
public class FallTimerListener implements ActionListener {
	
	private GameBoardPanel gameBoardPanel;
	private NextPiecePanel nextPiecePanel;
	private ScorePanel scorePanel;
	
	public FallTimerListener(GameBoardPanel gameBoardPanel, NextPiecePanel nextPiecePanel, ScorePanel scorePanel) {
		
		this.gameBoardPanel = gameBoardPanel;
		this.nextPiecePanel = nextPiecePanel;
		this.scorePanel = scorePanel;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (gameBoardPanel.getCurrentPiece().canMove(1,0)) {
			gameBoardPanel.lowerPiece();
		}
		else {
	
			gameBoardPanel.placePiece();

			// Enable the keyboard if it was disabled
			// as a result of the player hitting the space 
			// bar to place the piece
			if (!gameBoardPanel.isKeyboardEnabled())
				gameBoardPanel.enableKeyboard();

			// Generate a new piece. Before continuing, make sure
			// it is able to emerge onto the board. If not, it's
			// game over
			AbstractPiece nextPiece = PieceFactory.receiveNextPiece();

			if (!nextPiece.canEmerge()) {
				
				((Timer)e.getSource()).stop();
				AudioManager.stopCurrentSoundtrack();
				AudioManager.playGameOverSound();
				scorePanel.refreshScoreInfo();
				gameBoardPanel.disableKeyboard();
				gameBoardPanel.paintGameOverFill();
				
			}
		
			else {

				gameBoardPanel.setCurrentPiece(nextPiece);
				scorePanel.refreshScoreInfo();
				nextPiecePanel.eraseCurrentPiece();
				nextPiecePanel.setCurrentPiece(PieceFactory.peekAtNextPiece());
				nextPiecePanel.paintCurrentPiece();
				gameBoardPanel.paintCurrentAndGhost();
				
			}
			
		}
		
	}
	
}


