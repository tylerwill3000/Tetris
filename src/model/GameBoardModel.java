package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ui.GameBoardPanel;

/**
 *  Describes the current configuration of pieces placed on the grid and
 *  handles placement of new pieces
 * @author Tyler
 */
public final class GameBoardModel {
	
	private GameBoardModel() {}
	
	// Represents colors on the game grid
	private static LinkedList<Color[]> _quilt;
	
	public static Color getColor(int row, int col) { return _quilt.get(row)[col]; }
	
	public static void setColor(Color c, int row, int col) {
		
		// Will cause out of bounds errors in cases where piece extends above top of board
		if (row < 0) return;
		
		_quilt.get(row)[col] = c;
		
	}
	
	/**
	 *  Adds the active squares for a piece to the quilt.
	 *  
	 * @param p The piece that is to be placed onto the grid
	 * @return A list of line indices to be cleared as a result of placing this piece.
	 * This list can be empty.
	 */
	public static List<Integer> addPiece(Piece p) {
		
		for (int[] litSquare : p.getLitSquares())
			setColor(p.getColor(), litSquare[0], litSquare[1]);
		
		List<Integer> completeLines = new ArrayList<>();
		
		// Iterate from the bottom row of the piece's bounding
		// matrix upwards to check for complete lines
		int startRow = p.getRow();
		
		// The highest bounding matrix is 4 units (for the straight line),
		// so you'll never have to search further than that to find completed
		// lines
		for (int row = startRow; row >= 0 && row > startRow - 4; row--)
			if (isCompleteRow(row)) completeLines.add(row);
		
		return completeLines;
		
	}
	
	private static boolean isCompleteRow(int rowIndex) {
		
		for (Color c : _quilt.get(rowIndex)) 
			if (c == null) return false;
			
		return true;
		
	}
	
	/**
	 * Removes the specified completed lines from the quilt
	 * @param toRemove A list of line indices to remove from the grid
	 */
	public static void removeCompleteLines(List<Integer> toRemove) {
		
		// Since removing a line essentially increases the row index value
		// of all lines above it by 1, removing multiple lines in sequence
		// requires an 'offset' to target the correct line. This offset
		// is increased each time a line is removed
		Iterator<Integer> iter = toRemove.iterator();
		for (int offset = 0; iter.hasNext(); offset++) {
			
			_quilt.remove(iter.next().intValue() + offset);
	
			// Add a new blank line to the top of the quilt to
			// replace the line that was just removed
			_quilt.offerFirst(new Color[GameBoardPanel.H_CELLS]);
			
		}
		
		ScoreModel.increaseScore(toRemove.size());
		
	}
	
	public static boolean isSquareOccupied(int row, int col) { 
		return (row < 0 ? false : getColor(row, col) != null);
	}
	
	private static boolean isInBoundsSquare(int row, int col) {
		
		// Add 3 to game board panel V_CELLS to account for 3 invisible rows at the
		// top of the board
		return col >= 0 && row < GameBoardPanel.V_CELLS+3 && col < GameBoardPanel.H_CELLS;
		
	}
	
	/**
	 *  Checks whether the specified square is both in bounds and
	 *  not already occupied.
	 * @param row Row coordinate for the square to be checked
	 * @param col Column coordinate for the square to be checked
	 * @return Whether the square is both in bounds and not occupied
	 */
	public static boolean isLegalSquare(int row, int col) {
	
		// Crucial to check in bounds before checking if the square
		// is occupied to short circuit the && - otherwise, you'll
		// generate an index out of bounds error when checking the quilt
		return isInBoundsSquare(row, col) && !isSquareOccupied(row, col);
		
	}
	
	/**
	 * Checks whether or not the specified list of squares are valid initial
	 * squares for a piece
	 * @param candidateSquares A list of squares that may or may not be valid initial squares
	 * @return Whether the specified squares are valid initial squares
	 */
	public static boolean areValidInitialSquares(int[][] candidateSquares) {
		
		// Flag for whether at least 1 square is a visible square (since some
		// rows above the top of the game grid are invisible)
		boolean hasVisibleSquares = false;
	
		for (int[] square : candidateSquares) {
			
			// >= 3 to account for the 3 invisible squares at the top
			if (square[0] >= 3) hasVisibleSquares = true;
			
			// As soon as a square is found that is already occupied, it can
			// be concluded that these squares are not valid
			if (isSquareOccupied(square[0], square[1]))
				return false;
			
		}
		
		// If the loop was completed, all squares were unoccupied. So, in order
		// for these to be valid starting squares, there also must be at least
		// one visible square
		return hasVisibleSquares;
		
	}
	
	/**
	 *  Removes all data from the grid
	 */
	public static void reset() {
		
		_quilt = buildStartingquilt();
		
		/** Method to set a static piece configuration for developing my icon
		 *  
		 *  RRRY
		 *  RGYY
		 *  GGBY
		 *  GBBB
		
		for (int[] s : new int[][]{ {3,0},{3,1},{3,2},{4,0} })
			setColor(new Color(255,30,0), s[0], s[1]);
		
		for (int[] s : new int[][]{ {5,2},{6,1},{6,2},{6,3} })
			setColor(new Color(0,70,255), s[0], s[1]);
		
		for (int[] s : new int[][]{ {4,1},{5,0},{5,1},{6,0} })
			setColor(Color.GREEN, s[0], s[1]);
		
		for (int[] s : new int[][]{ {3,3},{4,2},{4,3},{5,3} })
			setColor(Color.YELLOW, s[0], s[1]);	
		*/
	}
	
	private static LinkedList<Color[]> buildStartingquilt() {

		LinkedList<Color[]> quilt = new LinkedList<Color[]>();
		
		// +3 to account for the 3 invisible rows at the top of
		// the board
		for (int i = 0; i <= GameBoardPanel.V_CELLS+3; i++) 
			quilt.add(new Color[GameBoardPanel.H_CELLS]);
			
		return quilt;
		
	}
	
}
