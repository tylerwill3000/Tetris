package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ui.components.TetrisButton;
import ui.secondaryWindows.BlockStylesFrame;
import ui.secondaryWindows.DBSettingsFrame;
import ui.secondaryWindows.SpecialPiecesFrame;
import model.AudioManager;

/**
 * Holds all game settings components
 * @author Tyler
 */
public class SettingsPanel extends JPanel {
	
	// Public so it can be used to map int values to difficulty strings by other
	// GUI classes and the DBComm class
	public final static String[] DIFFICULTIES = {"Easy","Medium","Hard"};
	
	private JCheckBox _jcbxGhostSquares = new JCheckBox("Ghost Squares", true);
	private JCheckBox _jcbxMusic = new JCheckBox("Music", true);
	private JCheckBox _jcbxSoundEffects = new JCheckBox("Sound Effects", true);
	private JCheckBox _jcbxSaveScores = new JCheckBox("Save Scores", true);
	
	private JComboBox<String> _jlstDifficulty = new JComboBox<String>(DIFFICULTIES);
	
	private TetrisButton _jbtChooseSpecials = new TetrisButton("Special Pieces...");
	private TetrisButton _jbtChooseBlockStyles = new TetrisButton("Block Styles...");
	private TetrisButton _jbtDBConfig = new TetrisButton("Database Connectivity...");
	
	private ItemListener _ghostSquaresListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (_jcbxGhostSquares.isSelected())
				GameFrame._gameBoardPanel.paintGhostPiece();
			else 
				GameFrame._gameBoardPanel.eraseGhostPiece();
			
			// In case ghost overlaps current piece
			GameFrame._gameBoardPanel.paintCurrentPiece();
			
		}
		
	};
	
	private ItemListener _musicListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (_jcbxMusic.isSelected())
				AudioManager.resumeCurrentSoundtrack();
			else
				AudioManager.stopCurrentSoundtrack();
			
		}
		
	};
	
	SettingsPanel() {
		
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Settings"));
		
		JPanel checkboxes = new JPanel(new GridLayout(4,1));
		for (JCheckBox x : Arrays.asList(_jcbxGhostSquares, _jcbxMusic, _jcbxSoundEffects, _jcbxSaveScores)) {
			checkboxes.add(x);
			x.setFocusable(false);
		}
		
		JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		diffPanel.add(new JLabel("Difficulty:  "));
		diffPanel.add(_jlstDifficulty);
		
		JPanel buttonContainer = new JPanel(new GridLayout(3,1));
		buttonContainer.add(_jbtChooseSpecials);
		buttonContainer.add(_jbtChooseBlockStyles);
		buttonContainer.add(_jbtDBConfig);
		
		_jbtChooseSpecials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { new SpecialPiecesFrame(); }
		});
		
		_jbtChooseBlockStyles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { new BlockStylesFrame(); }
		});
		
		_jbtDBConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { new DBSettingsFrame(); }
		});
		
		add(checkboxes, BorderLayout.NORTH);
		add(diffPanel, BorderLayout.CENTER);
		add(buttonContainer, BorderLayout.SOUTH);
		
	}
	
	public boolean ghostSquaresOn() { return _jcbxGhostSquares.isSelected(); }
	public boolean musicOn() { return _jcbxMusic.isSelected(); }
	public boolean effectsOn() { return _jcbxSoundEffects.isSelected(); }
	public boolean saveScoreOn() { return _jcbxSaveScores.isSelected(); }
	
	public int getDifficulty() { return _jlstDifficulty.getSelectedIndex(); }
	
	// Music checkbox gets its own unique enabling methods since it is enabled / disabled
	// independently from the ghost squares checkbox in the MenuPanel class
	void enableMusicCbxListener() { _jcbxMusic.addItemListener(_musicListener); }
	void disableMusicCbxListener() { _jcbxMusic.removeItemListener(_musicListener); }
	
	void enableCbxListeners() {
		_jcbxGhostSquares.addItemListener(_ghostSquaresListener);
		enableMusicCbxListener();
	}
	
	void disableCbxListeners() {
		_jcbxGhostSquares.removeItemListener(_ghostSquaresListener);
		disableMusicCbxListener();
	}
	
	void enableDifficultyList() { _jlstDifficulty.setEnabled(true); }
	void enableSpecialPiecesButton() { _jbtChooseSpecials.setEnabled(true); }
	void enableBlockStylesButton() { _jbtChooseBlockStyles.setEnabled(true); }
	void enableDatabaseConnectivityButton() { _jbtDBConfig.setEnabled(true); }
	
	void disableDifficultyList() { _jlstDifficulty.setEnabled(false); }
	void disableSpecialPiecesButton() { _jbtChooseSpecials.setEnabled(false); }
	void disableBlockStylesButton() { _jbtChooseBlockStyles.setEnabled(false); }
	void disableDatabaseConnectivityButton() { _jbtDBConfig.setEnabled(false); }
		
}
