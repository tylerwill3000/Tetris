package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import model.Piece;

// Objects of this class are grid-based panels that are 
// "paintable" - that is, they are able to have certain
// cells filled in a certain color. Each instance has
// a currently active piece associated with it
public class GridPainter extends JPanel {
	
	// Holds all the JPanel objects for the panel
	protected JPanel[][] JPanelGrid;
	
	// Package-private so other UI elements can manipulate it
	Piece currentPiece;
	
	protected GridPainter(int rows, int cols) {
		
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
	
	// Paints any given individual square the specified color.
	// Pass the panel object that represents the square
	protected void paintSquare(JPanel toPaint, Color color) {
		toPaint.setBackground(color);
		toPaint.setBorder(GameFrame.BEVEL_BORDER);
	}
	
	// Erases the color in any given square. Pass the panel
	// object that represents the square
	// Removes the color and border from a panel
	protected void eraseSquare(JPanel p) {
		p.setBackground(null);
		p.setBorder(null);
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
			
			paintSquare(JPanelGrid[square[0]][square[1]], color);
				
		}
		
	}
	
	// Erases the squares specified by the list given
	protected void eraseSquares(int[][] squares) {
		
		if (squares == null) return;
		
		for (int[] square : squares) {
			
			if (square[0] < 0) continue;
			
			eraseSquare(JPanelGrid[square[0]][square[1]]);
			
		}
		
	}
	
}
