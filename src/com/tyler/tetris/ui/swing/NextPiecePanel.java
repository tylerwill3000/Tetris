
package com.tyler.tetris.ui.swing;

import java.awt.Dimension;

import javax.swing.border.TitledBorder;

import com.tyler.tetris.model.Block;

/**
 * These are panels that are designed to display a still piece on them
 * @author Tyler
 */
public abstract class NextPiecePanel extends GridPainter {
	
	public NextPiecePanel(String title) {
		this(title, null);
	}
	
	public NextPiecePanel(String title, Block currentPiece) {
		
		super(4, 5);
		setBorder(new TitledBorder(title));
		setFocusable(false);
		
		// Size of all next piece panels should be constant
		setPreferredSize(new Dimension(
				
			TetrisFrame.INFO_PANEL_WIDTH,
			
			// For some reason I need +10 to get proper size, even
			// though I want to height of the panel to be 4 squares...
			GridPainter.SQUARE_SIDE_LENGTH * 4 + 10));
	}

}

