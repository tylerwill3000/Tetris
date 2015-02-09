package ui.secondaryWindows;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ui.GameFrame;
import ui.components.CloseFrameButton;
import ui.components.TetrisButton;
import ui.util.FrameUtils;
import model.DB;
import model.GameBoardModel;
import model.Properties;

// Frame prompting the user whether they want to save their score
public class SaveScoreFrame extends JFrame {
	
	public final static int NAME_LENGTH = 20;
	
	private JPanel _buttonPanel;
	private JLabel _jlbAttainedScore = new JLabel();
	private JTextField _jtxName = new JTextField(8);
	private TetrisButton _btnSaveScore = new TetrisButton("Save");
	private CloseFrameButton _btnClose = new CloseFrameButton(this, "Cancel");
	
	private ActionListener _saveScoreListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			String saveName = _jtxName.getText();
			
			if (saveName.equals("")) {
				JOptionPane.showMessageDialog(null, "You must enter a name to save your score");
				return;
			}
			
			Properties.setPlayerSaveName(saveName);
			Properties.saveCurrentProperties(true); // Persist player save name
			
			int rank = 0;
			try {
				
				rank = DB.writeScore(
					saveName,
					GameBoardModel.getScore(),
					GameBoardModel.getLevel(),
					GameBoardModel.getLinesCompleted(),
					GameFrame._settingsPanel.getDifficulty(),
					GameFrame._scorePanel.getGameTimeString()
				);
				
			}
			catch (ClassNotFoundException | SQLException e1) {
				JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage());
				return;
			}
			
			JOptionPane.showMessageDialog(null, "Score saved! Your rank: " + rank);
			dispose();
			
			// After saving score, display high scores frame with maximum record selection
			Properties.setHighScoreRecordCount(3);
			Properties.setHighScoresDifficulty(3);
			
			// I currently do not have the ability to target certain pages of high scores.
			// Therefore, only show a new high score frame if the rank is visible on the
			// first page; else, the score would not show up on the frame
			if (rank <= 100) new HighScoreFrame(rank);
			
		}
		
	};
	
	public SaveScoreFrame() {
		
		// Validates name length
		_jtxName.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (_jtxName.getText().length() > NAME_LENGTH)
					_jtxName.setText(_jtxName.getText().substring(0, NAME_LENGTH));
			}
		});
		
		_jtxName.setText(Properties.GAME_PROPERTIES.getProperty("player.save.name"));
		
		setLayout(new GridLayout(3,1));
		
		_jlbAttainedScore.setHorizontalAlignment(JLabel.CENTER);
		_jlbAttainedScore.setText("Your score: " + GameBoardModel.getScore());
		add(_jlbAttainedScore);
		
		// Input area with instructions and name text field
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Enter the name to save your score under or press cancel: "));
		inputPanel.add(_jtxName);
		add(inputPanel);
		
		// Button panel for saving / canceling
		_buttonPanel = new JPanel();
		_buttonPanel.add(_btnSaveScore);
		_buttonPanel.add(_btnClose);
		add(_buttonPanel);
		
		_btnSaveScore.setMnemonic('s');
		_btnClose.setMnemonic('c');
		
		_btnSaveScore.addActionListener(_saveScoreListener);
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_S:
						_btnSaveScore.doClick();
						break;
					case KeyEvent.VK_C:
						_btnClose.doClick();
						break;
				}
			}
		});
		
		FrameUtils.setIcon(this, "save-icon.png");
		setTitle("Save Score");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
}
