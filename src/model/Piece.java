package model;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ui.GameBoardPanel;

// Data-layer class that represents pieces. The primary functions
// of this class are to provide methods for determining whether the
// current piece can be moved in a certain direction, and if so,
// to reassign it's current position to reflect the move
public class Piece {
	
	private Color color;
	
	// Orientation map. Provides offsets to determine
	// which squares are lit up in the piece's bounding
	// grid for each orientation
	private int[][][] orientationMap;
	
	// Denotes which squares are lit up when the piece is
	// displayed on the "Next Piece" panel
	private int[][] nextPanelSquares;
	
	// Starting row index position of the piece
	private int startRow;
	
	// Location of piece on the game board.
	// Corresponds to the location of the
	// bottom left corner of the bounding grid.
	// Set as piece comes off conveyor belt or
	// is released from the hold panel
	private int[] location;
	
	// Used to index into the orientation map
	private int orientation = 0;
	
	// Holds the currently lit squares of the piece.
	// Recalculated every time the piece moves or rotates.
	// Not set until the piece comes off the factory conveyor
	// belt, in case the piece needs to be shifted updwards
	// first
	private int[][] litSquares = null;
	
	// This flag is necessary since released hold pieces must be placed - they
	// can not be re-held. This is to prevent manual shifting of the piece
	// conveyor belt
	private boolean isHoldPiece = false;
	
	public Piece(Color color, int[][][] orientationMap, int[][] nextPanelSqaures, int startRow) {
		
		this.color = color;
		this.orientationMap = orientationMap;
		this.nextPanelSquares = nextPanelSqaures;
		this.startRow = startRow;

	}
	
	public int getRow() { return location[0]; }
	public int getCol() { return location[1]; }
	
	public Color getColor() { return color; }
	public int[][] getLitSquares() { return litSquares; }
	public int[][] getNextPanelSquares() { return nextPanelSquares; }
	
	// Movement methods. Each time a movement is made, the piece's
	// lit squares must be recalculated
	public void move(int rowMove, int colMove) {
		location[0] += rowMove;
		location[1] += colMove;
		litSquares = calcLitSquares();
	}
	
	// Valid orientation values cycle through the range 0-3
	public void rotate(int rotation) {
	
		orientation += rotation;
		
		if (orientation == 4)
			orientation = 0;
		else if (orientation == -1)
			orientation = 3;
		
		litSquares = calcLitSquares();
		
	}
	
	public boolean isHoldPiece() { return isHoldPiece; }
	public void tagAsHoldPiece() { isHoldPiece = true; }
	
	// Returns a list of coordinates denoting which
	// squares the piece currently occupies, based
	// on its current location and orientation
	public int[][] calcLitSquares() {
		
		List<int[]> litSquares = new ArrayList<int[]>();
		
		// Iterate over each offset value in the orientation map
		// for the current orientation
		for (int[] offset : orientationMap[orientation]) {
			
			// Obtain the square that should be lit by adding
			// the offset to the current location
			int newRow = location[0] + offset[0];
			int newCol = location[1] + offset[1];
			
			// Even if the square is illegal, still add it. This is
			// necessary in order for the canRotate method to work
			// properly, since it will check the litSquares list
			// on its own after it rotates the piece
			litSquares.add(new int[]{newRow, newCol});
			
		}
		
		// All of my painting methods require arrays to work properly,
		// so convert the lit squares to an array before returning
		return litSquares.toArray(new int[litSquares.size()][2]);
		
	}

	// Returns the list of squares to highlight to show the piece's destination
	// position were it to be placed immediately. Null is returned if the piece
	// can't move downwards
	public int[][] getGhostSquares() {
		
		int downwardShift = 0;
		
		// Keep track of the current lit squares for the piece, since it will
		// be logically shifted downwards. This prevents me from having to
		// recalculate the lit squares when the piece is returned to its
		// original location
		int[][] currentLitSquares = litSquares;
	
		while (canMove(1,0)) {
			move(1,0);
			downwardShift++; 
		}
		
		if (downwardShift == 0)
			return null;
		
		else {
	
			int[][] ghostSquares = litSquares;
			
			// Reset the piece's location and lit squares
			location[0] -= downwardShift;
			litSquares = currentLitSquares;

			return ghostSquares;
			
		}
		
	}
	
	// Lit squares will be set to null if there are
	// no valid squares for the piece to occupy when
	// it first emerges
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
		// piece is rotated by logically rotating the piece
		// and then calculating the lit squares from the new
		// position
		rotate(orientationShift);
		int[][] destinationSquares = litSquares;
		rotate(orientationShift * -1); // Return piece to original position
		
		// Make sure all new squares are legal
		for (int[] litSquare : destinationSquares) 
			if (!GameBoardModel.isLegalSquare(litSquare[0], litSquare[1]))
				return false;
		
		return true;
		
	}
	
	// This is used right before the piece is popped off the factory conveyor belt to
	// determine which squares it initially occupies. In some cases, the piece will
	// be shifted up a few rows if there is not enough room for it to occupy
	// its normal initial squares. If the loop is exhausted (i.e. no valid initial
	// squares were found), it will cause game over, since lit squares will remain
	// assigned to null
	public void setInitialSquares() {
		
		location = new int[]{startRow, GameBoardPanel.CENTER_OFFSET};
		
		// Decrement location each iteration to test the next row above
		for (int row = startRow; row >= 0; location[0]--, row--) {
			
			// Set candidate squares
			int[][] candidateInitialSquares = calcLitSquares();
			
			if (GameBoardModel.areValidInitialSquares(candidateInitialSquares)) {
				litSquares = candidateInitialSquares;
				return;
			}
			
		}
		
	}
	
}
