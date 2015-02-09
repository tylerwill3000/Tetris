package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.Border;

import ui.secondaryWindows.BlockStylesFrame;
import model.Piece;

/**
 * Objects of this class are grid-based panels that are "paintable" - that is, they are able
 * to have certain cells filled in a certain color. Each instance has a currently active piece
 * associated with it 
 * @author Tyler
 */
public class GridPainter extends JPanel {
	
	/**
	 *  Pixel side length of each panel square
	 */
	static final int SQUARE_SIDE_LENGTH = 35;
	
	/**
	 *  Holds all the JPanel objects for the panel
	 */
	protected JPanel[][] _JPanelGrid;
	
	/**
	 *  Package-private so other GUI elements can manipulate it
	 */
	Piece _currentPiece;
	
	protected GridPainter(int rows, int cols) {
		
		// Set the grid layout and the matrix that
		// represents it
		setLayout(new GridLayout(rows, cols));
		_JPanelGrid = new JPanel[rows][cols];
		
		// Add all the initial panels to both the piece painter
		// panel and the JPanel matrix
		for (int i = 0; i < _JPanelGrid.length; i++) {
			
			for (int j = 0; j < _JPanelGrid[i].length; j++) {
				
				JPanel p = new JPanel();
				_JPanelGrid[i][j] = p;
				add(p);				
				
			}
			
		}
		
	}
	
	/**
	 *  Paints any given individual square the specified color.
	 * @param row Row of the square to paint
	 * @param col Column of the square to paint
	 * @param color Color to paint the square
	 * @param Border The border style for the square
	 */
	protected void paintSquare(int row, int col, Color color, Border border) {
		
		// -3 to account for the 3 invisible rows at the top of
		// the board if this is the game board
		if (this instanceof GameBoardPanel) row -= 3;
		
		if (row < 0) return;
		
		JPanel toPaint = _JPanelGrid[row][col];
		
		toPaint.setBackground(color);
		toPaint.setBorder(border);
		
	}
	
	/**
	 * Paints square using default game border setting
	 */
	protected void paintSquare(int row, int col, Color color) {
		paintSquare(row, col, color, BlockStylesFrame.getCurrentPieceBorder());
	}
	
	/**
	 *  Erases the color in any given square.
	 * @param row Row of the square to erase
	 * @param col Column of the square to erase
	 */
	protected void eraseSquare(int row, int col) {
		
		// -3 to account for the 3 invisible rows at the top of
		// the board if this is the game board
		if (this instanceof GameBoardPanel) row -= 3;
		
		if (row < 0) return;
		
		JPanel p = _JPanelGrid[row][col];
		
		p.setBackground(null);
		p.setBorder(null);
	}
	
	/**
	 * Paints the squares specified by the list given in the
	 * specified color
	 */
	protected void paintSquares(int[][] squares, Color color) {
		
		// Can happen if you're trying to paint the ghost
		// piece but the piece is already at the ghost position
		if (squares == null) return;
		
		for (int[] square : squares)
			paintSquare(square[0], square[1], color);
		
	}
	
	/**
	 *  Erases the squares specified by the list given
	 */
	protected void eraseSquares(int[][] squares) {
		
		if (squares == null) return;
		
		for (int[] square : squares)
			eraseSquare(square[0], square[1]);
		
	}
	
}
