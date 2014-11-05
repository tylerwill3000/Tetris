package model.pieces;


import java.awt.Color;

import ui.GameBoardPanel;

// LBLockL:
// ..X.
// ..X.
// .XX.
public class LBlockL extends AbstractPiece {
	
	private static final int[][][] orientationMap = {
		
		// Standard orientation:
		// X..
		// XXX
		// ...
		{
			{-2,0},
			{-1,0},
			{-1,1},
			{-1,2}
		},
		
		// North orientation:
		// .XX
		// .X.
		// .X.
		{
			{-2,1},
			{-2,2},
			{-1,1},
			{0,1}
		},
		
		// East orientation:
		// ...
		// XXX
		// ..X
		{
			{-1,0},
			{-1,1},
			{-1,2},
			{0,2}
		},
		
		// South orientation:
		// .X.
		// .X.
		// XX.
		{
			{0,0},
			{0,1},
			{-1,1},
			{-2,1}
		},
		
	};
	
	private final static int[][] nextPanelSquares = {
		{1,1},{2,1},{2,2},{2,3}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET},{4, GameBoardPanel.CENTER_OFFSET+1}
	};
	
	public LBlockL(Color color) { super(color); }

	public int[][] getInitialSquares() { return initialSquares; }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][][] getOrientationMap() { return orientationMap; }
	
}