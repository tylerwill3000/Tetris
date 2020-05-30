package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.ColoredSquare;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Grid-based panels that represent a matrix of colorable squares
 */
abstract class PixelGrid extends JPanel {

  private int rows, columns;
  private int padding;
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
    return String.format("PixelGrid(rows=%d, columns=%d, padding=%d, unitWidth=%d, unitHeight=%d, squareStyle=%s)",
                                    rows,    columns,    padding,    getUnitWidth(), getUnitHeight(), squareStyle);
  }

  /**
   * Re-renders the display of this grid according to the current state of its color model
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (var square : getCurrentColors()) {
      int squareX = getXCoordinate(square) + padding;
      int squareY = getYCoordinate(square) + padding;
      int width = getUnitWidth();
      int height = getUnitHeight();

      if (square.getColor() == null) {
        squareStyle.paintGhost(g, squareX, squareY, width, height);
      } else {
        squareStyle.paintSquare(g, square.getColor(), squareX, squareY, width, height);
      }
    }
  }

  protected int getXCoordinate(ColoredSquare square) {
    return square.getColumn() * getUnitWidth();
  }

  protected int getYCoordinate(ColoredSquare square) {
    return square.getRow() * getUnitHeight();
  }

  protected int getUnitWidth() {
    return (getWidth() - padding * 2) / columns;
  }

  protected int getUnitHeight() {
    return (getHeight() - padding * 2) / rows;
  }

  /**
   * @return A collection of colored squares to paint in this grid
   */
  abstract Collection<ColoredSquare> getCurrentColors();

}
