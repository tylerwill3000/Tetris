package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.AudioManager;

public class SettingsPanel extends JPanel {
	
	public final static String[] DIFFICULTIES = {"Easy","Medium","Hard"};
	
	private JCheckBox ghostSquaresCbx = new JCheckBox("Ghost Squares", true);
	private JCheckBox musicCbx = new JCheckBox("Music", true);
	private JCheckBox soundEffectsCbx = new JCheckBox("Sound Effects", true);
	private JCheckBox saveScoreCbx = new JCheckBox("Save Scores", true);
	
	private JComboBox<String> difficultyList = new JComboBox<String>(DIFFICULTIES);
	
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
		
		setLayout(new GridLayout(5,1));
		setBorder(new TitledBorder("Settings"));
		
		for (JCheckBox x : new JCheckBox[]{ghostSquaresCbx, musicCbx, soundEffectsCbx, saveScoreCbx}) {
			add(x);
			x.setFocusable(false);
		}
		
		JPanel diffPanel = new JPanel(new BorderLayout());
		diffPanel.add(new JLabel("Difficulty:  "), BorderLayout.WEST);
		diffPanel.add(difficultyList, BorderLayout.CENTER);
		add(diffPanel);
		
	}
	
	public boolean ghostSquaresOn() { return ghostSquaresCbx.isSelected(); }
	public boolean musicOn() { return musicCbx.isSelected(); }
	public boolean effectsOn() { return soundEffectsCbx.isSelected(); }
	public boolean saveScoreOn() { return saveScoreCbx.isSelected(); }
	
	int getDifficulty() { return difficultyList.getSelectedIndex(); }
	
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
	
	void enableDifficultyList() { difficultyList.setEnabled(true); }
	
	void disableDifficultyList() { difficultyList.setEnabled(false); }
		
}
