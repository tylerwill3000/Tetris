


import java.awt.Color;

// SBlockL:
// XX.
// .XX
public class SBlockL extends AbstractPiece {
	
	public static final int[][][] orientationMap = {
		
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
	
	public final static int[][] nextPanelSquares = {
		{1,1},{1,2},{2,2},{2,3}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET+1},{4, GameBoardPanel.CENTER_OFFSET+2}
	};
	
	public SBlockL(Color color) { super(color); }

	protected int[][] getInitialSquares() { return initialSquares; }

	protected int[][] getNextPanelSquares() { return nextPanelSquares; }

	protected int[][][] getOrientationMap() { return orientationMap; }
	
}
