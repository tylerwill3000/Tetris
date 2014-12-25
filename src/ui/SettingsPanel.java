package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.AudioManager;

public class SettingsPanel extends JPanel {
	
	// Public so it can be used to map int values to difficulty strings by other
	// GUI classes and the DBComm class
	public final static String[] DIFFICULTIES = {"Easy","Medium","Hard"};
	
	private JCheckBox jcbxGhostSquares = new JCheckBox("Ghost Squares", true);
	private JCheckBox jcbxMusic = new JCheckBox("Music", true);
	private JCheckBox jcbxSoundEffects = new JCheckBox("Sound Effects", true);
	private JCheckBox jcbxSaveScores = new JCheckBox("Save Scores", true);
	
	private JComboBox<String> jlstDifficulty = new JComboBox<String>(DIFFICULTIES);
	
	private GameFrame.TetrisButton jbtChooseSpecials = new GameFrame.TetrisButton("Special Pieces...");
	private GameFrame.TetrisButton jbtChooseBlockStyles = new GameFrame.TetrisButton("Block Styles...");
	
	private ItemListener ghostSquaresListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (jcbxGhostSquares.isSelected())
				GameFrame.gameBoardPanel.paintGhostPiece();
			else 
				GameFrame.gameBoardPanel.eraseGhostPiece();
			
			// In case ghost overlaps current piece
			GameFrame.gameBoardPanel.paintCurrentPiece();
			
		}
		
	};
	
	private ItemListener musicListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (jcbxMusic.isSelected())
				AudioManager.resumeCurrentSoundtrack();
			else
				AudioManager.stopCurrentSoundtrack();
			
		}
		
	};
	
	SettingsPanel() {
		
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Settings"));
		
		JPanel checkboxes = new JPanel(new GridLayout(4,1));
		for (JCheckBox x : new JCheckBox[]{jcbxGhostSquares, jcbxMusic, jcbxSoundEffects, jcbxSaveScores}) {
			checkboxes.add(x);
			x.setFocusable(false);
		}
		
		JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		diffPanel.add(new JLabel("Difficulty:  "));
		diffPanel.add(jlstDifficulty);
		
		JPanel buttonContainer = new JPanel(new GridLayout(2,1));
		buttonContainer.add(jbtChooseSpecials);
		buttonContainer.add(jbtChooseBlockStyles);
		
		jbtChooseSpecials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { new SpecialPiecesFrame(); }
		});
		
		jbtChooseBlockStyles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { new BlockStylesFrame(); }
		});
		
		add(checkboxes, BorderLayout.NORTH);
		add(diffPanel, BorderLayout.CENTER);
		add(buttonContainer, BorderLayout.SOUTH);
		
	}
	
	public boolean ghostSquaresOn() { return jcbxGhostSquares.isSelected(); }
	public boolean musicOn() { return jcbxMusic.isSelected(); }
	public boolean effectsOn() { return jcbxSoundEffects.isSelected(); }
	public boolean saveScoreOn() { return jcbxSaveScores.isSelected(); }
	
	public int getDifficulty() { return jlstDifficulty.getSelectedIndex(); }
	
	// Music checkbox gets its own unique enabling methods since it is enabled / disabled
	// independently from the ghost squares checkbox in the MenuPanel class
	void enableMusicCbxListener() { jcbxMusic.addItemListener(musicListener); }
	void disableMusicCbxListener() { jcbxMusic.removeItemListener(musicListener); }
	
	void enableCbxListeners() {
		jcbxGhostSquares.addItemListener(ghostSquaresListener);
		enableMusicCbxListener();
	}
	
	void disableCbxListeners() {
		jcbxGhostSquares.removeItemListener(ghostSquaresListener);
		disableMusicCbxListener();
	}
	
	void enableDifficultyList() { jlstDifficulty.setEnabled(true); }
	void enableSpecialPiecesButton() { jbtChooseSpecials.setEnabled(true); }
	
	void disableDifficultyList() { jlstDifficulty.setEnabled(false); }
	void disableSpecialPiecesButton() { jbtChooseSpecials.setEnabled(false); }
		
}
