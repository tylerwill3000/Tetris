
package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Tetronimo;
import com.github.tylersharpe.tetris.ColoredSquare;

import javax.swing.border.TitledBorder;
import java.util.Collection;
import java.util.List;

class TetronimoDisplayPanel extends ColorGrid {
    public static final int DEFAULT_SQUARE_DIMENSION = 35;
    public static final int DEFAULT_SQUARE_PADDING = 15;

    private static final int DISPLAY_PANEL_ROWS = 4;
    private static final int DISPLAY_PANEL_COLUMNS = 5;

    private final Tetronimo displayedTetronimo;

    TetronimoDisplayPanel(String title) {
        this(title, null);
    }

    TetronimoDisplayPanel(String title, Tetronimo displayedTetronimo) {
        super(DISPLAY_PANEL_ROWS, DISPLAY_PANEL_COLUMNS, DEFAULT_SQUARE_DIMENSION, DEFAULT_SQUARE_PADDING);
        setBorder(new TitledBorder(title));
        setFocusable(false);
        this.displayedTetronimo = displayedTetronimo;
    }

    @Override
    public Collection<ColoredSquare> getCurrentColors() {
        return displayedTetronimo == null ? List.of() : displayedTetronimo.getPreviewPanelSquares();
    }

}

