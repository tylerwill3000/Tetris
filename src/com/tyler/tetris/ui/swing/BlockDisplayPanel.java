
package com.tyler.tetris.ui.swing;

import java.util.Collection;

import javax.swing.border.TitledBorder;

import com.tyler.tetris.Block;
import com.tyler.tetris.Block.ColoredSquare;

public class BlockDisplayPanel extends PixelGrid {
	
	public static final int DEFAULT_BLOCK_DIMENSION = 35;
	public static final int BLOCK_PADDING = 15;
	
	private Block currentBlock;
	
	public BlockDisplayPanel(String title) {
		this(title, null);
	}
	
	public BlockDisplayPanel(String title, Block currentBlock) {
		super(4, 5, DEFAULT_BLOCK_DIMENSION, BLOCK_PADDING);
		setBorder(new TitledBorder(title));
		setFocusable(false);
		this.currentBlock = currentBlock;
	}

	@Override
	public Collection<ColoredSquare> getCurrentColors() {
		return currentBlock.getNextPanelSquares();
	}

}

