package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.ColoredSquare;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Grid-based panels that represent a matrix of colorable squares
 */
abstract class ColorGrid extends JPanel {

    private final int rows, columns;
    private final int padding;
    private final SquareStyle squareStyle;

    ColorGrid(int rows, int columns, int cellDimension) {
        this(rows, columns, cellDimension, 0, SquareStyle.DEFAULT);
    }

    ColorGrid(int rows, int columns, int cellDimension, int padding) {
        this(rows, columns, cellDimension, padding, SquareStyle.DEFAULT);
    }

    ColorGrid(int rows, int columns, int cellDimension, int padding, SquareStyle style) {
        this.rows = rows;
        this.columns = columns;
        this.squareStyle = style;
        this.padding = padding;
        setPreferredSize(new Dimension(columns * cellDimension, rows * cellDimension));
    }

    int getRows() {
        return rows;
    }

    int getColumns() {
        return columns;
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
