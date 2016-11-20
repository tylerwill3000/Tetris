package com.tyler.tetris.ui.swing;

import javax.swing.JFrame;

// Frame prompting the user whether they want to save their score
public class SaveScoreFrame extends JFrame {
	/*
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
			
			TetrisProperties.setPlayerSaveName(saveName);
			
			try { // Persist player save name
				TetrisProperties.persist();
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(null, "Could not save current player save name to disk: " + e2);
			}
			
			int rank = 0;
			try {
				
				rank = TetrisDB.writeScore(
					saveName,
					ScoreModel.getScore(),
					ScoreModel.getLevel(),
					ScoreModel.getTotalLinesCleared(),
					TetrisFrame.settingsPanel.getDifficulty(),
					ScoreModel.getTotalElapsedTime()
				);
				
			}
			catch (ClassNotFoundException | SQLException e1) {
				JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage());
				return;
			}
			
			JOptionPane.showMessageDialog(null, "Score saved! Your rank: " + rank);
			dispose();
			
			// After saving score, display high scores frame with maximum record selection
			TetrisProperties.setHighScoreRecordCount(3);
			TetrisProperties.setHighScoresDifficulty(3);
			
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
		
		_jtxName.setText(TetrisProperties.getPlayerSaveName());
		
		setLayout(new GridLayout(3,1));
		
		_jlbAttainedScore.setHorizontalAlignment(JLabel.CENTER);
		_jlbAttainedScore.setText("Your score: " + ScoreModel.getScore());
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
*/	
}