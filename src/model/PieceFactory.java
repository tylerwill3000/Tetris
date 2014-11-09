
package model;

import java.awt.Color;
import java.util.LinkedList;

// Used to churn out new pieces at random
public final class PieceFactory {
	
	private static LinkedList<Piece> conveyorBelt = initConveyorBelt();
	
	// Pops the first piece off the conveyor belt and adds a
	// new one to replace it
	public static Piece receiveNextPiece() {
		conveyorBelt.offer(generate());
		Piece toReturn = conveyorBelt.poll();
		toReturn.setInitialSquares();
		return toReturn;
	}
	
	// Peeks at the next piece. Used to determine what to
	// display in the next box
	public static Piece peekAtNextPiece() {
		return conveyorBelt.peek();		
	}
	
	private static Piece generate() {
		
		// Generate a random number that will
		// determine which index to use to get
		// the piece "ingredients"
		int index = randInRange(0,6);
		
		return new Piece(
			getRandomColor(),
			Ingredients.ORIENTATION_MAPS[index],
			Ingredients.NEXT_PANEL_SQUARES[index],
			Ingredients.START_ROWS[index]
		);
		
	}
	
	// Returns random number in specified range
	private static int randInRange(int min, int max) {
		
		return (int)(Math.random() * (max - min + 1)) + min;
		
	}
	
	public static Color getRandomColor() {
	
		switch (randInRange(1,7)) {
			
			case 1: return Color.RED;
			case 2: return Color.ORANGE;
			case 3: return Color.YELLOW;
			case 4: return Color.GREEN;
			case 5: return Color.BLUE;
			case 6: return Color.CYAN;	
			case 7: return Color.PINK;
		
		}
		
		return null;
		
	}
	
	private static LinkedList<Piece> initConveyorBelt() {
		
		LinkedList<Piece> belt = new LinkedList<Piece>();
		
		for (int i = 1; i <= 2; i++) belt.offer(generate());
		
		return belt;
		
	}
	
}

// Holds all the data necessary to construct a piece
class Ingredients {
	
	/** Box:
	 * 
	 *  XX
	 *  XX
	 */
	private final static int[][][] BOX_ORIENTATIONS = {
		
		// All the same configurations:
		// XX
		// XX
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}}
			
	};
	
	/** L-block L:
	 * .X
	 * .X
	 * XX
	 */
	private final static int[][][] L_BLOCK_L_ORIENTATIONS = {
		
		// Standard orientation:
		// X..
		// XXX
		// ...
		{
			{-2,0},
			{-1,0},
			{-1,1},
			{-1,2}
		},
		
		// North orientation:
		// .XX
		// .X.
		// .X.
		{
			{-2,1},
			{-2,2},
			{-1,1},
			{0,1}
		},
		
		// East orientation:
		// ...
		// XXX
		// ..X
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{0,2}
		},
		
		// South orientation:
		// .X.
		// .X.
		// XX.
		{
			{0,0},
			{0,1},
			{-1,1},
			{-2,1}
		}
		
	};
	
	/** L-block R:
	 *  X.
	 *  X.
	 *  XX
	 */
	private final static int[][][] L_BLOCK_R_ORIENTATIONS = {
			
		// Standard Orientation:
		// ..X
		// XXX
		// ...
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{-2,2}
		},
		
		// South orientation:
		// .X.
		// .X.
		// .XX
		{
			{0,1},
			{0,2},
			{-1,1},
			{-2,1}
		},
		
		// West orientation:
		// ...
		// XXX
		// X..
		{
			{0,0},
			{-1,0},
			{-1,1},
			{-1,2}
		},
		
		// North orientation:
		// XX.
		// .X.
		// .X.
		{
			{-2,0},
			{-2,1},
			{-1,1},
			{0,1}
		},
		
	};
	
	/** S-block L:
	 * XX.
	 * .XX
	 */
	private final static int[][][] S_BLOCK_L_ORIENTATIONS = {
			
		// Standard orientation:
		// ...
		// XX
		// .XX
		{
			{-1,0},
			{-1,1},
			{0,1},
			{0,2}
		},
		
		// North orientation:
		// .X.
		// XX.
		// X..
		{
			{0,0},
			{-1,0},
			{-1,1},
			{-2,1}
		},
		
		// Other 2 orientations are the same as the first 2, so
		// just cycle through them
		{
			{-1,0},
			{-1,1},
			{0,1},
			{0,2}
		},
		
		{
			{0,0},
			{-1,0},
			{-1,1},
			{-2,1}
		}
		
	};
	
	/** S-block R:
	 *  .XX
	 *  XX.
	 */
	private final static int[][][] S_BLOCK_R_ORIENTATIONS = {
			
		// Standard orientation:
		// ...
		// .XX
		// XX.
		{
			{0,0},
			{0,1},
			{-1,1},
			{-1,2}
		},
		
		// North orientation:
		// .X.
		// .XX
		// ..X
		{
			{-2,1},
			{-1,1},
			{-1,2},
			{0,2}
		},
		
		// Other 2 orientations are the same as the first 2, so
		// just cycle through them
		{
			{0,0},
			{0,1},
			{-1,1},
			{-1,2}
		},
		
		{
			{-2,1},
			{-1,1},
			{-1,2},
			{0,2}
		}
		
	};
		
	/** Straight Line:
	 *  .X.
	 *  .X.
	 *  .X.
	 *  .X.
	 */
	private final static int[][][] STRAIGHT_LINE_ORIENTATIONS = {
			
		// Standard orientation:
		// ....
		// ....
		// XXXX
		// ....
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{-1,3}
		},
		
		// Vertical orientation:
		// .X..
		// .X..
		// .X..
		// .X..
		{
			{0,1},
			{-1,1},
			{-2,1},
			{-3,1}
		},
		
		// Other 2 orientations are the same as the first 2, so
		// just cycle through them
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{-1,3}
		},
		
		{
			{0,1},
			{-1,1},
			{-2,1},
			{-3,1}
		}
	
	};
	
	/** T-block:
	 * XXX
	 * .X.
	 */
	private final static int[][][] T_BLOCK_ORIENTATIONS = {
		
		// Standard orientation:
		// ...
		// XXX
		// .X.
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{0,1}
		},
		
		// West orientation:
		// .X.
		// XX.
		// .X.
		{
			{-1,0},
			{-2,1},
			{-1,1},
			{0,1}
		},
		
		// North orientation:
		// .X.
		// XXX
		// ...
		{
			{-2,1},
			{-1,0},
			{-1,1},
			{-1,2}
		},
		
		// East orientation:
		// .X.
		// .XX
		// .X.
		{
			{0,1},
			{-1,1},
			{-1,2},
			{-2,1}
		}		
		
	};
	
	/** From here on out, it is critically important that the data
	 *  in each collection is added in the same exact order. For
	 *  example, since the data for the "Box" piece is the first element
	 *  in one collection ,it must also be the first element in all
	 *  other collections. This allows me to use be able to use only 1
	 *  index to gather all necessary piece information
	 */
	
	// Essentially a collection of all the maps declared above
	public final static int[][][][] ORIENTATION_MAPS = {
		BOX_ORIENTATIONS,
		L_BLOCK_L_ORIENTATIONS,
		L_BLOCK_R_ORIENTATIONS,
		S_BLOCK_L_ORIENTATIONS,
		S_BLOCK_R_ORIENTATIONS,
		STRAIGHT_LINE_ORIENTATIONS,
		T_BLOCK_ORIENTATIONS
	};
	
	// Starting row of this piece on the board. Corresponds to
	// bottom left corner of the piece's bounding grid
	public final static int[] START_ROWS = {
		1, // Box
		2, // L-block L
		2, // L-block R
		1, // S-block L
		1, // S-block R
		1, // Straight line
		1 // T-block
	};
	
	// List of grid coordinate squares the piece occupies when in
	// the 'Next Piece' panel
	public final static int[][][] NEXT_PANEL_SQUARES = {
		{  {1,1},{1,2},{2,1},{2,2}  }, // Box
		{  {1,1},{2,1},{2,2},{2,3}  }, // L-block L
		{  {1,3},{2,3},{2,2},{2,1}  }, // L-block R
		{  {1,1},{1,2},{2,2},{2,3}  }, // S-block L
		{  {1,2},{1,3},{2,1},{2,2}  }, // S-block R
		{  {0,2},{1,2},{2,2},{3,2}  }, // Straight line
		{  {1,1},{1,2},{1,3},{2,2}  } // T-block
	};
	
};
