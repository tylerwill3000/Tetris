
import java.awt.Color;
import java.util.LinkedList;

// Used to churn out new pieces at random
public final class PieceFactory {
	
	private static LinkedList<AbstractPiece> conveyorBelt = initConveyorBelt();
	
	// Pops the first piece off the conveyor belt and adds a
	// new one to replace it
	public static AbstractPiece receiveNextPiece() {
		conveyorBelt.offer(generate());
		return conveyorBelt.poll();
	}
	
	// Peeks at the next piece. Used to determine what to
	// display in the next box
	public static AbstractPiece peekAtNextPiece() {
		return conveyorBelt.peek();		
	}
	
	public static AbstractPiece generate() {
		
		// Generate a random number that will
		// determine which piece to use. Since
		// there are 7 possible pieces, the
		// number will be in the range 1-7*/
		switch (randInRange(1,7)) {
			
			// Return an S_BLOCK_R
			case 1: return new SBlockR(getRandomColor());
			
			// Return an S_BLOCK_L
			case 2: return new SBlockL(getRandomColor());
			
			// Return a STRAIGHT_LINE
			case 3: return new StraightLine(getRandomColor());
			
			// Return a T_BLOCK
			case 4: return new TBlock(getRandomColor());
			
			// Returns a L_BLOCK_R
			case 5: return new LBlockR(getRandomColor());
			
			// Return an L_BLOCK_L
			case 6: return new LBlockL(getRandomColor());
			
			// Return a BOX
			case 7: return new Box(getRandomColor());
			
		}
		
		// Makes the compiler happy
		return null;
		
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
	
	private static LinkedList<AbstractPiece> initConveyorBelt() {
		
		LinkedList<AbstractPiece> belt = new LinkedList<AbstractPiece>();
		
		for (int i = 1; i <= 2; i++) belt.offer(generate());
		
		return belt;
	}
	
}
