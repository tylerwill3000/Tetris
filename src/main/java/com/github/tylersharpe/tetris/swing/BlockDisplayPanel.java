
package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Block;
import com.github.tylersharpe.tetris.ColoredSquare;

import javax.swing.border.TitledBorder;
import java.util.Collection;
import java.util.List;

class BlockDisplayPanel extends ColorGrid {

    public static final int DEFAULT_BLOCK_DIMENSION = 35;
    public static final int DEFAULT_BLOCK_PADDING = 15;

    private static final int BLOCK_DISPLAY_PANEL_ROWS = 4;
    private static final int BLOCK_DISPLAY_PANEL_COLUMNS = 5;

    private final Block displayedBlock;

    BlockDisplayPanel(String title) {
        this(title, null);
    }

    BlockDisplayPanel(String title, Block displayedBlock) {
        super(BLOCK_DISPLAY_PANEL_ROWS, BLOCK_DISPLAY_PANEL_COLUMNS, DEFAULT_BLOCK_DIMENSION, DEFAULT_BLOCK_PADDING);
        setBorder(new TitledBorder(title));
        setFocusable(false);
        this.displayedBlock = displayedBlock;
    }

    @Override
    public Collection<ColoredSquare> getCurrentColors() {
        return displayedBlock == null ? List.of() : displayedBlock.getPreviewPanelSquares();
    }

}

