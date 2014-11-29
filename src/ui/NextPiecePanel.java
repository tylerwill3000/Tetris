
package ui;

import java.awt.Dimension;

import javax.swing.border.TitledBorder;

public class NextPiecePanel extends GridPainter {
	
	NextPiecePanel(String title) {
		super(4,5);
		setBorder(new TitledBorder(title));
		setFocusable(false);
		
		// Size of all info panels should be constant
		setPreferredSize(new Dimension(
			GameFrame.INFO_PANEL_WIDTH,
			
			// For some reason I need +10 to get proper size, even
			// those I want to height of the panel to be 4 squares...
			GridPainter.SQUARE_SIDE_LENGTH * 4 + 10));
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getNextPanelSquares(), currentPiece.getColor());
	}
	
	// Clears all squares on the panel. Used before printing a new next piece
	public void clear() {

		for (int row = 0; row < JPanelGrid.length; row++) {
			
			for (int col = 0; col < JPanelGrid[row].length; col++) {
			
				eraseSquare(row, col);
				
			}			
			
		}
	
	}
	
}

