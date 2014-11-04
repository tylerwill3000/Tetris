


import java.awt.Color;

public class Box extends AbstractPiece {
	
	public static final int[][][] orientationMap = {
		
		// All the same configurations:
		// XX
		// XX
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}},
		{{0,0}, {-1,0}, {-1,1}, {0,1}}
		
	};
	
	public final static int[][] nextPanelSquares = {
		{1,1},{1,2},{2,1},{2,2}
	};
	
	private static final int[][] initialSquares = {
		{4, GameBoardPanel.CENTER_OFFSET},{4, GameBoardPanel.CENTER_OFFSET+1}
	};
	
	public Box(Color color) { super(color); }

	protected int[][] getInitialSquares() { return initialSquares; }

	protected int[][] getNextPanelSquares() { return nextPanelSquares; }

	protected int[][][] getOrientationMap() { return orientationMap; }
	
	// Overridden, since the box can always be rotated
	public boolean canRotate(int orientationShift) { return true; }
	
}
