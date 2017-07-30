package com.github.tylerwill.tetris.swing;

import com.github.tylerwill.tetris.Block;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/** Objects of this class are grid-based panels that represent a set of 'pixels'  */
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

  int getRows() {
    return rows;
  }

  int getColumns() {
    return columns;
  }

  @Override
  public String toString() {
    return String.format("PixelGrid(rows=%d, columns=%d, unitWidth=%d, unitHeight=%d)", rows, columns, getUnitWidth(), getUnitHeight());
  }

  /** Re-renders and updates the display of this grid according to the current state of its color model */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (Block.ColoredSquare coloredSquare : getCurrentColors()) {

      int squareX = getXCoordinate(coloredSquare) + padding;
      int squareY = getYCoordinate(coloredSquare) + padding;
      int width = getUnitWidth();
      int height = getUnitHeight();

      if (coloredSquare.getColor() == null) {
        squareStyle.paintGhost(g, squareX, squareY, width, height);
      } else {
        squareStyle.paintSquare(g, coloredSquare.getColor(), squareX, squareY, width, height);
      }
    }

  }

  protected int getUnitWidth() {
    return (getWidth() - padding * 2) / columns;
  }

  protected int getUnitHeight() {
    return (getHeight() - padding * 2) / rows;
  }

  int getXCoordinate(Block.ColoredSquare square) {
    return square.getColumn() * getUnitWidth();
  }

  int getYCoordinate(Block.ColoredSquare square) {
    return square.getRow() * getUnitHeight();
  }

  abstract Collection<Block.ColoredSquare> getCurrentColors();

}
