
import java.awt.event.*;

import javax.swing.Timer;

// The fall timer listener class defines the logic
// for what needs to happen on each tick of the
// game timer while the game is running
public class FallTimerListener implements ActionListener {
	
	private GameBoardPanel gameBoard;
	private NextPiecePanel nextPiecePanel;
	private ScorePanel scorePanel;
	
	// Current falling piece
	private AbstractPiece currentPiece;
	
	public FallTimerListener(GameBoardPanel gameBoardPanel, NextPiecePanel nextPiecePanel, ScorePanel scorePanel) {
		
		this.gameBoard = gameBoardPanel;
		this.nextPiecePanel = nextPiecePanel;
		this.scorePanel = scorePanel;
		
		currentPiece = PieceFactory.receiveNextPiece();
		this.gameBoard.setCurrentPiece(currentPiece);
		this.nextPiecePanel.setCurrentPiece(PieceFactory.peekAtNextPiece());;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (currentPiece.canMove(1,0)) {
			gameBoard.lowerPiece();
		}
		else {
	
			gameBoard.placePiece();

			// Enable the keyboard if it was disabled
			// as a result of the player hitting the space 
			// bar to place the piece
			if (!gameBoard.isKeyboardEnabled())
				gameBoard.enableKeyboard();

			// Generate a new piece. Before continuing, make sure
			// it is able to emerge onto the board. If not, it's
			// game over
			currentPiece = PieceFactory.receiveNextPiece();

			if (!currentPiece.canEmerge()) {
				((Timer)e.getSource()).stop();
				AudioManager.stopCurrentSoundtrack();
				AudioManager.playGameOverSound();
				scorePanel.refreshScoreInfo();
				gameBoard.disableKeyboard();
				gameBoard.paintGameOverFill();
			}
		
			else {
				
				// Reset the current piece for the game board
				// and then perform a full print (piece and ghost)
				gameBoard.setCurrentPiece(currentPiece);
				scorePanel.refreshScoreInfo();
				nextPiecePanel.eraseCurrentPiece();
				nextPiecePanel.setCurrentPiece(PieceFactory.peekAtNextPiece());
				nextPiecePanel.paintCurrentPiece();
				gameBoard.paintCurrentAndGhost();
				
			}
			
		}
		
	}
	
}


