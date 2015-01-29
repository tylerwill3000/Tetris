package ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

// When clicked, closes the frame passed in the constructor
public class CloseFrameButton extends TetrisButton {
	
	private JFrame _frameContainer;
	
	public CloseFrameButton(JFrame frameContainer) {
		this(frameContainer, "Close");
	}
	
	public CloseFrameButton(JFrame frameContainer, String title) {
		super(title);
		_frameContainer = frameContainer;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { _frameContainer.dispose(); }
		});
	}
	
}
