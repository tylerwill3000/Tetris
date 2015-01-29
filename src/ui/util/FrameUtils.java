package ui.util;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Utility methods for performing some basic functions to manipulate frames and panels
public final class FrameUtils {
	
	public static void setIcon(JFrame f, String image) {
		f.setIconImage(new ImageIcon(f.getClass().getResource(image)).getImage());
	}
	
	public static JPanel nestInPanel(JComponent toNest) {
		JPanel container = new JPanel();
		container.add(toNest);
		return container;
	}
	
}
