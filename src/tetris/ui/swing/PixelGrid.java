package tetris.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JPanel;

import tetris.Block.ColoredSquare;

/**
 * Objects of this class are grid-based panels that are "paintable" - that is, they are able
 * to have certain cells filled in a certain color
 * @author Tyler
 */
public abstract class PixelGrid extends JPanel {
	
	protected int rows;
	protected int columns;
	protected int padding;
	private SquareStyle squareStyle;
	
	public PixelGrid(int rows, int columns, int pixelDimension) {
		this(rows, columns, pixelDimension, 0, SquareStyle.DEFAULT);
	}
	
	public PixelGrid(int rows, int columns, int pixelDimension, int padding) {
		this(rows, columns, pixelDimension, padding, SquareStyle.DEFAULT);
	}
	
	public PixelGrid(int rows, int columns, int pixelDimension, int padding, SquareStyle style) {
		this.rows = rows;
		this.columns = columns;
		this.squareStyle = style;
		this.padding = padding;
		setPreferredSize(new Dimension(columns * pixelDimension, rows * pixelDimension));
	}

	public void setSquareStyle(SquareStyle style) {
		this.squareStyle = style;
	}
	
	/**
	 * Re-renders and updates the display of this grid according to the current state of its color model
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		for (ColoredSquare sq : getCurrentColors()) {
			
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
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	protected int getUnitWidth() {
		return (getWidth() - padding * 2) / columns;
	}
	
	protected int getUnitHeight() {
		return (getHeight() - padding * 2) / rows;
	}
	
	protected int getXCoordinate(ColoredSquare sq) {
		return sq.getColumn() * getUnitWidth();
	}
	
	protected int getYCoordinate(ColoredSquare sq) {
		return sq.getRow() * getUnitHeight();
	}
	
	@Override
	public String toString() {
		return String.format("PixelGrid { rows=%d, columns=%d, unitWidth=%d, unitHeight=%d }", rows, columns, getUnitWidth(), getUnitHeight());
	}
	
	public abstract Collection<ColoredSquare> getCurrentColors();
	
}
