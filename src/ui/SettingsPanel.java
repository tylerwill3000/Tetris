package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.AudioManager;

public class SettingsPanel extends JPanel {
	
	private JCheckBox ghostSquaresCbx = new JCheckBox("Ghost Squares", true);
	private JCheckBox musicCbx = new JCheckBox("Music", true);
	private JCheckBox soundEffectsCbx = new JCheckBox("Sound Effects", true);
	
	private JList<String> difficultyLst = new JList<String>(
			new String[]{"Easy","Intermediate","Hard"}
	);
	
	private ItemListener ghostSquaresListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (ghostSquaresCbx.isSelected())
				GameFrame.gameBoardPanel.paintGhostPiece();
			else 
				GameFrame.gameBoardPanel.eraseGhostPiece();
			
			// In case ghost overlaps current piece
			GameFrame.gameBoardPanel.paintCurrentPiece();
			
		}
		
	};
	
	private ItemListener musicListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (musicCbx.isSelected())
				AudioManager.resumeCurrentSoundtrack();
			else
				AudioManager.stopCurrentSoundtrack();
			
		}
		
	};
	
	SettingsPanel() {
		
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Settings"));
		
		// Panel to hold all settings checkboxes
		JPanel cbxPanel = new JPanel(new GridLayout(3,1));
		for (JCheckBox x : new JCheckBox[]{ghostSquaresCbx, musicCbx, soundEffectsCbx}) {
			cbxPanel.add(x);
			x.setFocusable(false);
		}
		
		// Default to "Intermediate" difficulty
		difficultyLst.setSelectedIndex(1);
		difficultyLst.setBorder(GameFrame.LINE_BORDER);
		
		add(cbxPanel, BorderLayout.CENTER);
		add(difficultyLst, BorderLayout.SOUTH);
		
	}
	
	public boolean ghostSquaresOn() { return ghostSquaresCbx.isSelected(); }
	public boolean musicOn() { return musicCbx.isSelected(); }
	public boolean effectsOn() { return soundEffectsCbx.isSelected(); }
	
	int getDifficulty() { return difficultyLst.getSelectedIndex(); }
	
	// Music checkbox gets its own unique enabling methods since it is enabled / disabled
	// independently from the ghost squares checkbox in the MenuPanel class
	void enableMusicCbxListener() { musicCbx.addItemListener(musicListener); }
	void disableMusicCbxListener() { musicCbx.removeItemListener(musicListener); }
	
	void enableCbxListeners() {
		ghostSquaresCbx.addItemListener(ghostSquaresListener);
		enableMusicCbxListener();
	}
	
	void disableCbxListeners() {
		ghostSquaresCbx.removeItemListener(ghostSquaresListener);
		disableMusicCbxListener();
	}
	
	void enableDifficultyList() { difficultyLst.setEnabled(true); }
	
	void disableDifficultyList() { difficultyLst.setEnabled(false); }
		
}
