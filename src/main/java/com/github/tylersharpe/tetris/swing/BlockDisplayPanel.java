
package com.github.tylersharpe.tetris.swing;

import com.github.tylersharpe.tetris.Block;

import javax.swing.border.TitledBorder;
import java.util.Collection;
import java.util.Collections;

class BlockDisplayPanel extends PixelGrid {

  static final int DEFAULT_BLOCK_DIMENSION = 35;
  private static final int BLOCK_PADDING = 15;

  private Block currentBlock;

  BlockDisplayPanel(String title) {
    this(title, null);
  }

  BlockDisplayPanel(String title, Block currentBlock) {
    super(4, 5, DEFAULT_BLOCK_DIMENSION, BLOCK_PADDING);
    setBorder(new TitledBorder(title));
    setFocusable(false);
    this.currentBlock = currentBlock;
  }

  @Override
  public Collection<Block.ColoredSquare> getCurrentColors() {
    return currentBlock == null ? Collections.emptyList() : currentBlock.getNextPanelSquares();
  }

}

