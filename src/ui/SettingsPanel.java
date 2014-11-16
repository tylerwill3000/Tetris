package ui;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.AudioManager;

public class SettingsPanel extends JPanel {
	
	private JCheckBox ghostSquaresCbx = new JCheckBox("Ghost Squares", true);
	private JCheckBox musicCbx = new JCheckBox("Music", true);
	private JCheckBox soundEffectsCbx = new JCheckBox("Sound Effects", true);
	
	boolean cbxsEnabled = true;
	
	ItemListener ghostSquaresListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (ghostSquaresCbx.isSelected())
				UIBox.gameBoardPanel.paintGhostPiece();
			else 
				UIBox.gameBoardPanel.eraseGhostPiece();
			
			// In case ghost overlaps current piece
			UIBox.gameBoardPanel.paintCurrentPiece();
			
		}
		
	};
	
	ItemListener musicListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (musicCbx.isSelected())
				AudioManager.resumeCurrentSoundtrack();
			else
				AudioManager.stopCurrentSoundtrack();
			
		}
		
	};
		
	public boolean ghostSquaresOn() { return ghostSquaresCbx.isSelected(); }
	public boolean musicOn() { return musicCbx.isSelected(); }
	public boolean effectsOn() { return soundEffectsCbx.isSelected(); }
	
	void enableCbxListeners() {
		ghostSquaresCbx.addItemListener(ghostSquaresListener);
		musicCbx.addItemListener(musicListener);
		cbxsEnabled = true;
	}
	
	void disableCbxListeners() {
		ghostSquaresCbx.removeItemListener(ghostSquaresListener);
		musicCbx.removeItemListener(musicListener);
		cbxsEnabled = false;
	}
	
	SettingsPanel() {
		
		setLayout(new GridLayout(3,1));
		setBorder(new TitledBorder("Settings"));
		
		for (JCheckBox x : new JCheckBox[]{ghostSquaresCbx, musicCbx, soundEffectsCbx}) {
			add(x);
			x.setFocusable(false);
		}
		
		enableCbxListeners();
		
	}
	
}
