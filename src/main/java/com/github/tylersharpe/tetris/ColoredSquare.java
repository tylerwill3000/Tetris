package com.github.tylersharpe.tetris;

import java.awt.*;
import java.util.Objects;

public class ColoredSquare {

  private Color color;
  private final int row, column;

  public ColoredSquare(int row, int column) {
    this(Utility.getRandomColor(), row, column);
  }

  public ColoredSquare(Color color, int row, int column) {
    this.color = color;
    this.row = row;
    this.column = column;
  }

  public Color getColor() {
    return color;
  }

  void clearColor() {
    this.color = null;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, column, color);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ColoredSquare) {
      ColoredSquare other = (ColoredSquare) o;
      return row == other.row && column == other.column && color.equals(other.color);
    }
    return false;
  }

  @Override
  public String toString() {
    return "ColoredSquare(row=" + row + ", column=" + column + ", color=" + color + ")";
  }
}
