package model.pieces;

import java.awt.Color;

// SBlockL:
// XX.
// .XX
public class SBlockL extends AbstractPiece {
	
	private static final int START_ROW = 1;
	
	private static final int[][][] orientationMap = {
		
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
	
	private final static int[][] nextPanelSquares = {
		{1,1},{1,2},{2,2},{2,3}
	};
	
	public SBlockL(Color color) { super(color, START_ROW); }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][] getOrientationMap(int orientation) { return orientationMap[orientation]; }
	
}
