
package model.pieces;

import java.awt.Color;

import ui.GameBoardPanel;

public class Box extends AbstractPiece {
	
	private static final int[][][] orientationMap = {
		
		// All the same configurations:
		// XX
		// XX
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}}
		
	};
	
	private final static int[][] nextPanelSquares = {
		{1,1},{1,2},{2,1},{2,2}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET},{4, GameBoardPanel.CENTER_OFFSET+1}
	};
	
	public Box(Color color) { super(color); }

	public int[][] getInitialSquares() { return initialSquares; }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][][] getOrientationMap() { return orientationMap; }
	
	// Overridden, since the box can always be rotated
	public boolean canRotate(int orientationShift) { return true; }
	
}
