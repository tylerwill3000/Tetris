package model.pieces;



import java.awt.Color;

import ui.GameBoardPanel;

// SBlockL:
// XX.
// .XX
public class SBlockL extends AbstractPiece {
	
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
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET+1},{4, GameBoardPanel.CENTER_OFFSET+2}
	};
	
	public SBlockL(Color color) { super(color); }

	public int[][] getInitialSquares() { return initialSquares; }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][][] getOrientationMap() { return orientationMap; }
	
}
