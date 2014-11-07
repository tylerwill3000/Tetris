
package model.pieces;

import java.awt.Color;

public class Box extends AbstractPiece {
	
	private static final int START_ROW = 1;
	
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
	
	public Box(Color color) { super(color, START_ROW); }

	public int[][] getNextPanelSquares() { return nextPanelSquares; }

	public int[][] getOrientationMap(int orientation) { return orientationMap[orientation]; }
	
	// Overridden, since the box can always be rotated
	public boolean canRotate(int orientationShift) { return true; }
	
}
