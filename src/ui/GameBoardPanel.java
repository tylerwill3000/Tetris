package ui;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;

// The GameBoardPanel is dedicated to the "View" portion
// of the game area - it paints the panel according to the
// existing piece configuration as specified by the GameBoardModel
// as well as the active piece configuration as specified by
// the current piece
public class GameBoardPanel extends GridPainter {
	
	// Board dimensions
	public static final int V_CELLS = 20;
	public static final int H_CELLS = 10;
	
	// Amount to offset to get to the center of the board. Useful
	// when calculating initial squares for pieces
	public static final int CENTER_OFFSET = H_CELLS / 2;
	
	// List of spiral squares in order from the top left corner going inwards
	private static final List<int[]> SPIRAL_SQUARES = initSpiralSquares();
	
	Thread spiralAnimation = new Thread(new SpiralAnimation());
	Thread clearAnimation = new Thread(new ClearAnimation());
	
	// Keyboard listeners
	private PieceMovementInput pieceMovementInput = new PieceMovementInput();
	private MenuHotkeyInput menuHotkeyInput = new MenuHotkeyInput();
	
	GameBoardPanel() {
		
		super(V_CELLS, H_CELLS);
		
		// Piece movement listener is added once start button is clicked
		addKeyListener(menuHotkeyInput);
		
		setFocusable(true);
		setBorder(GameFrame.LINE_BORDER);
		
	}
	
	void enablePieceMovementInput() {
		addKeyListener(pieceMovementInput);
	}
	
	void disablePieceMovementInput() {
		removeKeyListener(pieceMovementInput);
	}
	
	// Listener for the piece movement input from the keyboard
	private class PieceMovementInput extends KeyAdapter {
		
		Set<Integer> pressed = new HashSet<>();
		
		public void keyPressed(KeyEvent e) {			
			
			int code = e.getKeyCode();
			
			pressed.add(code);
			
			switch (code) {
				
			case KeyEvent.VK_LEFT:
				
				// Perform super-shift if 's' is pressed
				if (pressed.contains(KeyEvent.VK_S)) {
					
					eraseCurrentAndGhost();
					
					while (currentPiece.canMove(0,-1))
						currentPiece.move(0,-1);
					
					AudioManager.playSuperslideSound();
					
					paintCurrentAndGhost();
					
				}
				
				else if (currentPiece.canMove(0,-1)) {
					eraseCurrentAndGhost();
					currentPiece.move(0,-1);
					paintCurrentAndGhost();
				}
				
				break;
				
			case KeyEvent.VK_RIGHT:
				
				// Perform super-shift is 's' is pressed
				if (pressed.contains(KeyEvent.VK_S)) {
					
					eraseCurrentAndGhost();
					
					while (currentPiece.canMove(0,1))
						currentPiece.move(0,1);
					
					AudioManager.playSuperslideSound();
					
					paintCurrentAndGhost();
					
				}
				
				else if (currentPiece.canMove(0,1)) {
					eraseCurrentAndGhost();
					currentPiece.move(0,1);
					paintCurrentAndGhost();
				}					
				
				break;
				
			case KeyEvent.VK_DOWN:
				
				if (currentPiece.canMove(1,0)) {
					eraseCurrentPiece();
					currentPiece.move(1,0);
					paintCurrentPiece();
				}
				
				break;
				
			case KeyEvent.VK_UP:
					
				if (currentPiece.canRotate(1)) {
					eraseCurrentAndGhost();
					currentPiece.rotate(1);
					AudioManager.playCWRotationSound();
					paintCurrentAndGhost();
				}
				
				
				break;
				
			case KeyEvent.VK_F:
				
				if (currentPiece.canRotate(-1)) {
					eraseCurrentAndGhost();
					currentPiece.rotate(-1);
					AudioManager.playCCWRotationSound();
					paintCurrentAndGhost();
				}
				
				break;
			
			// Hold set
			case KeyEvent.VK_D:
				
				// Can't hold a new piece if you're already holding one or the
				// piece has been tagged as a hold piece (these must be placed)
				if (GameFrame.holdPanel.currentPiece != null || currentPiece.isHoldPiece())
					return;
				
				currentPiece.tagAsHoldPiece();
				GameFrame.holdPanel.currentPiece = currentPiece;
				GameFrame.holdPanel.paintCurrentPiece();
				
				eraseCurrentAndGhost();
				GameFrame.nextPiecePanel.clear();
				
				AudioManager.playHoldSound();
				
				Controller.moveConveyorBelt();
				
				paintCurrentAndGhost();
				GameFrame.nextPiecePanel.paintCurrentPiece();
			
				break;
			
			// Hold release
			case KeyEvent.VK_E:
				
				if (GameFrame.holdPanel.currentPiece == null) return;
				
				eraseCurrentAndGhost();
				GameFrame.holdPanel.clear();
				
				AudioManager.playReleaseSound();
				
				currentPiece = GameFrame.holdPanel.currentPiece;
				GameFrame.holdPanel.currentPiece = null;
				currentPiece.setInitialSquares();		
				
				paintCurrentAndGhost();
				
				break;				
				
			case KeyEvent.VK_SPACE:
				
				eraseCurrentPiece();
				
				while (currentPiece.canMove(1,0))
					currentPiece.move(1,0);
				
				paintCurrentPiece();
				
				AudioManager.playPiecePlacementSound();
				
				// Force the next tick to execute immediately on the timer
				Controller.fallTimer.restart();
				
				break;
				
			}				
			
		}
		
		public void keyReleased(KeyEvent e) {
			pressed.remove(e.getKeyCode());
		}
		
	}
	
	// Separate listener for menu hotkeys. This should never get disabled
	private class MenuHotkeyInput extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			
			switch (e.getKeyCode()) {
			
			case KeyEvent.VK_S:
				
				// Only perform a do-click when timer is stopped. Otherwise,
				// the button click animation gets annoying when pressing
				// 's' to do a super-slide
				if (!Controller.fallTimer.isRunning())
					GameFrame.menuPanel.start.doClick();
				
				break;
			
			case KeyEvent.VK_P:
				
				GameFrame.menuPanel.pause.doClick();
				break;
				
			case KeyEvent.VK_R:
				
				GameFrame.menuPanel.resume.doClick();
				break;
				
			case KeyEvent.VK_G:
				
				GameFrame.menuPanel.giveUp.doClick();
				break;
			
			case KeyEvent.VK_H:
				
				GameFrame.menuPanel.highScores.doClick();
				break;
			
			}
				
		}
		
	}
	
	void paintCurrentAndGhost() {
		paintGhostPiece();
		paintCurrentPiece();
	}
	
	void eraseCurrentAndGhost() {
		eraseGhostPiece();
		eraseCurrentPiece();
	}
	
	// Lowers the currently active piece down 1 square
	void lowerPiece() {
		eraseCurrentPiece();
		currentPiece.move(1,0);
		paintCurrentPiece();
	}
	
	// Repaints the specified row according to the GameBoardModel
	void paintRow(int row) {
		
		// Iterate over all cell values in the row
		for (int cell = 0; cell < H_CELLS; cell++) {
			
			// If square is occupied, paint the color. Else,
			// erase any current contents that may have been
			// occupying that square
			if (GameBoardModel.isSquareOccupied(row, cell))
				paintSquare(row, cell, GameBoardModel.getColor(row, cell));
			else 
				eraseSquare(row, cell);
			
		}
		
	}
	
	// Flashes the specified row white (with no border)
	private void flashRow(int row) {
		
		for (int cell = 0; cell < H_CELLS; cell++) {
			JPanelGrid[row][cell].setBorder(null);
			JPanelGrid[row][cell].setBackground(Color.WHITE);
		}
		
	}
	
	void paintCurrentPiece() {
		paintSquares(currentPiece.getLitSquares(), currentPiece.getColor());
	}
	
	void paintGhostPiece() {
		
		if (!GameFrame.settingsPanel.ghostSquaresOn())
			return;
		
		paintSquares(currentPiece.getGhostSquares(), null);
	
	}
	
	void eraseCurrentPiece() {
		eraseSquares(currentPiece.getLitSquares());
	}
	
	void eraseGhostPiece() {
		eraseSquares(currentPiece.getGhostSquares());
	}
	
	// Reprints all panels on the game board according to
	// the grid model
	void fullReprint() {
		
		// Start at row 3 since 3 invisible rows at the top are not printed
		for (int i = 3; i < V_CELLS; i++) paintRow(i);
		
	}
	
	// Fills in all squares in the grid in a spiral pattern.
	void startSpiralAnimation() {
		GameFrame.THREAD_EXECUTOR.execute(spiralAnimation);
	}
	
	// Erases all squares in the grid after filling them bottom to top
	void startClearAnimation() {
		GameFrame.THREAD_EXECUTOR.execute(clearAnimation);
	}
	
	// Builds the list of spiral squares. Squares are in order
	// from the top left corner spiraling inwards, CCW
	private static List<int[]> initSpiralSquares() {
		
		List<int[]> squares = new ArrayList<int[]>();
		
		// Stores indices of next column / row that needs to
		// be processed
		int nextLeftCol = 0,
			nextBottomRow = V_CELLS+2, // Account for 3 invisible rows at top of board
			nextRightCol = H_CELLS-1,
			nextTopRow = 3;
		
		// Total squares is equal to the dimensions of the 
		// visible panels. Loop until the size of squares
		// reaches this amount
		while (squares.size() < H_CELLS * V_CELLS) {
			
			// Get all cells in the next leftmost column
			for (int row = nextTopRow; row <= nextBottomRow; row++)
				squares.add(new int[]{row, nextLeftCol});
			
			// Leftmost column has been processed
			nextLeftCol++;
			
			// Get all cells in the next bottom row
			for (int col = nextLeftCol; col <= nextRightCol; col++)
				squares.add(new int[]{nextBottomRow, col});
			
			// Bottom row has been processed
			nextBottomRow--;
			
			// Get all cells in the next rightmost column
			for (int row = nextBottomRow; row >= nextTopRow; row--)
				squares.add(new int[]{row, nextRightCol});
			
			// Rightmost column has been processed
			nextRightCol--;
			
			// Get all cells in the next top row
			for (int col = nextRightCol; col >= nextLeftCol; col--)
				squares.add(new int[]{nextTopRow, col});
			
			// Top row has been processed
			nextTopRow++;
			
		}
		
		return squares;
		
	}

/***************** Thread task classes *****************/	
	
	// Thread task class for painting the game over fill
	private class SpiralAnimation implements Runnable {
		
		private final static int SLEEP_INTERVAL = 8;
		
		public void run() {
			
			try { // In order to catch InterupptedException from calling Thread.sleep
			
			// Run 1 loop to paint in all unoccupied squares
			for (int[] square : SPIRAL_SQUARES) {
				
				if (!GameBoardModel.isSquareOccupied(square[0], square[1]))
					paintSquare(square[0], square[1], PieceFactory.getRandomColor());
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			// Run a second loop to erase all of them
			for (int[] square : SPIRAL_SQUARES) {
				eraseSquare(square[0], square[1]);
				Thread.sleep(SLEEP_INTERVAL);
			}
			
			// Re-enable the start button once the spiral loop is completed
			GameFrame.menuPanel.enableStartButton();
			
			// Ask player to save score
			if (GameFrame.settingsPanel.saveScoreOn()) new SaveScoreFrame();
			
			}
			catch (InterruptedException e) {} // Munch
			
		}
		
	}
	
	// Runnable task class that is started upon game complete
	// to clear the board. Fills all rows bottom to top, and then
	// clears all rows top to bottom
	private class ClearAnimation implements Runnable {
		
		private final static int SLEEP_INTERVAL = 82;
		
		public void run() {
			
			try {
			
			// Fill all rows bottom to top. Make sure to account for
			// invisible rows at the top of the board
			for (int row = V_CELLS+2; row >= 3; row --) {
				
				for (int col = 0; col < H_CELLS; col++) {
					
					if (!GameBoardModel.isSquareOccupied(row, col))
						paintSquare(row, col, PieceFactory.getRandomColor());
					
				}
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			// Clear all rows top to bottom. Again, account for invisible
			// rows at top
			for (int row = 3; row <= V_CELLS+2; row ++) {
				
				for (int col = 0; col < H_CELLS; col++)
					eraseSquare(row, col);					
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			// Re-enable start button
			GameFrame.menuPanel.enableStartButton();
			
			// Ask player to save score
			if (GameFrame.settingsPanel.saveScoreOn()) new SaveScoreFrame();
			
			}
			catch (InterruptedException e) {}
			
		}
		
	}
	
	// Thread task class for flashing a row a couple times.
	// Used when lines are cleared. NOTE: this isn't working
	// right now! I'm not sure if I will ever fix it...
	class FlashRowsTask implements Runnable {
		
		private List<Integer> rowsToFlash;
		
		public FlashRowsTask(List<Integer> completeLines) {
			this.rowsToFlash = completeLines;
		}			
		
		public void run() {
			
			// Flash the rows three times before clearing it
			for (int i = 1; i <= 9; i ++) {
				
				for (Integer row : rowsToFlash) {
					
					if (i % 2 == 0)
						paintRow(row);
					else
						flashRow(row);
					
				}
				
				try { Thread.sleep(20); } 
				catch (InterruptedException e) {} // Munch

			}

		}
		
	}
	
}
