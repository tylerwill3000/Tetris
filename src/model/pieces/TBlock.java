package model.pieces;

import java.awt.Color;

// TBlock:
// .X.
// XXX
public class TBlock extends AbstractPiece {
	
	private static final int START_ROW = 1;
	
	private final static int[][][] orientationMap = {
		
		// Standard orientation:
		// ...
		// XXX
		// .X.
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{0,1}
		},
		
		// West orientation:
		// .X.
		// XX.
		// .X.
		{
			{-1,0},
			{-2,1},
			{-1,1},
			{0,1}
		},
		
		// North orientation:
		// .X.
		// XXX
		// ...
		{
			{-2,1},
			{-1,0},
			{-1,1},
			{-1,2}
		},
		
		// East orientation:
		// .X.
		// .XX
		// .X.
		{
			{0,1},
			{-1,1},
			{-1,2},
			{-2,1}
		}		
		
	};
	
	private final static int[][] nextPanelSquares = {
		{1,2},{2,1},{2,2},{2,3}
	};
	
	public TBlock(Color color) { super(color, START_ROW); }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][] getOrientationMap(int orientation) { return orientationMap[orientation]; }
	
}
