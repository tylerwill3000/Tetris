package model;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

// Used to churn out new pieces at random off a virtual 'conveyor belt'
public final class PieceFactory {
	
	public final static int BOX_ID = 0;
	public final static int L_BLOCK_L_ID = 1;
	public final static int L_BLOCK_R_ID = 2;
	public final static int S_BLOCK_L_ID = 3;
	public final static int S_BLOCK_R_ID = 4;
	public final static int STRAIGHT_LINE_ID = 5;
	public final static int T_BLOCK_ID = 6;
	public final static int TWIN_PILLARS_BLOCK_ID = 7;
	public final static int ROCKET_BLOCK_ID = 8;
	public final static int DIAMOND_BLOCK_ID = 9;
	
	// Allows me to iterate over these values in the special blocks frame
	public final static int[] SPECIAL_BLOCK_IDS = {
		TWIN_PILLARS_BLOCK_ID,
		ROCKET_BLOCK_ID,
		DIAMOND_BLOCK_ID
	};
	
	public final static Map<Integer,Color> PIECE_COLOR_MAP = initPieceColorMap();
	private static Map<Integer,Color> initPieceColorMap() {
		Map<Integer,Color> colors = new HashMap<>();
		colors.put(BOX_ID, new Color(0, 70, 255)); // Blue
		colors.put(L_BLOCK_L_ID, Color.YELLOW);
		colors.put(L_BLOCK_R_ID, Color.ORANGE);
		colors.put(S_BLOCK_L_ID, Color.GREEN);
		colors.put(S_BLOCK_R_ID, new Color(170, 45, 255)); // Purple
		colors.put(STRAIGHT_LINE_ID, new Color(0, 200, 200)); // Blue-green
		colors.put(T_BLOCK_ID, new Color(255, 30, 0)); // Red
		colors.put(TWIN_PILLARS_BLOCK_ID, new Color(80, 140, 45)); // Dark-green
		colors.put(ROCKET_BLOCK_ID, Color.PINK);
		colors.put(DIAMOND_BLOCK_ID, Color.LIGHT_GRAY);
		return colors;
	}
	
	private static Set<Integer> activePieceIDs = initGamePieceIDs();
	
	// Once game is started, active piece IDs are converted to an array to make sampling easier
	private static Integer[] arrayedActivePieceIDs;
	
	private static LinkedList<Piece> conveyorBelt; // This is initialized once the start button is clicked
	
	// Pops the first piece off the conveyor belt and adds a
	// new one to replace it
	public static Piece receiveNextPiece() {
		
		conveyorBelt.offer(generate());
		Piece nextPiece = conveyorBelt.poll();
		
		// These are dynamic, since pie+ce might have to be
		// shifted upwards a couple squares
		nextPiece.setInitialSquares();
		
		return nextPiece;
		
	}
	
	// Peeks at the next piece. Used to determine what to
	// display in the 'next piece' panel
	public static Piece peekAtNextPiece() {
		return conveyorBelt.peek();		
	}
	
	public static boolean isPieceActive(int pieceID) {
		return activePieceIDs.contains(pieceID);
	}
	
	// Returns a random number within the specified range
	private static int randInRange(int min, int max) {
		return (int)(Math.random() * (max - min + 1)) + min;
	}
	
	// Takes the active piece IDs in the set and converts them
	// to an array for this game session. I use an array since
	// it's easier to sample from to get random pieces
	public static void solidifyActivePieces() {
		arrayedActivePieceIDs = activePieceIDs.toArray(new Integer[activePieceIDs.size()]);
	}
	
	// Generates a random Piece object
	private static Piece generate() {
		
		// Sample from the active piece ID array
		int pieceID = arrayedActivePieceIDs[randInRange(0, arrayedActivePieceIDs.length-1)];
		
		return new Piece(
			PIECE_COLOR_MAP.get(pieceID),
			Ingredients.ORIENTATION_MAPS[pieceID],
			Ingredients.NEXT_PANEL_SQUARES[pieceID],
			Ingredients.START_ROWS[pieceID]
		);
		
	}
	
	// Returns the specified piece according to the piece ID
	public static Piece order(int pieceID) {
		
		return new Piece(
			PIECE_COLOR_MAP.get(pieceID),
			Ingredients.ORIENTATION_MAPS[pieceID],
			Ingredients.NEXT_PANEL_SQUARES[pieceID],
			Ingredients.START_ROWS[pieceID]
		);
		
	}
	
	public static boolean addPieceID(int id) { return activePieceIDs.add(id); }
	public static boolean removePieceID(int id) { return activePieceIDs.remove(id); }
	
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
	
	// Clears all current pieces off the conveyor belt and replaces them with 2 new ones
	public static void resetConveyorBelt() { conveyorBelt = initConveyorBelt(); }
	
	// Builds the initial piece conveyor belt with 2 pieces
	private static LinkedList<Piece> initConveyorBelt() {
		
		LinkedList<Piece> belt = new LinkedList<Piece>();
		
		for (int i = 1; i <= 2; i++) belt.offer(generate());
		
		return belt;
		
	}
	
	// By default, all standard pieces are active (i.e. those with IDs 0-6)
	private static Set<Integer> initGamePieceIDs() {
		
		Set<Integer> IDs = new HashSet<>();
	
		// 7 original starting pieces
		for (int id = 0; id <= 6; id++) IDs.add(id);
		
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
	
	/** Rocket block:
	 * .X.
	 * .X.
	 * X.X
	 */
	private final static int[][][] ROCKET_BLOCK_ORIENTATIONS = {
		
		// Standard orientation:
		// .X.
		// .X.
		// X.X
		{
			{0,0},
			{-1,1},
			{-2,1},
			{0,2}
		},
		
		// East orientation:
		// X..
		// .XX
		// X..
		{
			{-2,0},
			{0,0},
			{-1,1},
			{-1,2}
		},
		
		// South orientation:
		// X.X
		// .X.
		// .X.
		{
			{-2,0},
			{-2,2},
			{-1,1},
			{0,1}
		},
				
		// West orientation:
		// ..X
		// XX.
		// ..X
		{
			{-1,0},
			{-1,1},
			{0,2},
			{-2,2}
		},
				
	};
	
	/* Diamond block:
	 * .X.
	 * X.X
	 * .X.
	 */
	private final static int[][][] DIAMOND_BLOCK_ORIENTATIONS = {
		
		// All same configurations:
		// .X.
		// X.X
		// .X.
		{{0,1},{-1,0},{-1,2},{-2,1}},
		{{0,1},{-1,0},{-1,2},{-2,1}},
		{{0,1},{-1,0},{-1,2},{-2,1}},
		{{0,1},{-1,0},{-1,2},{-2,1}}
				
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
		TWIN_PILLARS_BLOCK_ORIENTATIONS,
		ROCKET_BLOCK_ORIENTATIONS,
		DIAMOND_BLOCK_ORIENTATIONS
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
		5, // Rocket block
		4  // Diamond block
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
		{  {1,1},{2,1},{1,3},{2,3}  }, // Twin-pillars block
		{  {3,1},{3,3},{2,2},{1,2}  }, // Rocket block
		{  {1,2},{2,1},{2,3},{3,2}  }  // Diamond block
	};
	
};