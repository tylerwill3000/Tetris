
package ui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class NextPiecePanel extends GridPainter {
	
	NextPiecePanel(String title) {
		super(4,5);
		setBorder(new TitledBorder(title));
		setFocusable(false);
		setPreferredSize(new Dimension(GameFrame.INFO_PANEL_WIDTH, 130));
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

