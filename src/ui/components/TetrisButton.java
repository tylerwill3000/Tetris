package ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import ui.GameFrame;

/**
 *  Standard button style for all frames
 * @author Tyler
 */
public class TetrisButton extends JButton {
	
	private final static int BUTTON_HEIGHT = 30;
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	// Causes the mouse to change to a hand icon when mousing over the button
	private MouseListener handCursorListener = new MouseAdapter() {
		
		public void mouseEntered(MouseEvent e) {
			if (isEnabled()) {
				setCursor(HAND_CURSOR);
				setBackground(Color.LIGHT_GRAY);
			}
		}
		
		public void mouseExited(MouseEvent e) {
			setBackground(null);
		}
		
	};
	
	/**
	 * Construct button with specified width (height is constant for all)
	 */
	public TetrisButton(String buttonText, int width) {
		setText(buttonText);
		setPreferredSize(new Dimension(width, BUTTON_HEIGHT));
		setBorder(GameFrame.ETCHED_BORDER);
		setFocusable(false);
		addMouseListener(handCursorListener);
	}
	
	/**
	 * Construct button with default size
	 */
	public TetrisButton(String buttonText) {
		this(buttonText, 100);
	}
	
}
