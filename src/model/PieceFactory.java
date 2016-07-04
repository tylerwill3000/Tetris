package model;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *  Used to churn out new pieces at random off a virtual 'conveyor belt'
 * @author Tyler
 */
public final class PieceFactory {
	
	private static Set<PieceType> _activePieces = new HashSet<>(PieceType.getInitialPieces());
	
	// Once game is started, active piece IDs are converted to an array to make sampling easier
	private static PieceType[] _arrayedActivePieces;
	
	private static Queue<Piece> _conveyorBelt; // This is initialized once the start button is clicked
	
	/**
	 *  Pops the first piece off the conveyor belt and adds a new one to replace it
	 */
	public static Piece receiveNextPiece() {
		
		_conveyorBelt.offer(generate());
		Piece nextPiece = _conveyorBelt.poll();
		
		// These are dynamic, since piece might have to be
		// shifted upwards a couple squares
		nextPiece.setInitialSquares();
		
		return nextPiece;
		
	}
	
	/**
	 *  Peeks at the next piece. Used to determine what to display in the 'next piece' panel
	 */
	public static Piece peekAtNextPiece() {
		return _conveyorBelt.peek();		
	}
	
	public static boolean isPieceActive(PieceType pieceType) {
		return _activePieces.contains(pieceType);
	}
	
	// Returns a random number within the specified range
	private static int randInRange(int min, int max) {
		return (int)(Math.random() * (max - min + 1)) + min;
	}
	
	/**
	 * Takes the active pieces in the set and converts them
	 * to an array for this game session. I use an array since
	 * it's easier to sample from to get random pieces
	 */
	public static void solidifyActivePieces() {
		
		// Load all active special pieces from properties file
		for (PieceType special : Properties.getSavedSpecialPieces()) {
			_activePieces.add(special);
		}
		
		_arrayedActivePieces = _activePieces.toArray(new PieceType[_activePieces.size()]);
	}
	
	/**
	 *  Generates a random Piece object
	 */
	private static Piece generate() {
		
		// Sample from the active piece ID array
		PieceType pieceType = _arrayedActivePieces[randInRange(0, _arrayedActivePieces.length-1)];
		
		return new Piece(pieceType);
		
	}
	
	public static boolean addActivePiece(PieceType pieceType) { return _activePieces.add(pieceType); }
	public static boolean removeActivePiece(PieceType pieceType) { return _activePieces.remove(pieceType); }
	
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
	
	/**
	 *  Clears all current pieces off the conveyor belt and replaces them with 2 new ones
	 */
	public static void resetConveyorBelt() { _conveyorBelt = initConveyorBelt(); }
	
	/**
	 *  Builds the initial piece conveyor belt with 2 pieces
	 */
	private static LinkedList<Piece> initConveyorBelt() {
		
		LinkedList<Piece> belt = new LinkedList<Piece>();
		
		for (int i = 1; i <= 2; i++) belt.offer(generate());
		
		return belt;
		
	}
	
}
