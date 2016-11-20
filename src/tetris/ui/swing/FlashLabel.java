package tetris.ui.swing;

import java.awt.Color;

import javax.swing.JLabel;

/**
 * Extends standard JLabel with ability to flash its text
 * @author Tyler
 *
 */
public class FlashLabel extends JLabel {

	public FlashLabel(String text, int center) {
		super(text, center);
	}

	public void flash(Color flashColor) {
		Color currentColor = getForeground();
		try {
			for (int i = 1; i <= 60; i++) {
				setForeground(i % 2 == 0 ? currentColor : flashColor);
				Thread.sleep(50);
			}
		}
		catch (InterruptedException e) {
			setForeground(currentColor);
		}
	}
	
}
