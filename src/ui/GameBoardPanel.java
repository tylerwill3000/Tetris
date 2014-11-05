package ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Timer;

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
	public static final int V_CELLS = 23; // 3 extra because of the "hidden" squares at the top
	public static final int H_CELLS = 10;
	
	// Amount to offset to get to the center of the board. Useful
	// when calculation initial squares for pieces
	public static final int CENTER_OFFSET = H_CELLS / 2;
	
	// List of spiral squares in order from the top left corner going inwards
	public static final int[][] spiralSquares = initSpiralSquares();
	
	// Tracks whether or not the keyboard is enabled. Used when
	// placing a piece with spacebar, since it disables the 
	// keyboard until the next piece is generated
	private boolean keyboardEnabled;
	
	// Listener for the keyboard
	private KeyboardInput keyboardInput = new KeyboardInput();
	
	public GameBoardPanel() {
		
		super(V_CELLS, H_CELLS);
		
		// -3 to prevent the 3 hidden rows from displaying at top
		setLayout(new GridLayout(V_CELLS-3, H_CELLS));
		
		// Add all JPanel objects to the panel, excluding
		// those on the first 3 rows since they are't
		// visible
		for (int row = 3; row < V_CELLS; row++) {
			
			for (int col = 0; col < H_CELLS; col++) {
				
				add(JPanelGrid[row][col]);
				
			}
			
		}		
	
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
			
			switch (keyCode) {
				
				case KeyEvent.VK_LEFT:
					
					eraseCurrentAndGhost();
					
					if (currentPiece.canMove(0,-1)) currentPiece.move(0,-1);
					
					paintCurrentAndGhost();
					
					break;
					
				case KeyEvent.VK_RIGHT:
					
					eraseCurrentAndGhost();
					
					if (currentPiece.canMove(0,1)) currentPiece.move(0,1);
					
					paintCurrentAndGhost();
					
					break;
					
				case KeyEvent.VK_DOWN:
					
					eraseCurrentPiece();
					
					if (currentPiece.canMove(1,0)) currentPiece.move(1,0);
					
					paintCurrentPiece();
					
					break;
					
				case KeyEvent.VK_UP:
					
					eraseCurrentAndGhost();
					
					if (pressed.size() > 1 && pressed.contains(KeyEvent.VK_CONTROL)) {
					
						if (currentPiece.canRotate(-1)) {
							currentPiece.rotate(-1);
							AudioManager.playCCWRotationSound();
						}
					}
					
					else {
						
						if (currentPiece.canRotate(1)) {
							currentPiece.rotate(1);
							AudioManager.playCWRotationSound();
						}
					}
					
					paintCurrentAndGhost();					
					
					break;
					
				case KeyEvent.VK_SPACE:
					
					eraseCurrentPiece();
					
					while (currentPiece.canMove(1,0)) { currentPiece.move(1,0); }
					
					paintCurrentPiece();
					
					AudioManager.playPiecePlacementSound();
					
					disableKeyboard(); // Prevents player from moving the piece before it gets logged
					
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
	
	// Lowers the currently active piece down 1 square. Erasing / painting
	// the ghost piece is necessary here in case the ghost squares checkbox
	// is clicked mid-fall
	public void lowerPiece() {
		eraseCurrentAndGhost();
		currentPiece.move(1,0);
		paintCurrentAndGhost();
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
			
			AudioManager.playClearLineSound();
			
			// Play explosion sound if ultra line
			//if (completeLines.size() == 4) AudioManager.playUltraLineSound();
			
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
				panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				
			}
			
			// Nullify all unoccupied panels in the row
			else 
				nullifyPanel(panel);
			
		}
		
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getLitSquares(), currentPiece.getColor());
	}
	
	private void paintGhostPiece() {
		
		if (!GameBoardModel.isUsingGhostSquares()) return;
		
		int[][] ghostSquares = currentPiece.getGhostSquares();
		
		if (ghostSquares != null) {
			
			for (int[] ghostSquare : ghostSquares) {
				
				JPanel panel = JPanelGrid[ghostSquare[0]][ghostSquare[1]];
				panel.setBackground(null); // Provides 'ghost' effect
				panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					
			}
			
		}
	
	}
	
	public void eraseCurrentPiece() {
		eraseSquares(currentPiece.getLitSquares());
	}
	
	private void eraseGhostPiece() {
		
		int[][] ghostSquares = currentPiece.getGhostSquares();
		
		if (ghostSquares != null) 
			eraseSquares(ghostSquares);
	
	}
	
	// Fills in all squares in the grid in a spiral pattern. This is
	// a really messy way to handle painting the game over squares,
	// but it works alright for now
	private Timer spiralTimer = new Timer(1, new ActionListener() {
		
		private boolean erasing = false;
		private int currentSquare = 0;
		
		public void actionPerformed(ActionEvent e) {
			
			if (currentSquare == spiralSquares.length) {
				
				if (erasing == false) {
				
					// Run through 1 more cycle to erase
					currentSquare = 0;
					erasing = true;
					
				}
				else {
					spiralTimer.stop();
					GameBoardModel.reset();
				}
			}
			else {
				
				// Grab the JPanel object and the square
				// coordinates
				int[] square = spiralSquares[currentSquare];
				JPanel p = JPanelGrid[square[0]][square[1]];
				
				if (erasing)
					nullifyPanel(p);
				
				else {
					
					if (!GameBoardModel.isSquareOccupied(square[0], square[1])) {
		
						p.setBackground(PieceFactory.getRandomColor());
						p.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
						
					}
				
				}
				
				currentSquare++;
			
			}
			
		}
		
	});
	
	public void paintGameOverFill() {
		spiralTimer.start();
	}
	
	// Builds the list of spiral squares. Squares are in order
	// from the top left corner spiraling inwards, CCW
	private static int[][] initSpiralSquares() {
		
		List<int[]> squares = new ArrayList<int[]>();
		
		// Stores indices of next column / row that needs to
		// be processed
		int nextLeftCol = 0;
		int nextBottomRow = V_CELLS-1;
		int nextRightCol = H_CELLS-1;
		int nextTopRow = 3; // Don't paint the invisible rows
		
		// Total squares is equal to the dimensions of the 
		// visible panels. Loop until the size of squares
		// reaches this amount
		int squareLimit = H_CELLS * (V_CELLS-3);
		while (squares.size() < squareLimit) {
			
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
		
		return squares.toArray(new int[squares.size()][2]);
		
	}
	
}
