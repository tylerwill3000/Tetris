
package ui;

import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class NextPiecePanel extends AbstractPiecePainter {
	
	NextPiecePanel() {
		super(4,5);
		setBorder(new TitledBorder("Next Piece"));
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getNextPanelSquares(), currentPiece.getColor());
	}
	
	public void eraseCurrentPiece() {
		
		// This is called every time the start button is pressed, so
		// on the first run through the game current piece will be null
		if (currentPiece == null) return;
		
		eraseSquares(currentPiece.getNextPanelSquares());
	}
	
}

