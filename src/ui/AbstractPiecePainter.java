package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import model.Piece;

// Objects of this class are grid-based panels that are 
// "paintable" - that is, they are able to have certain
// cells filled in a certain color. The class is abstract
// since the two subclasses, the main game board grid
// and the 'next piece' grid, each have separate implementations
// of the paint / erase current piece methods
@SuppressWarnings("serial")
public abstract class AbstractPiecePainter extends JPanel {
	
	// Holds all the JPanel objects for the panel
	protected JPanel[][] JPanelGrid;
	
	// Current active piece on the panel that needs to be
	// painted. Package-private so other UI elements can
	// manipulate it
	Piece currentPiece;
	
	protected AbstractPiecePainter(int rows, int cols) {
		
		// Set the grid layout and the matrix that
		// represents it
		setLayout(new GridLayout(rows, cols));
		JPanelGrid = new JPanel[rows][cols];
		
		// Add all the initial panels to both the piece painter
		// panel and the JPanel matrix
		for (int i = 0; i < JPanelGrid.length; i++) {
			
			for (int j = 0; j < JPanelGrid[i].length; j++) {
				
				JPanel p = new JPanel();
				JPanelGrid[i][j] = p;
				add(p);				
				
			}
			
		}
		
	}
	
	// Paints the squares specified by the list given in the
	// specified color
	protected void paintSquares(int[][] squares, Color color) {
		
		// Can happen if you're trying to paint the ghost
		// piece but the piece is already at the ghost position
		if (squares == null) return;
		
		for (int[] square : squares) {
			
			// Prevent invisible squares off the top edge of the
			// board from trying to be printed. This would only
			// happen if the piece is immediately rotated right
			// after it emerges
			if (square[0] < 0) continue;
			
			JPanel panel = JPanelGrid[square[0]][square[1]];
			panel.setBackground(color);
			panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
				
		}
		
	}
	
	// Erases the squares specified by the list given
	protected void eraseSquares(int[][] squares) {
		
		if (squares == null) return;
		
		for (int[] square : squares) {
			
			if (square[0] < 0) continue;
			
			nullifyPanel(JPanelGrid[square[0]][square[1]]);
			
		}
		
	}
	
	// Removes the color and border from a panel
	protected void nullifyPanel(JPanel p) {
		p.setBackground(null);
		p.setBorder(null);
	}
	
	// Abstract methods that will receive concrete implementations
	// in both the game grid panel and the next piece panel classes
	abstract void paintCurrentPiece();
	abstract void eraseCurrentPiece();
	
}
