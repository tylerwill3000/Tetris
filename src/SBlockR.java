


import java.awt.Color;

// SBlockR:
// .XX
// XX.
public class SBlockR extends AbstractPiece {

	private static final int[][][] orientationMap = {
		
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
	
	private static final int[][] nextPanelSquares = {
		{1,2},{1,3},{2,1},{2,2}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET},{4, GameBoardPanel.CENTER_OFFSET+1}
	};
	
	public SBlockR(Color color) { super(color); }

	protected int[][] getInitialSquares() { return initialSquares; }

	protected int[][] getNextPanelSquares() { return nextPanelSquares; }

	protected int[][][] getOrientationMap() { return orientationMap; }

}
