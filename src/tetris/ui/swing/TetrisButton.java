package tetris.ui.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

class TetrisButton extends JButton {
	
	private final static int BUTTON_HEIGHT = 30;
	private final static int BUTTON_WIDTH = 100;
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private final static Color BUTTON_HIGHLIGHT = new Color(205, 220, 219);
	
	TetrisButton(String buttonText) {
		
		setText(buttonText);
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setBorder(BorderFactory.createEtchedBorder());
		setFocusable(false);
		
		addMouseListener(new MouseAdapter() {
			
			public void mouseEntered(MouseEvent e) {
				if (isEnabled()) {
					setCursor(HAND_CURSOR);
					setBackground(BUTTON_HIGHLIGHT);
				}
			}
			
			public void mouseExited(MouseEvent e) {
				setBackground(null);
			}
			
		});
		
	}
	
}
