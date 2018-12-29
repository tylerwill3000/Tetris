
package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Block;

import javax.swing.border.TitledBorder;
import java.util.Collection;
import java.util.List;

class BlockDisplayPanel extends PixelGrid {

  public static final int DEFAULT_BLOCK_DIMENSION = 35;
  public static final int DEFAULT_BLOCK_PADDING = 15;

  private Block displayedBlock;

  BlockDisplayPanel(String title) {
    this(title, null);
  }

  BlockDisplayPanel(String title, Block displayedBlock) {
    super(4, 5, DEFAULT_BLOCK_DIMENSION, DEFAULT_BLOCK_PADDING);
    setBorder(new TitledBorder(title));
    setFocusable(false);
    this.displayedBlock = displayedBlock;
  }

  @Override
  public Collection<Block.ColoredSquare> getCurrentColors() {
    return displayedBlock == null ? List.of() : displayedBlock.getNextPanelSquares();
  }

}

