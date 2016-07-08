package com.tyler.tetris.ui.swing;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.tyler.tetris.model.Block;

/**
 * Objects of this class are grid-based panels that are "paintable" - that is, they are able
 * to have certain cells filled in a certain color
 * @author Tyler
 */
public abstract class GridPainter extends JPanel {
	
	static final int SQUARE_SIDE_LENGTH = 35;
	
	protected JPanel[][] panelGrid;
	private Border border;
	
	protected GridPainter(int rows, int cols) {
		
		setLayout(new GridLayout(rows, cols));
		this.border = BorderFactory.createEtchedBorder();
		
		this.panelGrid = new JPanel[rows][cols];
		for (int row = 0; row < panelGrid.length; row++) {
			for (int col = 0; col < panelGrid[row].length; col++) {
				panelGrid[row][col] = new JPanel();
			}
		}
	}
	
	public void setBorder(Border border) {
		this.border = border;
	}
	
	public boolean isInBounds(int row, int col) {
		return row < 0 || row > panelGrid.length || col < 0 || col > panelGrid[0].length;
	}
	
	/**
	 * Re-renders and updates the display of this grid according to the current state of its color model
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		getCurrentColors().forEach(sq -> {
			JPanel panel = panelGrid[sq.getRow()][sq.getCol()];
			panel.setBackground(sq.getColor());
			panel.setBorder(border);
		});
	}
	
	/**
	 * Returns the current color model of this grid
	 */
	public List<Block.ColoredSquare> getCurrentColors() {
		return new ArrayList<>();
	};
	
}
