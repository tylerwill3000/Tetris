
package ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NextPiecePanel extends AbstractPiecePainter {
	
	public NextPiecePanel() { super(4,5); }
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getNextPanelSquares(), currentPiece.getColor());
	}
	
	public void eraseCurrentPiece() {
		eraseSquares(currentPiece.getNextPanelSquares());
	}
	
}

