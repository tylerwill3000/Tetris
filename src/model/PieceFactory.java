package model;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// Used to churn out new pieces at random off a virtual 'conveyor belt'
public final class PieceFactory {
	
	public enum PieceType {
		
		BOX(
			
			// Orientations. All the same for each direction
			// XX
			// XX
			new int[][][]{ 					
				{ {0,0},{-1,0},{-1,1},{0,1} },
				{ {0,0},{-1,0},{-1,1},{0,1} },
				{ {0,0},{-1,0},{-1,1},{0,1} },
				{ {0,0},{-1,0},{-1,1},{0,1} }						
			},
			
			// Next panel squares
			new int[][]{ {1,1},{1,2},{2,1},{2,2} },
			
			// Start row
			4,
			
			// Color
			new Color(0, 70, 255)
			
		),
		
		L_BLOCK_L(
			
			// Orientations
			new int[][][]{
				
				// X..
				// XXX
				// ...
				{ {-2,0},{-1,0},{-1,1},{-1,2} },
				
				// .XX
				// .X.
				// .X.
				{ {-2,1},{-2,2},{-1,1},{0,1} },
				
				// ...
				// XXX
				// ..X
				{ {-1,0},{-1,1},{-1,2},{0,2} },
				
				// .X.
				// .X.
				// XX.
				{ {0,0},{0,1},{-1,1},{-2,1} }
				
			},
			
			// Next panel squares
			new int[][]{ {1,1},{2,1},{2,2},{2,3} },
			
			// Start row
			5,
			
			// Color
			Color.YELLOW			
				
		),
		
		L_BLOCK_R(
				
			// Orientations
			new int[][][]{
				
				// ..X
				// XXX
				// ...
				{ {-1,0},{-1,1},{-1,2},{-2,2} },
				
				// .X.
				// .X.
				// .XX
				{ {0,1},{0,2},{-1,1},{-2,1} },
				
				// ...
				// XXX
				// X..
				{ {0,0},{-1,0},{-1,1},{-1,2} },
				
				// XX.
				// .X.
				// .X.
				{ {-2,0},{-2,1},{-1,1},{0,1} },
				
			},
			
			// Next panel squares
			new int[][]{ {1,3},{2,3},{2,2},{2,1} },
			
			// Start row
			5,
			
			// Color
			Color.PINK
				
		),
		
		S_BLOCK_L(
				
			// Orientations
			new int[][][]{
				
				// ...
				// XX.
				// .XX
				{ {-1,0},{-1,1},{0,1},{0,2} },
			
				// .X.
				// XX.
				// X..
				{ {0,0},{-1,0},{-1,1},{-2,1} },
			
				// Other 2 orientations are the same as the first 2, so
				// just cycle through them
				{ {-1,0},{-1,1},{0,1},{0,2} },
				{ {0,0},{-1,0},{-1,1},{-2,1} }
			
			},
			
			// Next panel squares
			new int[][]{ {1,1},{1,2},{2,2},{2,3} },
			
			// Start row
			4,
			
			// Color
			Color.GREEN
	
		),
		
		S_BLOCK_R(
			
			// Orientations
			new int[][][]{
				
				// ...
				// .XX
				// XX.
				{ {0,0},{0,1},{-1,1},{-1,2} },
				
				// .X.
				// .XX
				// ..X
				{ {-2,1},{-1,1},{-1,2},{0,2} },
				
				// Other 2 orientations are the same as the first 2, so
				// just cycle through them
				{ {0,0},{0,1},{-1,1},{-1,2} },
				{ {-2,1},{-1,1},{-1,2},{0,2} }
				
			},
			
			// Next panel squares
			new int[][]{ {1,2},{1,3},{2,1},{2,2} },
			
			// Start row
			4,
			
			// Color
			new Color(170, 45, 255) // Purple
			
		),
		
		STRAIGHT_LINE(
			
			// Orientations
			new int[][][]{
				
				// ....
				// ....
				// XXXX
				// ....
				{ {-1,0},{-1,1},{-1,2},{-1,3} },
				
				// .X..
				// .X..
				// .X..
				// .X..
				{ {0,1},{-1,1},{-2,1},{-3,1} },
				
				// Other 2 orientations are the same as the first 2, so
				// just cycle through them
				{ {-1,0},{-1,1},{-1,2},{-1,3} },				
				{ {0,1},{-1,1},{-2,1},{-3,1} }
			
			},
			
			// Next panel squares
			new int[][]{ {0,2},{1,2},{2,2},{3,2} },
			
			// Start row
			4,
			
			// Color
			new Color(0, 200, 200) // Blue-green
			
		),
		
		T_BLOCK(
			
			// Orientations
			new int[][][]{
				
				// ...
				// XXX
				// .X.
				{ {-1,0},{-1,1},{-1,2},{0,1} },
				
				// .X.
				// XX.
				// .X.
				{ {-1,0},{-2,1},{-1,1},{0,1} },
				
				// .X.
				// XXX
				// ...
				{ {-2,1},{-1,0},{-1,1},{-1,2} },
				
				// .X.
				// .XX
				// .X.
				{ {0,1},{-1,1},{-1,2},{-2,1} }		
				
			},
			
			// Next panel squares
			new int[][]{ {1,1},{1,2},{1,3},{2,2} },
			
			// Start row
			4,
			
			// Color
			new Color(255, 30, 0) // Red
				
		),
		
		TWIN_PILLARS(
				
			// Orientations
			new int[][][]{
				
				// ...
				// X.X
				// X.X
				{ {0,0},{-1,0},{-1,2},{0,2} },
				
				// XX.
				// ...
				// XX.
				{ {-2,0},{-2,1},{0,0},{0,1} },
				
				// Other two orientations are the same, so cycle through them again
				{ {0,0},{-1,0},{-1,2},{0,2} },				
				{ {-2,0},{-2,1},{0,0},{0,1} },				
				
			},
			
			// Next panel squares
			new int[][]{ {1,1},{2,1},{1,3},{2,3} },
			
			// Start row
			4,
			
			// Color
			new Color(80, 140, 45) // Forest-green
			
		),
		
		ROCKET(
			
			// Orientations
			new int[][][]{
				
				// .X.
				// .X.
				// X.X
				{ {0,0},{-1,1},{-2,1},{0,2} },
				
				// X..
				// .XX
				// X..
				{ {-2,0},{0,0},{-1,1},{-1,2} },
				
				// X.X
				// .X.
				// .X.
				{ {-2,0},{-2,2},{-1,1},{0,1} },
						
				// ..X
				// XX.
				// ..X
				{ {-1,0},{-1,1},{0,2},{-2,2} },
						
			},
			
			// Next panel squares
			new int[][]{ {3,1},{3,3},{2,2},{1,2} },
			
			// Start row
			5,
			
			// Color
			Color.ORANGE				
				
		),
		
		DIAMOND(
			
			// Orientations. All the same for each direction:
			// .X.
			// X.X
			// .X.
			new int[][][]{
				{ {0,1},{-1,0},{-1,2},{-2,1} },
				{ {0,1},{-1,0},{-1,2},{-2,1} },
				{ {0,1},{-1,0},{-1,2},{-2,1} },
				{ {0,1},{-1,0},{-1,2},{-2,1} }						
			},
			
			// Next panel squares
			new int[][]{ {1,2},{2,1},{2,3},{3,2} },
			
			// Start row
			4,
			
			// Color
			Color.LIGHT_GRAY
			
		),
		
		;
		
		private int[][][] orientations;
		private int[][] nextPanelSquares;
		private int startRow;
		private Color color;
		
		private PieceType(int[][][] orientations, int[][] nextPanelSquares, int startRow, Color color) {
			this.orientations = orientations;
			this.nextPanelSquares = nextPanelSquares;
			this.startRow = startRow;
			this.color = color;
		}
		
		@Override
		public String toString() {
			return name().charAt(0) + name().substring(1).toLowerCase().replace('_', ' ');
		}
		
		public int[][] getOrientation(int orientation) { return orientations[orientation]; }
		public int[][] getNextPanelSquares() { return nextPanelSquares; }
		public int getStartRow() { return startRow; }
		public Color getColor() { return color; }
		
		private static List<PieceType> getInitialPieces() {
			return Arrays.asList(
				L_BLOCK_L,
				L_BLOCK_R,
				S_BLOCK_L,
				S_BLOCK_R,
				STRAIGHT_LINE,
				T_BLOCK				
			);
		}
		
		public static List<PieceType> getSpecialPieces() {
			return Arrays.asList(
				TWIN_PILLARS,
				ROCKET,
				DIAMOND
			);
		}
		
	}
	
	private static Set<PieceType> activePieces = new HashSet<>(PieceType.getInitialPieces());
	
	// Load all active special pieces from properties file
	static {
		for (PieceType special : Properties.getSavedSpecialPieces()) {
			activePieces.add(special);
		}
	}
	
	// Once game is started, active piece IDs are converted to an array to make sampling easier
	private static PieceType[] arrayedActivePieceIDs;
	
	private static LinkedList<Piece> conveyorBelt; // This is initialized once the start button is clicked
	
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
	
	public static boolean isPieceActive(PieceType pieceType) {
		return activePieces.contains(pieceType);
	}
	
	// Returns a random number within the specified range
	private static int randInRange(int min, int max) {
		return (int)(Math.random() * (max - min + 1)) + min;
	}
	
	// Takes the active pieces in the set and converts them
	// to an array for this game session. I use an array since
	// it's easier to sample from to get random pieces
	public static void solidifyActivePieces() {
		arrayedActivePieceIDs = activePieces.toArray(new PieceType[activePieces.size()]);
	}
	
	// Generates a random Piece object
	private static Piece generate() {
		
		// Sample from the active piece ID array
		PieceType pieceType = arrayedActivePieceIDs[randInRange(0, arrayedActivePieceIDs.length-1)];
		
		return new Piece(pieceType);
		
	}
	
	public static boolean addActivePiece(PieceType pieceType) { return activePieces.add(pieceType); }
	public static boolean removeActivePiece(PieceType pieceType) { return activePieces.remove(pieceType); }
	
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
	
}
