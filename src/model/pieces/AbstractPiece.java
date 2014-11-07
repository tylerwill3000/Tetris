package model.pieces;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.GameBoardModel;
import ui.GameBoardPanel;

// The piece class is abstracted in order to allow the
// concrete sub-class implementations to return unique
// square / coordinate info
public abstract class AbstractPiece {
	
	// Location of piece on the game board.
	// Corresponds to the bottom left corner
	// of the bounding grid. Start out on row 1
	// instead of 0 (the top) since some pieces
	// have two rows to display instead of 1
	private int[] location;
	
	// Orientation of the piece. Used to index into the
	// orientation map. Starts out at 0, the default
	private int orientation = 0;
	
	// Holds the currently lit squares to the piece.
	// Recalculated every time the piece moves or rotates
	private int[][] litSquares;
	
	// Piece's color
	private Color color;
	
	private int startRow;
	
	protected AbstractPiece(Color color, int startRow) {
		
		this.color = color;
		this.startRow = startRow;
		
		location = new int[]{startRow, GameBoardPanel.CENTER_OFFSET};
		
	}
	
	// Location getters. Since some pieces can be placed with the bottom
	// row of their bounding matrix empty, it can cause the location to
	// extend beyond the number of vertical cells, so perform a quick
	// check to prevent an out of bounds index from getting returned if
	// this is the case
	public int getRow() {
		return location[0] >= GameBoardPanel.V_CELLS ? GameBoardPanel.V_CELLS - 1 : location[0];
	}
	public int getCol() { return location[1]; }
	
	// Movement methods. Each time a movement is made, the piece's
	// lit squares must be recalculated
	public void setRow(int newRow) {
		location[0] = newRow;
		litSquares = calcLitSquares();
	}
	
	public void setCol(int newCol) {
		location[1] = newCol;
		litSquares = calcLitSquares();
	}
	
	
	public void move(int rowMove, int colMove) {
		location[0] += rowMove;
		location[1] += colMove;
		litSquares = calcLitSquares();
	}

	public void rotate(int rotation) {
	
		orientation += rotation;
		
		if (orientation == 4)
			orientation = 0;
		else if (orientation == -1)
			orientation = 3;
		
		litSquares = calcLitSquares();
		
	}

	public Color getColor() { return color; }
	
	public int[][] getLitSquares() { return litSquares; }
	
	// Returns a list of coordinates denoting which
	// squares the piece currently occupies on the
	// game board
	public int[][] calcLitSquares() {
		
		List<int[]> litSquares = new ArrayList<int[]>();
		
		// Iterate over each offset value in the orientation map
		// for the current orientation
		for (int[] offset : getOrientationMap(orientation)) {
			
			// Obtain the square that should be lit by adding
			// the offset to the current location
			int row = location[0] + offset[0];
			int col = location[1] + offset[1];
			
			// Even if the square is illegal, still add it. This is
			// necessary in order for the canRotate method to work
			// properly, since it will check the litSquares list
			// on its own after it rotates the piece
			litSquares.add(new int[]{row, col});
			
		}
		
		return litSquares.toArray(new int[litSquares.size()][2]);
		
	}

	// Returns the list of squares to highlight to show the piece's destination
	// position were it to be placed immediately. Null is returned if the piece
	// can't move downwards
	public int[][] getGhostSquares() {
		
		int downwardShift = 0;
	
		while (canMove(1,0)) {
			move(1,0);
			downwardShift++; 
		}
		
		if (downwardShift == 0)
			return null;
		
		else {
	
			int[][] ghostSquares = calcLitSquares();
			setRow(location[0] - downwardShift); // Return piece to actual location
			return ghostSquares;
			
		}
		
	}
	
	// Lit squares will be set to null if there are
	// no valid squares for the piece
	public boolean canEmerge() { return litSquares != null; }
	
	public boolean canMove(int rowMove, int colMove) {

		// Make sure all new squares are legal
		for (int[] litSquare : litSquares) {

			int potentialRow = litSquare[0] + rowMove;
			int potentialCol = litSquare[1] + colMove;
			
			if (!GameBoardModel.isLegalSquare(potentialRow, potentialCol))
				return false;
			
		}

		return true;
		
	}
	
	// Checks to see if the piece can be rotated. Pass 1 for CW, -1 for CCW
	public boolean canRotate(int orientationShift) {
		
		// Build a list of squares that will be active if the
		// piece is rotated
		rotate(orientationShift);
		int[][] destinationSquares = calcLitSquares();
		rotate(orientationShift * -1); // Return piece to original position
		
		// Make sure all new squares are legal
		for (int[] litSquare : destinationSquares) {
			
			if (!GameBoardModel.isLegalSquare(litSquare[0], litSquare[1]))
				return false;
		}
		
		return true;
		
	}
	
	// This is used right before the piece is popped off the factory conveyor belt to
	// determine which squares it initially occupies. In some cases, the piece will
	// be shifted up a single row if there is not enough room for it to occupy
	// its normal initial squares. This method runs a loop until it finds suitable
	// squares, setting them to null if none are found
	public void setInitialSquares() {
		
		int[][] initials;
		
		for (int row = startRow; row >= 0; location[0]--, row--) {
			
			initials = calcLitSquares();
			
			// In order for these squares to be valid initial squares,
			// they must have at least 1 visible square and must all
			// be unoccupied
			boolean hasVisibleSquares = false;
			boolean allUnoccupied = true;
			
			// Run both checks against all squares
			for (int[] square : initials) {
				
				if (square[0] >= 0) hasVisibleSquares = true;
				
				if (GameBoardModel.isSquareOccupied(square[0], square[1])) allUnoccupied = false;
				
			}
			
			// See if these squares pass the test. If so, can return
			// from the method
			if (hasVisibleSquares && allUnoccupied) {
				litSquares = initials;
				return;
			}
			
		}
		
		litSquares = null;
		
	}
	
	/** Abstract methods */
	
	// The grid coordinates of which squares the piece will
	// occupy when displayed in the 'Next Piece' box
	public abstract int[][] getNextPanelSquares();
	
	// Returns an array of lists denoting the offsets from
	// the piece's current location to the squares it
	// currently occupies. Each list corresponds to a
	// unique orientation (0-3)
	public abstract int[][] getOrientationMap(int orientation);
	
}
