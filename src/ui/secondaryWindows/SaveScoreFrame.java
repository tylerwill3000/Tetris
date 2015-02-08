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
	
	private JPanel buttonPanel;
	private JLabel attainedScore = new JLabel();
	private JTextField name = new JTextField(8);
	private TetrisButton saveScore = new TetrisButton("Save");
	private CloseFrameButton jbtClose = new CloseFrameButton(this, "Cancel");
	
	private ActionListener saveScoreListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			String saveName = name.getText();
			
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
					GameFrame._settingsPanel.getDifficulty()
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
		name.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (name.getText().length() > NAME_LENGTH)
					name.setText(name.getText().substring(0, NAME_LENGTH));
			}
		});
		
		name.setText(Properties.GAME_PROPERTIES.getProperty("player.save.name"));
		
		setLayout(new GridLayout(3,1));
		
		attainedScore.setHorizontalAlignment(JLabel.CENTER);
		attainedScore.setText("Your score: " + GameBoardModel.getScore());
		add(attainedScore);
		
		// Input area with instructions and name text field
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Enter the name to save your score under or press cancel: "));
		inputPanel.add(name);
		add(inputPanel);
		
		// Button panel for saving / canceling
		buttonPanel = new JPanel();
		buttonPanel.add(saveScore);
		buttonPanel.add(jbtClose);
		add(buttonPanel);
		
		saveScore.setMnemonic('s');
		jbtClose.setMnemonic('c');
		
		saveScore.addActionListener(saveScoreListener);
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_S:
						saveScore.doClick();
						break;
					case KeyEvent.VK_C:
						jbtClose.doClick();
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
