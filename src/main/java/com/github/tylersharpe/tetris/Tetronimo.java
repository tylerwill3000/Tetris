package com.github.tylersharpe.tetris;

import java.util.Collection;
import java.util.Objects;

public class Tetronimo {
    private final TetronimoType type;
    private int row, column;
    private int orientation;
    private boolean isHold;

    public Tetronimo(TetronimoType type) {
        this.type = Objects.requireNonNull(type, "'type' cannot be null");
    }

    int getRow() {
        return row;
    }

    Collection<ColoredSquare> calculateOccupiedSquares() {
        return type.calculateOccupiedSquares(orientation, row, column);
    }

    public Collection<ColoredSquare> getPreviewPanelSquares() {
        return type.getPreviewPanelSquares();
    }

    public void tagAsHold() {
        isHold = true;
    }

    public boolean isHold() {
        return isHold;
    }

    TetronimoType getType() {
        return type;
    }

    void move(int rowMove, int columnMove) {
        setLocation(row + rowMove, column + columnMove);
    }

    void setLocation(int row, int column) {
        this.row = row;
        this.column = column;
    }

    Tetronimo rotate(Rotation rotation) {
        int orientationChange = rotation == Rotation.CLOCKWISE ? 1 : -1;
        orientation += orientationChange;
        if (orientation > 3) {
            orientation = 0;
        }
        if (orientation < 0) {
            orientation = 3;
        }

        return this;
    }

    Tetronimo copy() {
        Tetronimo tetronimoCopy = new Tetronimo(type);
        tetronimoCopy.row = row;
        tetronimoCopy.column = column;
        tetronimoCopy.orientation = orientation;
        tetronimoCopy.isHold = isHold;
        return tetronimoCopy;
    }

}
