

import java.awt.Color;

// LBlockR:
// .X..
// .X..
// .XX.
public class LBlockR extends AbstractPiece {
	
	public static final int[][][] orientationMap = {
		
		// Standard orientation:
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
		
		// East Orientation:
		// ..X
		// XXX
		// ...
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{-2,2}
		}
		
	};
	
	public final static int[][] nextPanelSquares = {
		{1,3},{2,3},{2,2},{2,1}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET},{4, GameBoardPanel.CENTER_OFFSET+1}
	};
	
	public LBlockR(Color color) { super(color); }

	protected int[][] getInitialSquares() { return initialSquares; }

	protected int[][] getNextPanelSquares() { return nextPanelSquares; }

	protected int[][][] getOrientationMap() { return orientationMap; }
	
}