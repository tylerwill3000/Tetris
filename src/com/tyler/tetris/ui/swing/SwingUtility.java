package com.tyler.tetris.ui.swing;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class SwingUtility {
	
	/**
	 * Sets the icon image for a frame. The image must exist in the same directory
	 * as the frame class' .class file
	 * @param f The frame to apply the image to
	 * @param image The image file
	 */
	public static void setIcon(JFrame f, String image) {
		f.setIconImage(new ImageIcon(f.getClass().getResource(image)).getImage());
	}
	
	/**
	 * Returns a JPanel with the specified component nested inside it
	 * @param toNest The component to nest in a new JPanel
	 * @return A new JPanel with the component added inside it
	 */
	public static JPanel nestInPanel(JComponent toNest) {
		JPanel container = new JPanel();
		container.add(toNest);
		return container;
	}
	
}
