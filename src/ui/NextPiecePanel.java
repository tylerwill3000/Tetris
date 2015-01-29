
package ui;

import java.awt.Dimension;

import javax.swing.border.TitledBorder;

import model.Piece;

public class NextPiecePanel extends GridPainter {
	
	// Contruct a next piece panel with the specified title and initial starting piece
	public NextPiecePanel(String title, Piece currentPiece) {
		this(title);
		this.currentPiece = currentPiece;
		paintCurrentPiece();
	}
	
	// Construct a next piece panel with just the title. Initial piece is set later
	NextPiecePanel(String title) {
		super(4,5);
		setBorder(new TitledBorder(title));
		setFocusable(false);
		
		// Size of all next piece panels should be constant
		setPreferredSize(new Dimension(
				
			GameFrame.INFO_PANEL_WIDTH,
			
			// For some reason I need +10 to get proper size, even
			// though I want to height of the panel to be 4 squares...
			GridPainter.SQUARE_SIDE_LENGTH * 4 + 10));
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getNextPanelSquares(), currentPiece.getColor());
	}
	
	// Clears all squares on the panel. Used before printing a new next piece
	public void clear() {

		for (int row = 0; row < JPanelGrid.length; row++)
			for (int col = 0; col < JPanelGrid[row].length; col++)
				eraseSquare(row, col);
	
	}

}

