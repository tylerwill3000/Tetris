package tetris.ui.swing;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tetris.Score;
import tetris.ScoreDao;
import tetris.TetrisGame;

class ScoreResultsFrame extends JFrame {
	
	private final static int NAME_LENGTH = 20;
	
	JLabel lblScore = new JLabel();
	JTextField txtName = new JTextField(8);
	TetrisButton btnSaveScore = new TetrisButton("Save");
	TetrisButton btnClose = new TetrisButton("Cancel");
	
	ScoreResultsFrame(ScoreDao scoresDao, TetrisGame game) throws Exception {
		
		setLayout(new GridLayout(3,1));
		
		txtName.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (txtName.getText().length() > NAME_LENGTH) {
					txtName.setText(txtName.getText().substring(0, NAME_LENGTH));
				}
			}
		});
		
		int rank = scoresDao.determineRank(game.getScore());
		lblScore.setFont(MasterTetrisFrame.ARIAL_HEADER);
		lblScore.setHorizontalAlignment(JLabel.CENTER);
		lblScore.setText("Your score: " + game.getScore() + "      Your rank: " + rank);
		add(lblScore);
		
		if (ScoreDao.isHighScore(rank)) {
			
			// Input area with instructions and name text field
			JPanel inputPanel = new JPanel();
			JLabel congrats = new JLabel("Congratulations! You made the leaderboard! Enter the name to save your score under or press cancel: ");
			congrats.setFont(MasterTetrisFrame.ARIAL_DESCRIPTION);
			inputPanel.add(congrats);
			inputPanel.add(txtName);
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(btnSaveScore);
			buttonPanel.add(btnClose);
			
			btnSaveScore.setMnemonic('s');
			btnClose.setMnemonic('c');
			
			btnSaveScore.addActionListener(e -> {
				
				String saveName = txtName.getText();
				
				if (saveName.equals("")) {
					JOptionPane.showMessageDialog(null, "You must enter a name to save your score");
					return;
				}
				
				try {
					scoresDao.saveScore(new Score(saveName, game.getScore(), game.getGameTime(),
					                      game.getDifficulty(), game.getTotalLinesCleared(), game.getLevel()));
					dispose();
					new LeaderboardFrame(scoresDao, rank);
				}
				catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error saving score: " + e1.getMessage());
					return;
				}
			});
			
			btnClose.addActionListener(e -> dispose());
			
			add(inputPanel);
			add(buttonPanel);
		}
		
		SwingUtility.setIcon(this, "/images/save-icon.png");
		setTitle("Score Results");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
