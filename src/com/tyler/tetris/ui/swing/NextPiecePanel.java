
package com.tyler.tetris.ui.swing;

import java.awt.Dimension;

import javax.swing.border.TitledBorder;

import com.tyler.tetris.Block;

/**
 * These are panels that are designed to display a still piece on them
 * @author Tyler
 */
public abstract class NextPiecePanel extends PixelGrid {
	
	public NextPiecePanel(String title) {
		this(title, null);
	}
	
	public NextPiecePanel(String title, Block currentPiece) {
		super(4, 5);
		setBorder(new TitledBorder(title));
		setFocusable(false);
		
		// Size of all next piece panels should be constant
		setPreferredSize(new Dimension(MasterTetrisFrame.INFO_PANEL_WIDTH, 35 * 4));
	}

}

