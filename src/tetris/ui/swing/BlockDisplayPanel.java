
package tetris.ui.swing;

import java.util.Collection;

import javax.swing.border.TitledBorder;

import tetris.Block;
import tetris.Block.ColoredSquare;

class BlockDisplayPanel extends PixelGrid {
	
	static final int DEFAULT_BLOCK_DIMENSION = 35;
	static final int BLOCK_PADDING = 15;
	
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
	public Collection<ColoredSquare> getCurrentColors() {
		return currentBlock.getNextPanelSquares();
	}

}

