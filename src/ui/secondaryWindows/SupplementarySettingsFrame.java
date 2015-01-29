package ui.secondaryWindows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import ui.components.TetrisButton;
import model.Properties;

/**
 * Superclass for frames that allow you to choose settings and then save them.
 * Objects that extend this class will automatically receive a button titled
 * "Save & Close" with a listener that will save the current game properties
 * to disk, as well as a window listener which will reload properties from
 * disk if the window is closed from the 'X' button
 * @author Tyler
 *
 */
public class SupplementarySettingsFrame extends JFrame {
	
	protected TetrisButton saveAndClose = new TetrisButton("Save & Close");
	
	SupplementarySettingsFrame() {
		
		// When frames of these type are closed, properties are saved to disk
		saveAndClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties.saveCurrentProperties(true);
				dispose();
			}
		});
		
		// If the frame is closed from the 'X' button, reload properties with
		// those stored on disk
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Properties.loadPropertiesFromDisk();
			}
		});
		
	}
	
}
