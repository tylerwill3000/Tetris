package ui;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

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
		
		public void keyPressed(KeyEvent e) {			
			
			switch (e.getKeyCode()) {
				
			case KeyEvent.VK_LEFT:
				
				if (currentPiece.canMove(0,-1)) {
					eraseCurrentAndGhost();
					currentPiece.move(0,-1);
					paintCurrentAndGhost();
				}
				
				break;
				
			case KeyEvent.VK_RIGHT:
				
				if (currentPiece.canMove(0,1)) {
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
		
	}
	
	// Separate listener for menu hotkeys. This should never get disabled
	private class MenuHotkeyInput extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			
			switch (e.getKeyCode()) {
			
			case KeyEvent.VK_S:
				
				GUI.menuPanel.start.doClick();
				break;
			
			case KeyEvent.VK_P:
				
				GUI.menuPanel.pause.doClick();
				break;
				
			case KeyEvent.VK_R:
				
				GUI.menuPanel.resume.doClick();
				break;
				
			case KeyEvent.VK_G:
				
				GUI.menuPanel.giveUp.doClick();
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
			
			JPanel panel = JPanelGrid[row][cell];
			
			// If square is occupied, paint the color. Else,
			// erase any current contents that may have been
			// occupying that square
			if (GameBoardModel.isSquareOccupied(row, cell))
				paintSquare(panel, GameBoardModel.getColor(row, cell));
			else 
				eraseSquare(panel);
			
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
		
		if (!GUI.settingsPanel.ghostSquaresOn())
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
		
		for (int i = 0; i < V_CELLS; i++)
			paintRow(i);
		
	}
	
	// Fills in all squares in the grid in a spiral pattern.
	void startSpiralAnimation() {
		GameFrame.THREAD_EXECUTOR.execute(spiralAnimation);
	}
	
	// Erases all squares in the grid after filling them bottom to top
	void startClearAnimation() {
		GameFrame.THREAD_EXECUTOR.execute(clearAnimation);
	}
	
	// BGUIlds the list of spiral squares. Squares are in order
	// from the top left corner spiraling inwards, CCW
	private static List<int[]> initSpiralSquares() {
		
		List<int[]> squares = new ArrayList<int[]>();
		
		// Stores indices of next column / row that needs to
		// be processed
		int nextLeftCol = 0,
			nextBottomRow = V_CELLS-1,
			nextRightCol = H_CELLS-1,
			nextTopRow = 0;
		
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
	
		private final static int SLEEP_INTERVAL = 9;
		
		public void run() {
			
			try { // In order to catch InterupptedException from calling Thread.sleep
			
			// Run 1 loop to paint in all unoccupied squares
			for (int[] square : SPIRAL_SQUARES) {
				
				JPanel p = JPanelGrid[square[0]][square[1]];
			
				if (!GameBoardModel.isSquareOccupied(square[0], square[1]))
					paintSquare(p, PieceFactory.getRandomColor());
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			// Run a second loop to erase all of them
			for (int[] square : SPIRAL_SQUARES) {
				eraseSquare(JPanelGrid[square[0]][square[1]]);
				Thread.sleep(SLEEP_INTERVAL);
			}
			
			// Re-enable the start button once the spiral loop is completed
			GUI.menuPanel.enableStartButton();
			
			}
			catch (InterruptedException e) {}
			
		}
		
	}
	
	// Runnable task class that is started upon game complete
	// to clear the board. Fills all rows bottom to top, and then
	// clears all rows top to bottom
	private class ClearAnimation implements Runnable {
		
		private final static int SLEEP_INTERVAL = 80;
		
		public void run() {
			
			try {
			
			// Fill all rows bottom to top
			for (int row = V_CELLS - 1; row >= 0; row --) {
				
				for (int col = 0; col < H_CELLS; col++) {
					
					JPanel p = JPanelGrid[row][col];
					
					if (!GameBoardModel.isSquareOccupied(row, col))
						paintSquare(p, PieceFactory.getRandomColor());
					
				}
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			// Clear all rows top to bottom
			for (int row = 0; row < V_CELLS; row ++) {
				
				for (int col = 0; col < H_CELLS; col++)
					eraseSquare(JPanelGrid[row][col]);					
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			}
			catch (InterruptedException e) {}
			
		}
		
	}
	
	// Thread task class for flashing a row a couple times.
	// Used when lines are cleared
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
				catch (InterruptedException e) {}

			}

		}
		
	}
	
}
