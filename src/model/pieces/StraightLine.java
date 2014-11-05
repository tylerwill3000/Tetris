package model.pieces;



import java.awt.Color;

import ui.GameBoardPanel;

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
	
	private static final int[][] initialSquares = {
		{START_ROW, GameBoardPanel.CENTER_OFFSET},
		{START_ROW, GameBoardPanel.CENTER_OFFSET+1},
		{START_ROW, GameBoardPanel.CENTER_OFFSET+2},
		{START_ROW, GameBoardPanel.CENTER_OFFSET+3},
	};
	
	public StraightLine(Color color) { super(color, START_ROW); }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][][] getOrientationMap() { return orientationMap; }
}
