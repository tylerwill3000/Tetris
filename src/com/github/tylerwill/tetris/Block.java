package com.github.tylerwill.tetris;

import java.awt.*;
import java.util.Collection;
import java.util.Objects;

/**  Data-layer class that represents blocks and provides an interface for changing their position and orientation */
public class Block {

  private BlockType blockType;
  private int[] location;
  private int orientation;
  private boolean isHoldBlock;

  public Block(BlockType blockType) {
    this.blockType = blockType;
    this.isHoldBlock = false;
    this.orientation = 0;
  }

  int getRow() {
    return location[0];
  }

  int getColumn() {
    return location[1];
  }

  Collection<ColoredSquare> getOccupiedSquares() {
    return blockType.calcOccupiedSquares(orientation, location[0], location[1]);
  }

  public Collection<ColoredSquare> getNextPanelSquares() {
    return blockType.getNextPanelSquares();
  }

  public void tagAsHoldBlock() {
    isHoldBlock = true;
  }

  public boolean isHoldBlock() {
    return isHoldBlock;
  }

  BlockType getType() {
    return blockType;
  }

  void move(int rowMove, int colMove) {
    location[0] += rowMove;
    location[1] += colMove;
  }

  void setLocation(int row, int col) {
    this.location = new int[]{ row, col };
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

  public static class ColoredSquare {

    private Color color;
    private int row, col;

    public ColoredSquare(int row, int col) {
      this(BlockType.getRandomColor(), row, col);
    }

    public ColoredSquare(Color color, int row, int col) {
      this.color = color;
      this.row = row;
      this.col = col;
    }

    public Color getColor() {
      return color;
    }

    public void clearColor() {
      this.color = null;
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return col;
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
