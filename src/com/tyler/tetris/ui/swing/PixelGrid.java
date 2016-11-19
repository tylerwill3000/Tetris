package com.tyler.tetris.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JPanel;

import com.tyler.tetris.Block.ColoredSquare;

/**
 * Objects of this class are grid-based panels that are "paintable" - that is, they are able
 * to have certain cells filled in a certain color
 * @author Tyler
 */
public abstract class PixelGrid extends JPanel {
	
	protected int rows;
	protected int columns;
	
	public PixelGrid(int rows, int columns, int pixelDimension) {
		this.rows = rows;
		this.columns = columns;
		setPreferredSize(new Dimension(columns * pixelDimension, rows * pixelDimension));
	}

	/**
	 * Re-renders and updates the display of this grid according to the current state of its color model
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (ColoredSquare sq : getCurrentColors()) {
			int squareX = getXCoordinate(sq);
			int squareY = getYCoordinate(sq);
			paintSquare(g, sq.getColor(), squareX, squareY);
		}
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	protected int getUnitWidth() {
		return getWidth() / columns;
	}
	
	protected int getUnitHeight() {
		return getHeight() / rows;
	}
	
	protected int getXCoordinate(ColoredSquare sq) {
		return sq.getColumn() * getUnitWidth();
	}
	
	protected int getYCoordinate(ColoredSquare sq) {
		return sq.getRow() * getUnitHeight();
	}
	
	protected void paintSquare(Graphics g, Color color, int x, int y) {
		if (color != null) {
			g.setColor(color);
			g.fill3DRect(x, y, getUnitWidth(), getUnitHeight(), true);
		}
		else {
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(x, y, getUnitWidth(), getUnitHeight());
		}
	}
	
	@Override
	public String toString() {
		return String.format("PixelGrid { rows=%d, columns=%d, unitWidth=%d, unitHeight=%d }", rows, columns, getUnitWidth(), getUnitHeight());
	}
	
	public abstract Collection<ColoredSquare> getCurrentColors();
	
}
