package model.pieces;

import java.awt.Color;


// LBlockR:
// .X..
// .X..
// .XX.
public class LBlockR extends AbstractPiece {
	
	private static final int START_ROW = 2;
	
	private static final int[][][] orientationMap = {
		
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
	
	private final static int[][] nextPanelSquares = {
		{1,3},{2,3},{2,2},{2,1}
	};
	
	public LBlockR(Color color) { super(color, START_ROW); }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][] getOrientationMap(int orientation) { return orientationMap[orientation]; }
	
}