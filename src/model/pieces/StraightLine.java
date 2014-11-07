package model.pieces;

import java.awt.Color;

// StraightLine:
// .X..
// .X..
// .X..
// .X..
public class StraightLine extends AbstractPiece {
	
	private static final int START_ROW = 1;
	
	private final static int[][][] orientationMap = {
		
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
	
	private final static int[][] nextPanelSquares = {
		{0,2},{1,2},{2,2},{3,2}
	};
	
	public StraightLine(Color color) { super(color, START_ROW); }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][] getOrientationMap(int orientation) { return orientationMap[orientation]; }
	
}
