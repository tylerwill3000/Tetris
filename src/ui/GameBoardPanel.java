package ui;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;

// The GameBoardPanel is dedicated to the "View" portion
// of the program - it paints the panel according to the
// existing piece configuration as specified by the gridModel
// as well as the active piece configuration as specified by
// the current piece
@SuppressWarnings("serial")
public class GameBoardPanel extends AbstractPiecePainter {
	
	// Board dimensions
	public static final int V_CELLS = 20;
	public static final int H_CELLS = 10;
	
	// Amount to offset to get to the center of the board. Useful
	// when calculation initial squares for pieces
	public static final int CENTER_OFFSET = H_CELLS / 2;
	
	// List of spiral squares in order from the top left corner going inwards
	public static final List<int[]> SPIRAL_SQUARES = initSpiralSquares();
	
	public Thread paintGameOverFill = new Thread(new PaintGameOverFill());
	
	// Tracks whether or not the keyboard is enabled. Used when
	// placing a piece with space bar, since it disables the 
	// keyboard until the next piece is generated
	private boolean keyboardEnabled;
	
	// Listener for the keyboard
	private KeyboardInput keyboardInput = new KeyboardInput();
	
	public GameBoardPanel() {
		
		super(V_CELLS, H_CELLS);
		setFocusable(true);
		setPreferredSize(new Dimension(GameFrame.GAME_BOARD_PANEL_WIDTH,750));
		
	}
	
	public boolean isKeyboardEnabled() { return keyboardEnabled; }
	
	public void disableKeyboard() {
		this.removeKeyListener(keyboardInput);
		keyboardEnabled = false;
	}
	
	public void enableKeyboard() {
		this.addKeyListener(keyboardInput);
		keyboardEnabled = true;
	}
	
	private class KeyboardInput extends KeyAdapter {
		
		private Set<Integer> pressed = new HashSet<Integer>();
		
		public void keyPressed(KeyEvent e) {
			
			int keyCode = e.getKeyCode();
			
			pressed.add(keyCode);			
			
			// Movement keys will not fire their actions if either the game is
			// paused or the space key has been pressed during this interval
			switch (keyCode) {
				
				case KeyEvent.VK_LEFT:
					
					if (GameBoardModel.isPaused || GameBoardModel.spacePressed) return;
					
					if (currentPiece.canMove(0,-1)) {
						eraseCurrentAndGhost();
						currentPiece.move(0,-1);
						paintCurrentAndGhost();
					}
					
					break;
					
				case KeyEvent.VK_RIGHT:
					
					if (GameBoardModel.isPaused || GameBoardModel.spacePressed) return;
					
					if (currentPiece.canMove(0,1)) {
						eraseCurrentAndGhost();
						currentPiece.move(0,1);
						paintCurrentAndGhost();
					}					
					
					break;
					
				case KeyEvent.VK_DOWN:
					
					if (GameBoardModel.isPaused || GameBoardModel.spacePressed) return;
					
					if (currentPiece.canMove(1,0)) {
						eraseCurrentPiece();
						currentPiece.move(1,0);
						paintCurrentPiece();
					}
					
					break;
					
				case KeyEvent.VK_UP:
					
					if (GameBoardModel.isPaused || GameBoardModel.spacePressed) return;
					
					if (pressed.size() > 1 && pressed.contains(KeyEvent.VK_CONTROL)) {
					
						if (currentPiece.canRotate(-1)) {
							eraseCurrentAndGhost();
							currentPiece.rotate(-1);
							AudioManager.playCCWRotationSound();
							paintCurrentAndGhost();
						}
					}
					
					else {
						
						if (currentPiece.canRotate(1)) {
							eraseCurrentAndGhost();
							currentPiece.rotate(1);
							AudioManager.playCWRotationSound();
							paintCurrentAndGhost();
						}
					}
					
					break;
					
				case KeyEvent.VK_SPACE:
					
					if (GameBoardModel.isPaused) return;
					
					eraseCurrentPiece();
					
					while (currentPiece.canMove(1,0)) currentPiece.move(1,0);
					
					paintCurrentPiece();
					
					AudioManager.playPiecePlacementSound();
					
					GameBoardModel.spacePressed = true;
					
					break;
				
				case KeyEvent.VK_P:
					
					UIBox.menuButtonsPanel.pause.doClick(100);
					break;
					
				case KeyEvent.VK_R:
					
					UIBox.menuButtonsPanel.resume.doClick(100);
					break;
					
				case KeyEvent.VK_G:
					
					UIBox.menuButtonsPanel.giveUp.doClick();
					break;
				
			}				
			
		}
		
		public void keyReleased(KeyEvent e) {
			pressed.remove(e.getKeyCode());
		}
		
	}
	
	public void paintCurrentAndGhost() {
		paintGhostPiece();
		paintCurrentPiece();
	}
	
	public void eraseCurrentAndGhost() {
		eraseGhostPiece();
		eraseCurrentPiece();
	}
	
	// Lowers the currently active piece down 1 square
	public void lowerPiece() {
		eraseCurrentPiece();
		currentPiece.move(1,0);
		paintCurrentPiece();
	}
	
	// Sets the current piece's position in stone, removing
	// any resulting complete lines
	public void placePiece() {

		// Log squares to the gridModel and receive the list
		// of completed lines
		List<Integer> completeLines = GameBoardModel.addPiece(currentPiece);
		
		if (!completeLines.isEmpty()) {
			
			// Iterate from the bottom completed line upwards,
			// repainting each row					
			for (int line = completeLines.get(0); line >= 0; line--)
				paintRow(line);
		
			// Play explosion sound if ultra line
			if (completeLines.size() == 4)
				AudioManager.playUltraLineSound();
			else
				AudioManager.playClearLineSound();
		}
	
	}
	
	// Repaints the specified row on the game grid according to the gridModel
	private void paintRow(int row) {
		
		// Iterate over all cell values in the row
		for (int cell = 0; cell < H_CELLS; cell++) {
			
			JPanel panel = JPanelGrid[row][cell];
			
			// If square is occupied, paint the color
			if (GameBoardModel.isSquareOccupied(row, cell)) {
		
				panel.setBackground(GameBoardModel.getColor(row, cell));
				panel.setBorder(GameFrame.BEVEL_BORDER);
				
			}
			
			// Nullify all unoccupied panels in the row
			else 
				nullifyPanel(panel);
			
		}
		
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getLitSquares(), currentPiece.getColor());
	}
	
	public void paintGhostPiece() {
		
		if (!UIBox.ghostSquaresCbx.isSelected()) return;
		
		paintSquares(currentPiece.getGhostSquares(), null);
	
	}
	
	public void eraseCurrentPiece() {
		eraseSquares(currentPiece.getLitSquares());
	}
	
	public void eraseGhostPiece() {
		eraseSquares(currentPiece.getGhostSquares());
	}
	
	// Reprints all panels on the game board according to
	// the grid model
	public void fullReprint() {
		
		for (int i = 0; i < V_CELLS; i++)
			paintRow(i);
		
	}
	
	// Fills in all squares in the grid in a spiral pattern.
	public void paintGameOverFill() { GameFrame.THREAD_EXECUTOR.execute(paintGameOverFill); }
	
	// Builds the list of spiral squares. Squares are in order
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
	
	// Thread task class for painting the game over fill
	private class PaintGameOverFill implements Runnable {
	
		private final static int SLEEP_INTERVAL = 9;
		
		public void run() {
			
			try { // In order to catch InterupptedException from calling Thread.sleep
			
			// Run 1 loop to paint in all unoccupied squares
			for (int[] square : SPIRAL_SQUARES) {
				
				JPanel p = JPanelGrid[square[0]][square[1]];
			
				if (!GameBoardModel.isSquareOccupied(square[0], square[1])) {
	
					p.setBackground(PieceFactory.getRandomColor());
					p.setBorder(GameFrame.BEVEL_BORDER);
					
				}
				
				Thread.sleep(SLEEP_INTERVAL);
				
			}
			
			// Run a second loop to erase all of them
			for (int[] square : SPIRAL_SQUARES) {
				nullifyPanel(JPanelGrid[square[0]][square[1]]);
				Thread.sleep(SLEEP_INTERVAL);
			}
			
			UIBox.menuButtonsPanel.start.addActionListener(UIBox.menuButtonsPanel.startButtonListener);
			
			}
			catch (InterruptedException e) {}
			
		}
		
	}
	
}
