package com.tyler.tetris.ui.swing;

import javax.swing.JFrame;

/**
 * When clicked, closes the frame passed in the constructor
 * @author Tyler
 */
public class CloseFrameButton extends TetrisButton {
	
	public CloseFrameButton(JFrame frameContainer) {
		this(frameContainer, "Close");
	}
	
	public CloseFrameButton(JFrame frameContainer, String title) {
		super(title);
		addActionListener(e -> frameContainer.dispose());
	}
	
}
