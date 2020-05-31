package com.github.tylersharpe.tetris;

import java.util.Collection;
import java.util.Objects;

public class Block {

  private final BlockType type;
  private int row, column;
  private int orientation;
  private boolean isHoldBlock;

  public Block(BlockType type) {
    this.type = Objects.requireNonNull(type, "'type' cannot be null");
  }

  int getRow() {
    return row;
  }

  int getColumn() {
    return column;
  }

  Collection<ColoredSquare> getOccupiedSquares() {
    return type.calculateOccupiedSquares(orientation, row, column);
  }

  public Collection<ColoredSquare> getPreviewPanelSquares() {
    return type.getPreviewPanelSquares();
  }

  public void tagAsHoldBlock() {
    isHoldBlock = true;
  }

  public boolean isHoldBlock() {
    return isHoldBlock;
  }

  BlockType getType() {
    return type;
  }

  void move(int rowMove, int columnMove) {
    setLocation(row + rowMove, column + columnMove);
  }

  void setLocation(int row, int column) {
    this.row = row;
    this.column = column;
  }

  void rotate(Rotation rotation) {
    int orientationChange = rotation == Rotation.CLOCKWISE ? 1 : -1;
    orientation += orientationChange;
    if (orientation > 3) {
      orientation = 0;
    }
    if (orientation < 0) {
      orientation = 3;
    }
  }

}
