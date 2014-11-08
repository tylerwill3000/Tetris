
package ui;

@SuppressWarnings("serial")
public class NextPiecePanel extends AbstractPiecePainter {
	
	public NextPiecePanel() { super(4,5); }
	
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

