


import java.awt.Color;

// StraightLine:
// .X..
// .X..
// .X..
// .X..
public class StraightLine extends AbstractPiece {
	
	private final static int[][][] orientationMap = {
		
		// Standard orientation:
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
		
		// Level orientation:
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
		
		// Other 2 orientations are the same as the first 2, so
		// just cycle through them
		{
			{0,1},
			{-1,1},
			{-2,1},
			{-3,1}
		},
		
		{
			{0,0},
			{0,1},
			{0,2},
			{0,3}
		},
		
	};
	
	public final static int[][] nextPanelSquares = {
		{0,2},{1,2},{2,2},{3,2}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET+1},{4, GameBoardPanel.CENTER_OFFSET+1}
	};
	
	public StraightLine(Color color) { super(color); }

	protected int[][] getInitialSquares() { return initialSquares; }

	protected int[][] getNextPanelSquares() { return nextPanelSquares; }

	protected int[][][] getOrientationMap() { return orientationMap; }
}
