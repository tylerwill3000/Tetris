package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ui.GameBoardPanel;
import ui.GameFrame;

// The GameBoardModel class describes the current configuration
// of placed pieces on the grid as well as tracks scoring info
public class GameBoardModel {
	
	public static final int INITIAL_TIMER_DELAY = 600;
	
	// Bonus points granted upon winning the game. Determined by difficulty
	private static final int[] WIN_BONUSES = {500,1000,2000};
	
	// Amount of milliseconds the timer delay decreases each level
	private static final int[] TIMER_DECREASE_RATES = {49,55,61};
	
	// Represents colors on the game grid
	private static LinkedList<Color[]> quilt;
	
	// Index of the selected option in the difficulty list is used
	// to index into this array to get lines per level
	private static final int[] LINES_PER_LEVEL = {15,20,25};
	
	// Point values per line based on lines cleared
	private static final int[] LINE_POINTS_MAP = {10,15,20,30};
	
	private static int linesCompleted = 0;
	private static int score = 0;
	private static int level = 0;
	
	// Flag for whether or not the player just increased level.
	// Used by the GUI components to know when to process level
	// up functions
	public static boolean justLeveled = false;
	
	public static Color getColor(int row, int col) { return quilt.get(row)[col]; }
	
	public static void setColor(Color c, int row, int col) {
		
		// Will cause out of bounds errors in cases where piece extends above top of board
		if (row < 0) return;
		
		quilt.get(row)[col] = c;
		
	}
	
	// Returns calculated timer delay based on current level and difficulty
	public static int getCurrentTimerDelay() {
		int milliDecrease = TIMER_DECREASE_RATES[GameFrame.settingsPanel.getDifficulty()] * (level - 1);
		return INITIAL_TIMER_DELAY - milliDecrease;
	}
	
	public static int getLinesCompleted() { return linesCompleted; }
	public static int getScore() { return score; }
	public static int getLevel() { return level; }
	
	private GameBoardModel() {}
	
	// Adds the active squares for a piece to the quilt. Used when a piece
	// is permanently placed somewhere. Returns a list of row indices for
	// completed lines (can be empty)
	public static List<Integer> addPiece(Piece p) {
		
		// Log all colors for this piece
		for (int[] litSquare : p.getLitSquares())
			setColor(p.getColor(), litSquare[0], litSquare[1]);
		
		return getCompleteLines(p);
	
	}
	
	private static List<Integer> getCompleteLines(Piece justPlaced) {
		
		List<Integer> completeLines = new ArrayList<>();
		
		// Iterate from the bottom row of the piece's bounding
		// matrix upwards to check for complete lines
		int startRow = justPlaced.getRow();
		
		// The highest bounding matrix is 4 units (for the straight line),
		// so you'll never have to search further than that to find completed
		// lines
		for (int row = startRow; row >= 0 && row > startRow - 4; row--)
			if (isCompleteRow(row)) completeLines.add(row);
		
		return completeLines;
		
	}
	
	private static boolean isCompleteRow(int rowIndex) {
		
		for (Color c : quilt.get(rowIndex)) 
			if (c == null) return false;
			
		return true;
		
	}
	
	// Removes the specified completed lines from the quilt
	public static void removeCompleteLines(List<Integer> toRemove) {
		
		// Since removing a line essentially increases the row index value
		// of all lines above it by 1, removing multiple lines in sequence
		// requires an 'offset' to target the correct line. This offset
		// is increased each time a line is removed
		Iterator<Integer> iter = toRemove.iterator();
		for (int offset = 0; iter.hasNext(); offset++, linesCompleted++) {
			
			quilt.remove(iter.next().intValue() + offset);
	
			// Add a new blank line to the top of the quilt to
			// replace the line that was just removed
			quilt.offerFirst(new Color[GameBoardPanel.H_CELLS]);
			
		}
		
		// Increase the score, passing the number of lines removed
		increaseScore(toRemove.size());
		
	}
	
	// Increases the player's score based on how many lines
	// were cleared
	private static void increaseScore(int completedLines) {
		
		int linePoints = completedLines * LINE_POINTS_MAP[completedLines-1];
		int difficultyBonus = completedLines * 5 * GameFrame.settingsPanel.getDifficulty();
		score += (linePoints + difficultyBonus);
		
		// Process level ups
		while (linesCompleted >= level * LINES_PER_LEVEL[GameFrame.settingsPanel.getDifficulty()]) {
			
			AudioManager.stopCurrentSoundtrack();
			level++;
			
			if (level == 11) // Game complete
				score += WIN_BONUSES[GameFrame.settingsPanel.getDifficulty()];
			else
				AudioManager.beginCurrentSoundtrack(); // Soundtrack for next level
			
			// Used to signal the UI components to initiate level up functions
			justLeveled = true;
			
		}
		
	}
	
	public static boolean isSquareOccupied(int row, int col) { 
		
		return (row < 0 ? false : getColor(row, col) != null);
		
	}
	
	private static boolean isInBoundsSquare(int row, int col) {
		
		// Add 3 to game board panel V_CELLS to account for 3 invisible rows at the
		// top of the board
		return col >= 0 && row < GameBoardPanel.V_CELLS+3 && col < GameBoardPanel.H_CELLS;
		
	}
	
	// Checks whether the specified square is both in bounds and
	// not already occupied
	public static boolean isLegalSquare(int row, int col) {
	
		// Crucial to check in bounds before checking if the square
		// is occupied to short circuit the && - otherwise, you'll
		// generate an index out of bounds error when checking the quilt
		return isInBoundsSquare(row, col) && !isSquareOccupied(row, col);
		
	}
	
	// Checks whether or not the specified list of squares are valid initial
	// squares for a piece
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
	
	// Removes all data from the quilt and resets scoring info
	public static void reset() {
		
		quilt = buildStartingquilt();
		score = 0;
		linesCompleted = 0;
		level = 1;
		
		/** Method to set a static piece configuration for developing my icon!
		 *  Icon plan is this:
		 *  
		 *  GGGG
		 *  BBBY
		 *  RBYY
		 *  RRRY

		for (int[] s : new int[][]{ {2,0},{3,0},{3,1},{3,2} })
			setColor(Color.RED, s[0], s[1]);
		
		for (int[] s : new int[][]{ {1,0},{1,1},{1,2},{2,1} })
			setColor(Color.BLUE, s[0], s[1]);
		
		for (int[] s : new int[][]{ {0,0},{0,1},{0,2},{0,3} })
			setColor(Color.GREEN, s[0], s[1]);
		
		for (int[] s : new int[][]{ {1,3},{2,2},{2,3},{3,3} })
			setColor(Color.YELLOW, s[0], s[1]);
		
		*/		
		
	}
	
	// Builds the blank starting quilt
	private static LinkedList<Color[]> buildStartingquilt() {

		LinkedList<Color[]> quilt = new LinkedList<Color[]>();
		
		// +3 to account for the 3 invisible rows at the top of
		// the board
		for (int i = 0; i <= GameBoardPanel.V_CELLS+3; i++) 
			quilt.add(new Color[GameBoardPanel.H_CELLS]);
			
		return quilt;
		
	}
	
	// For debugging
	public void print() {
		
		for (Color[] colorRow : quilt) {
	
			for (Color c : colorRow) {
				
				if (c == Color.RED)
					System.out.print("R ");
				else if (c == Color.BLUE)
					System.out.print("B ");
				else if (c == Color.ORANGE)
					System.out.print("O ");
				else if (c == Color.YELLOW)
					System.out.print("Y ");
				else if (c == Color.pink)
					System.out.print("P ");
				else if (c == Color.GREEN)
					System.out.print("G ");
				else if (c == Color.CYAN)
					System.out.println("C ");
				else if (c == Color.LIGHT_GRAY)
					System.out.print("Y ");
				else if (c == Color.MAGENTA)
					System.out.print("M ");
				else
					System.out.print(". ");
			
			}
			
			System.out.println();
			
		}
		
		System.out.println();
		
	}
	
}
