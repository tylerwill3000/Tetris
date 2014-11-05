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
	// of the bounding grid.
	private int[] location = {4, GameBoardPanel.H_CELLS / 2};
	
	// Orientation of the piece. Used to index into the
	// orientation map. Starts out at 0, the default
	private int orientation = 0;
	
	// Holds the currently lit squares to the piece.
	// Recalculated every time the piece moves or rotates
	private int[][] litSquares;
	
	// Piece's color
	private Color color;
	
	protected AbstractPiece(Color color) {
		this.color = color;
		this.litSquares = calcLitSquares();
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
	
	public void setRow(int newRow) {
		location[0] = newRow;
		litSquares = calcLitSquares();
	}
	
	public void setCol(int newCol) {
		location[1] = newCol;
		litSquares = calcLitSquares();
	}
	
	public Color getColor() { return color; }
	
	public int[][] getLitSquares() { return litSquares; }
	
	// Movement method. Each time a movement is made, the piece's
	// lit squares must be recalculated
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
	
	// Checks to see whether there is room for the
	// piece to emerge onto the board
	public boolean canEmerge() {
		
		for (int[] square : getInitialSquares()) {
			
			if (GameBoardModel.isSquareOccupied(square[0], square[1]))
				return false;
			
		}
		
		return true;
		
	}
	
	// Returns a list of coordinates denoting which
	// squares the piece currently occupies on the
	// game board
	public int[][] calcLitSquares() {
		
		List<int[]> litSquares = new ArrayList<int[]>();
		
		// Iterate over each offset value in the orientation map
		// for the current orientation
		for (int[] offset : getOrientationMap()[orientation]) {
			
			// Obtain the square that should be lit by adding
			// the offset to the current location
			int row = location[0] + offset[0];
			int col = location[1] + offset[1];
			
			// Make sure new square is legal before adding it
			// to the list
			if (GameBoardModel.isLegalSquare(row, col))
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
			location[0]++;
			downwardShift++; 
		}
		
		if (downwardShift == 0)
			return null;
		
		else {
	
			int[][] ghostSquares = calcLitSquares();
			location[0] -= downwardShift; // Return piece to actual location
			
			return ghostSquares;
			
		}
		
	}
	
	public boolean canMove(int rowMove, int colMove) {

		// Make sure all new squares are legal
		for (int[] litSquare : calcLitSquares()) {
			
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
		
		// If the rotation was successful, all 4 destination squares were valid
		return destinationSquares.length == 4;
		
	}
	
	/** Abstract methods */
	
	// Returns a list of the squares initially occupied by the piece
	// when it first emerges onto the board
	public abstract int[][] getInitialSquares();
	
	// The grid coordinates of which squares the piece will
	// occupy when displayed in the 'Next Piece' box
	public abstract int[][] getNextPanelSquares();
	
	// Returns an array of lists denoting the offsets from
	// the piece's current location to the squares it
	// currently occupies. Each list corresponds to a
	// unique orientation (0-3)
	public abstract int[][][] getOrientationMap();
}
