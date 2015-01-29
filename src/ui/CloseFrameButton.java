package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

// When clicked, closes the frame passed in the constructor
public class CloseFrameButton extends TetrisButton {
	
	private JFrame _frameContainer;
	
	CloseFrameButton(JFrame frameContainer) {
		super("Close");
		_frameContainer = frameContainer;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { _frameContainer.dispose(); }
		});
	}
	
}
