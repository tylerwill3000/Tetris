package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ui.GameBoardPanel;

// The GridModel class describes the current configuration
// of placed pieces on the grid, as well as keeps track of
// scoring info / settings. It represents the data layer of
// the game
public class GameBoardModel {
	
	public static final int INITIAL_TIMER_DELAY = 600;
	
	// Represents colors on the game grid. Use a linked list since
	// rows will need to be added to the front when rows are removed
	private static LinkedList<Color[]> quilt;
	
	// Specifies point value per line depending on number of
	// lines cleared. Index is treated as number of lines
	// completed
	private static final int[] LINE_POINTS_MAP = {
		0, // Will never have 0 lines completed
		10,
		15,
		20,
		30
	};
	
	private static final int LINES_PER_LEVEL = 1;
	
	// Scoring info
	private static int linesCompleted = 0;
	private static int score = 0;
	private static int level = 1;
	
	// Flag for whether or not the player just increased level
	public static boolean justLeveled = false;
	
	// Color manipulation methods
	public static Color getColor(int row, int col) { return quilt.get(row)[col]; }
	
	public static void setColor(Color c, int row, int col) {
		
		if (row < 0) return; // Will cause out of bounds errors in cases where piece extends above top of board
		
		quilt.get(row)[col] = c;
		
	}
	
	// Scoring getters
	public static int getLinesCompleted() { return linesCompleted; }
	public static int getScore() { return score; }
	public static int getLevel() { return level; }
	
	private GameBoardModel() {}
	
	// Adds the active squares for a piece to the quilt. Used when a piece
	// is permanently placed somewhere. Returns a list of the indices of 
	// any lines removed as a result of adding this piece
	public static List<Integer> addPiece(Piece p) {
		
		// Log all colors for this piece in the quilt that are within
		// grid bounds
		for (int[] litSquare : p.getLitSquares())
			setColor(p.getColor(), litSquare[0], litSquare[1]);
		
		// See if adding the piece created any new complete lines,
		// and if so, remove them
		List<Integer> completeLines = getCompleteLines(p);
		
		if (!completeLines.isEmpty())
			removeCompleteLines(completeLines);
			
		return completeLines;
	
	}
	
	// Returns a list of completed lines. Pass the piece that
	// was just placed to narrow down the search
	private static List<Integer> getCompleteLines(Piece justPlaced) {
		
		List<Integer> completeLines = new ArrayList<Integer>();
		
		// Iterate from the bottom row of the piece's bounding
		// matrix upwards to check for complete lines
		int startRow = justPlaced.getRow();
		
		// The highest bounding matrix is 4 units (for the straight line),
		// so you'll never have to search further than that to find completed
		// lines
		for (int row = startRow; row >= 0 && row > startRow - 4; row--) {
			
			if (isCompleteLine(row)) completeLines.add(row);
			
		}
		
		return completeLines;
	}
	
	private static boolean isCompleteLine(int rowIndex) {
		
		for (Color c : quilt.get(rowIndex)) 
			if (c == null) return false;
			
		return true;
		
	}
	
	// Removes the specified completed lines from the quilt
	private static void removeCompleteLines(List<Integer> completeLines) {
		
		// Since removing a line essentially increases the row index value
		// of all lines above it by 1, removing multiple lines in sequence
		// requires an 'offset' to target the correct line. This offset
		// is increased each time a line is removed
		int offset = 0;
		for (Integer line : completeLines) {
			
			quilt.remove(line.intValue() + offset);
	
			// Add a new blank line to the top of the quilt to
			// replace the line that was just removed
			quilt.offerFirst(new Color[GameBoardPanel.H_CELLS]);
			
			offset++;
			linesCompleted++;
			
		}
		
		increaseScore(completeLines.size());
		
	}
	
	// Returns calculated timer delay based on current level
	public static int getTimerDelay() { return INITIAL_TIMER_DELAY - 55 * (level - 1); }
	
	// Increases the player's score based on how many lines
	// were cleared
	private static void increaseScore(int completedLines) {
		
		score += completedLines * LINE_POINTS_MAP[completedLines];
		
		if (linesCompleted >= level * LINES_PER_LEVEL) {
			
			AudioManager.stopCurrentSoundtrack();
			level++;
			
			// There is no looping soundtrack that is played
			// once the game is won. The winning jingle will
			// be initiated from the main fall timer listener
			if (level != 11)
				AudioManager.playCurrentSoundtrack();
			
			justLeveled = true;
			
		}
		
	}
	
	// Checks in the negative row index range are valid since the
	// piece can be rotated beyond the top border as it first
	// emerges
	public static boolean isSquareOccupied(int row, int col) { 
		
		return (row < 0 ? false : getColor(row, col) != null);
		
	}
	
	private static boolean isInBoundsSquare(int row, int col) {

		return col >= 0 && row < GameBoardPanel.V_CELLS && col < GameBoardPanel.H_CELLS;
		
	}
	
	// Checks whether the specified square is both in bounds and
	// not already occupied
	public static boolean isLegalSquare(int row, int col) {
	
		// Crucial to check in bounds before checking if the square
		// is occupied to short circuit the && - otherwise, you'll 
		// generate an index out of bounds error when checking the quilt array
		return isInBoundsSquare(row, col) && !isSquareOccupied(row, col);
		
	}
	
	// Removes all data from the quilt and resets scoring info
	public static void reset() {
		quilt = buildStartingQuilt();
		score = 0;
		linesCompleted = 0;
		level = 1;
		justLeveled = false;
	}
	
	// Builds the blank starting quilt
	private static LinkedList<Color[]> buildStartingQuilt() {

		LinkedList<Color[]> quilt = new LinkedList<Color[]>();
		
		for (int i = 0; i < GameBoardPanel.V_CELLS; i++) 
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
				else
					System.out.print(". ");
			
			}
			
			System.out.println();
			
		}
		
		System.out.println();
		
	}
	
}
