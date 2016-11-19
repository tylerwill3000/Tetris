package com.tyler.tetris.ui.swing.widget;

import static java.awt.Color.LIGHT_GRAY;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class TetrisButton extends JButton {
	
	private final static int BUTTON_HEIGHT = 30;
	private final static int BUTTON_WIDTH = 100;
	
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	public TetrisButton(String buttonText) {
		
		setText(buttonText);
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setBorder(BorderFactory.createEtchedBorder());
		setFocusable(false);
		
		addMouseListener(new MouseAdapter() {
			
			public void mouseEntered(MouseEvent e) {
				if (isEnabled()) {
					setCursor(HAND_CURSOR);
					setBackground(LIGHT_GRAY);
				}
			}
			
			public void mouseExited(MouseEvent e) {
				setBackground(null);
			}
			
		});
		
	}
	
}
