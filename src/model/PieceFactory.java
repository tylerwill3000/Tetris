package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// Used to churn out new pieces at random off a virtual 'conveyor belt'
public final class PieceFactory {
	
	// Each piece is assigned an integer ID value from 0 to 6
	private static List<Integer> gamePieceIDs = initGamePieceIDs();
	
	private static LinkedList<Piece> conveyorBelt = initConveyorBelt();
	
	// Pops the first piece off the conveyor belt and adds a
	// new one to replace it
	public static Piece receiveNextPiece() {
		
		conveyorBelt.offer(generate());
		Piece nextPiece = conveyorBelt.poll();
		
		// These are dynamic, since piece might have to be
		// shifted upwards a couple squares
		nextPiece.setInitialSquares();
		
		return nextPiece;
		
	}
	
	// Peeks at the next piece. Used to determine what to
	// display in the 'next piece' panel
	public static Piece peekAtNextPiece() {
		return conveyorBelt.peek();		
	}
	
	// Returns a random number within the specified range
	private static int randInRange(int min, int max) {
		
		return (int)(Math.random() * (max - min + 1)) + min;
		
	}
	
	// Generates a random Piece object
	private static Piece generate() {
		
		Collections.shuffle(gamePieceIDs);
		int id = gamePieceIDs.get(0);
		
		return new Piece(
			getRandomColor(),
			Ingredients.ORIENTATION_MAPS[id],
			Ingredients.NEXT_PANEL_SQUARES[id],
			Ingredients.START_ROWS[id]
		);
		
	}
	
	public static Color getRandomColor() {
	
		switch (randInRange(1,8)) {

			case 1: return new Color(255, 30, 0); // Red
			case 2: return Color.ORANGE;
			case 3: return Color.YELLOW;
			case 4: return Color.GREEN;
			case 5: return new Color(0, 200, 200); // Blue-green
			case 6: return new Color(0, 70, 255); // Blue
			case 7: return new Color(170, 45, 255); // Purple
			case 8: return Color.PINK;
			default: return null;
		
		}
		
	}
	
	// Builds the initial piece conveyor belt with 2 pieces
	private static LinkedList<Piece> initConveyorBelt() {
		
		LinkedList<Piece> belt = new LinkedList<Piece>();
		
		for (int i = 1; i <= 2; i++) belt.offer(generate());
		
		return belt;
		
	}
	
	private static List<Integer> initGamePieceIDs() {
		
		List<Integer> IDs = new ArrayList<>();
	
		// 7 original starting pieces
		for (int id = 0; id <= 8; id++) IDs.add(id);
		
		return IDs;
		
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
	
	/** Corner-block
	 * X.
	 * XX
	 */
	private final static int[][][] CORNER_BLOCK_ORIENTATIONS = {
		
		// Southwest orientation:
		// X.
		// XX
		{
			{0,0},
			{0,1},
			{-1,0}
		},
		
		// Northwest orientation:
		// XX
		// X.
		{
			{0,0},
			{-1,0},
			{-1,1}
		},
		
		// Northeast orientation:
		// XX
		// .X
		{
			{-1,0},
			{-1,1},
			{0,1}
		},
		
		// Southeast orientation:
		// .X
		// XX
		{
			{0,0},
			{0,1},
			{-1,1}
		}				
		
	};
	
	/** Twin-pillars block:
	 * ...
	 * X.X
	 * X.X
	 */
	private final static int[][][] TWIN_PILLARS_BLOCK_ORIENTATIONS = {
		
		// Standard orientation:
		// ...
		// X.X
		// X.X
		{
			{0,0},
			{-1,0},
			{-1,2},
			{0,2}
		},
		
		// Horizontal orientation:
		// XX.
		// ...
		// XX.
		{
			{-2,0},
			{-2,1},
			{0,0},
			{0,1}
		},
		
		// Other two orientations are the same, so cycle through them again
		{
			{0,0},
			{-1,0},
			{-1,2},
			{0,2}
		},
		
		{
			{-2,0},
			{-2,1},
			{0,0},
			{0,1}
		},				
		
	};
	
	
	/** From here on out, it is critically important that the data
	 *  in each collection is added in the same exact order. For
	 *  example, since the data for the "Box" piece is the first element
	 *  in one collection ,it must also be the first element in all
	 *  other collections. This allows me to only have to use a single integer
	 *  index to gather all necessary piece information
	 */
	
	// A collection of all the maps declared above
	public final static int[][][][] ORIENTATION_MAPS = {
		BOX_ORIENTATIONS,
		L_BLOCK_L_ORIENTATIONS,
		L_BLOCK_R_ORIENTATIONS,
		S_BLOCK_L_ORIENTATIONS,
		S_BLOCK_R_ORIENTATIONS,
		STRAIGHT_LINE_ORIENTATIONS,
		T_BLOCK_ORIENTATIONS,
		CORNER_BLOCK_ORIENTATIONS,
		TWIN_PILLARS_BLOCK_ORIENTATIONS
	};
	
	// Starting row of this piece on the board. Corresponds to
	// bottom left corner of the piece's bounding grid. When analyzing
	// the starting rows of each piece, it is important to keep in mind
	// that each starting row is offset by +3 rows to account for the
	// 3 invisible rows at the top of the board. In other words, row 3 is
	// equivalent to row 0 of the visible squares
	public final static int[] START_ROWS = {
		4, // Box
		5, // L-block L
		5, // L-block R
		4, // S-block L
		4, // S-block R
		4, // Straight line
		4, // T-block
		4, // Corner block
		4, // Twin-pillars block
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
		{  {1,1},{1,2},{1,3},{2,2}  }, // T-block
		{  {1,1},{2,1},{2,2} }, // Corner block
		{  {1,1},{2,1},{1,3},{2,3}  }
	};
	
};