package com.tyler.tetris.model;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

/**
 *  Data-layer class that represents blocks and provides an interface for changing their position and orientation
 * @author Tyler
 */
public class Block {
	
	private BlockType blockType;
	
	/**
	 * Location of piece on the game board. Corresponds to the location of the
	 * bottom left corner of the bounding grid.
	 */
	private int[] location;
	
	/**
	 * Used to index into the orientation map
	 */
	private int orientation;
	
	/**
	 *  This flag is necessary since released hold pieces must be placed - they
	 * can not be re-held. This is to prevent manual shifting of the piece conveyor belt
	 */
	private boolean isHoldPiece;
	
	Block(BlockType pieceType) {
		this.blockType = pieceType;
		this.isHoldPiece = false;
		this.orientation = 0;
	}
	
	// Package private for a reason-should only get called by board model
	void setLocation(int row, int col) {
		this.location = new int[]{row, col};
	}
	
	public int getRow() {
		return location[0];
	}
	
	public int getCol() {
		return location[1];
	}
	
	public Color getColor() {
		return blockType.getColor();
	}
	
	public List<ColoredSquare> getOccupiedSquares() {
		return blockType.calcOccupiedSquares(orientation, location[0], location[1]);
	}
	
	public List<ColoredSquare> getNextPanelSquares() {
		return blockType.getNextPanelSquares();
	}
	
	/* Mutators are package private since they should only be called from piece board model */
	
	void move(int rowMove, int colMove) {
		location[0] += rowMove;
		location[1] += colMove;
	}
	
	void rotate(int rotation) {
		orientation += rotation;
		if (orientation == 4) {
			orientation = 0;
		}
		else if (orientation == -1) {
			orientation = 3;
		}
	}
	
	public void tagAsHoldBlock() {
		isHoldPiece = true;
	}
	
	public boolean isHoldBlock() {
		return isHoldPiece;
	}
	
	public BlockType getType() {
		return blockType;
	}
	
	public static class ColoredSquare {
		
		private Color color;
		private int row, col;
		
		public ColoredSquare(int row, int col) {
			this(null, row, col);
		}
		
		public ColoredSquare(Color color, int row, int col) {
			this.color = color;
			this.row = row;
			this.col = col;
		}

		public Color getColor() {
			return color;
		}
		
		public void setColor(Color color) {
			this.color = color;
		}
		
		public int getRow() {
			return row;
		}
		
		public void setRow(int row) {
			this.row = row;
		}
		
		public int getCol() {
			return col;
		}
		
		public void setCol(int col) {
			this.col = col;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(row, col);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ColoredSquare) {
				ColoredSquare other = (ColoredSquare) o;
				return row == other.row && col == other.col;
			}
			return false;
		}
		
	}
	
}
