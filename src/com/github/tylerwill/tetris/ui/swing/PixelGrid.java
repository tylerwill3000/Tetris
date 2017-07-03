package com.github.tylerwill.tetris.ui.swing;

import com.github.tylerwill.tetris.Block;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Objects of this class are grid-based panels that are "paintable" - that is, they are able
 * to have certain cells filled in a certain color
 * @author Tyler
 */
abstract class PixelGrid extends JPanel {
	
	protected int rows;
	protected int columns;
	protected int padding;
	private SquareStyle squareStyle;
	
	PixelGrid(int rows, int columns, int pixelDimension) {
		this(rows, columns, pixelDimension, 0, SquareStyle.DEFAULT);
	}
	
	PixelGrid(int rows, int columns, int pixelDimension, int padding) {
		this(rows, columns, pixelDimension, padding, SquareStyle.DEFAULT);
	}
	
	PixelGrid(int rows, int columns, int pixelDimension, int padding, SquareStyle style) {
		this.rows = rows;
		this.columns = columns;
		this.squareStyle = style;
		this.padding = padding;
		setPreferredSize(new Dimension(columns * pixelDimension, rows * pixelDimension));
	}

	void setSquareStyle(SquareStyle style) {
		this.squareStyle = style;
	}
	
	/**
	 * Re-renders and updates the display of this grid according to the current state of its color model
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		for (Block.ColoredSquare sq : getCurrentColors()) {
			
			int squareX = getXCoordinate(sq) + padding;
			int squareY = getYCoordinate(sq) + padding;
			int width = getUnitWidth();
			int height = getUnitHeight();
			
			if (sq.getColor() == null) {
				squareStyle.paintGhost(g, squareX, squareY, width, height);
			}
			else {
				squareStyle.paintSquare(g, sq.getColor(), squareX, squareY, width, height);
			}
			
		}
		
	}
	
	int getRows() {
		return rows;
	}
	
	int getColumns() {
		return columns;
	}
	
	int getUnitWidth() {
		return (getWidth() - padding * 2) / columns;
	}
	
	int getUnitHeight() {
		return (getHeight() - padding * 2) / rows;
	}
	
	int getXCoordinate(Block.ColoredSquare sq) {
		return sq.getColumn() * getUnitWidth();
	}
	
	int getYCoordinate(Block.ColoredSquare sq) {
		return sq.getRow() * getUnitHeight();
	}
	
	@Override
	public String toString() {
		return String.format("PixelGrid { rows=%d, columns=%d, unitWidth=%d, unitHeight=%d }", rows, columns, getUnitWidth(), getUnitHeight());
	}
	
	abstract Collection<Block.ColoredSquare> getCurrentColors();
	
}
