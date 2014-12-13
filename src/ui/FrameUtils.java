package ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

// Utility methods for performing some basic functions to manipulate frames
public final class FrameUtils {
	
	public static void setIcon(JFrame f, String image) {
		f.setIconImage(new ImageIcon(f.getClass().getResource("images/" + image)).getImage());
	}
	
}
