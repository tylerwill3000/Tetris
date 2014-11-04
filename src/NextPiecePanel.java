
import java.awt.GridLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NextPiecePanel extends AbstractPiecePainter {
	
	public NextPiecePanel() {
		super(4,5);
		
		setLayout(new GridLayout(4,5));
		
		// Add all JPanels to the panel to
		// create the actual grid
		for (JPanel[] row : JPanelGrid)
			for (JPanel p : row)
				add(p);
		
	}
	
	public void paintCurrentPiece() {
		paintSquares(currentPiece.getNextPanelSquares(), currentPiece.getColor());
	}
	
	public void eraseCurrentPiece() {
		eraseSquares(currentPiece.getNextPanelSquares());
		
	}
	
}

