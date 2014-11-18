package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ui.GameBoardPanel;

// The GameBoardModel class describes the current configuration
// of placed pieces on the grid It represents the data layer of
// the game
public class GameBoardModel {
	
	public static final int INITIAL_TIMER_DELAY = 600;
	
	// Amount of milliseconds the timer delay decreases each level
	private static final int TIMER_DELAY_DECREASE_RATE = 55;
	
	// Represents colors on the game grid. Use a linked list since
	// rows will need to be added to the front when rows are removed
	private static LinkedList<Color[]> qGUIlt;
	
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
	
	private static final int LINES_PER_LEVEL = 20;
	
	// Scoring info
	private static int linesCompleted = 0;
	private static int score = 0;
	private static int level = 1;
	
	// Flag for whether or not the player just increased level
	public static boolean justLeveled = false;
	
	// Color manipulation methods
	public static Color getColor(int row, int col) { return qGUIlt.get(row)[col]; }
	
	public static void setColor(Color c, int row, int col) {
		
		if (row < 0) return; // Will cause out of bounds errors in cases where piece extends above top of board
		
		qGUIlt.get(row)[col] = c;
		
	}
	
	// Returns calculated timer delay based on current level
	public static int getTimerDelay() {
		return INITIAL_TIMER_DELAY - (TIMER_DELAY_DECREASE_RATE * (level - 1));
	}
	
	// Scoring getters
	public static int getLinesCompleted() { return linesCompleted; }
	public static int getScore() { return score; }
	public static int getLevel() { return level; }
	
	private GameBoardModel() {}
	
	// Adds the active squares for a piece to the qGUIlt. Used when a piece
	// is permanently placed somewhere. Returns a list of row indices for
	// completed lines (can be empty)
	public static List<Integer> addPiece(Piece p) {
		
		// Log all colors for this piece
		for (int[] litSquare : p.getLitSquares())
			setColor(p.getColor(), litSquare[0], litSquare[1]);
		
		// Return the list of completed lines
		return getCompleteLines(p);
	
	}
	
	// Returns an integer list corresponding to the indices of rows
	// to be removed
	private static List<Integer> getCompleteLines(Piece justPlaced) {
		
		List<Integer> completeLines = new ArrayList<Integer>();
		
		// Iterate from the bottom row of the piece's bounding
		// matrix upwards to check for complete lines
		int startRow = justPlaced.getRow();
		
		// The highest bounding matrix is 4 units (for the straight line),
		// so you'll never have to search further than that to find completed
		// lines
		for (int row = startRow; row >= 0 && row > startRow - 4; row--) {
			
			if (isCompleteLine(row))
				completeLines.add(row);
			
		}
		
		return completeLines;
		
	}
	
	private static boolean isCompleteLine(int rowIndex) {
		
		for (Color c : qGUIlt.get(rowIndex)) 
			if (c == null) return false;
			
		return true;
		
	}
	
	// Removes the specified completed lines from the qGUIlt
	public static void removeCompleteLines(List<Integer> toRemove) {
		
		// Since removing a line essentially increases the row index value
		// of all lines above it by 1, removing multiple lines in sequence
		// reqGUIres an 'offset' to target the correct line. This offset
		// is increased each time a line is removed
		int offset = 0;
		for (Integer line : toRemove) {
			
			qGUIlt.remove(line.intValue() + offset);
	
			// Add a new blank line to the top of the qGUIlt to
			// replace the line that was just removed
			qGUIlt.offerFirst(new Color[GameBoardPanel.H_CELLS]);
			
			offset++;
			linesCompleted++;
			
		}
		
		// Increase the score, passing the number of lines removed
		increaseScore(toRemove.size());
		
	}
	
	// Increases the player's score based on how many lines
	// were cleared
	private static void increaseScore(int completedLines) {
		
		score += completedLines * LINE_POINTS_MAP[completedLines];
		
		// Process level ups
		while (linesCompleted >= level * LINES_PER_LEVEL) {
			
			AudioManager.stopCurrentSoundtrack();
			level++;
			
			if (level != 11)
				AudioManager.beginCurrentSoundtrack();
			
			justLeveled = true;
			
		}
		
	}
	
	// Checks in the negative row index range are valid since the
	// piece can be rotated beyond the top border as it first
	// emerges. Ternary protects against negative index checks
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
		// is occupied to short circGUIt the && - otherwise, you'll 
		// generate an index out of bounds error when checking the qGUIlt array
		return isInBoundsSquare(row, col) && !isSquareOccupied(row, col);
		
	}
	
	// Removes all data from the qGUIlt and resets scoring info
	public static void reset() {
		qGUIlt = bGUIldStartingQGUIlt();
		score = 0;
		linesCompleted = 0;
		level = 1;
	}
	
	// BGUIlds the blank starting qGUIlt
	private static LinkedList<Color[]> bGUIldStartingQGUIlt() {

		LinkedList<Color[]> qGUIlt = new LinkedList<Color[]>();
		
		for (int i = 0; i < GameBoardPanel.V_CELLS; i++) 
			qGUIlt.add(new Color[GameBoardPanel.H_CELLS]);
			
		return qGUIlt;
		
	}
	
	// For debugging
	public void print() {
		
		for (Color[] colorRow : qGUIlt) {
	
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
