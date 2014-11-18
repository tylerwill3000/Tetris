
package ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class NextPiecePanel extends GridPainter {
	
	NextPiecePanel() {
		super(4,5);
		setBorder(new TitledBorder("Next Piece"));
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getNextPanelSquares(), currentPiece.getColor());
	}
	
	// Clears all squares on the panel. Used before printing a new next piece
	public void clear() {
		
		for (JPanel[] row : JPanelGrid) {
			
			for (JPanel square : row) eraseSquare(square);
			
		}
	
	}
	
}

