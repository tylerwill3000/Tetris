package com.tyler.tetris.ui.swing;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tyler.tetris.TetrisGame;
import com.tyler.tetris.score.FlatFileHighScoreDao;
import com.tyler.tetris.score.HighScore;
import com.tyler.tetris.score.HighScoreDao;
import com.tyler.tetris.ui.swing.widget.TetrisButton;

public class SaveScoreFrame extends JFrame {
	
	public final static int NAME_LENGTH = 20;
	
	private HighScoreDao scoreDao = new FlatFileHighScoreDao();
	private JLabel lblScore = new JLabel();
	private JTextField txtName = new JTextField(8);
	private TetrisButton btnSaveScore = new TetrisButton("Save");
	private TetrisButton btnClose = new TetrisButton("Cancel");
	
	public SaveScoreFrame(TetrisGame game) {
		
		txtName.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (txtName.getText().length() > NAME_LENGTH) {
					txtName.setText(txtName.getText().substring(0, NAME_LENGTH));
				}
			}
		});
		
		lblScore.setHorizontalAlignment(JLabel.CENTER);
		lblScore.setText("Your score: " + game.getScore());
		
		// Input area with instructions and name text field
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Enter the name to save your score under or press cancel: "));
		inputPanel.add(txtName);
		
		// Button panel for saving / canceling
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
				int rank = scoreDao.saveHighScore(new HighScore(
				                                     saveName,
				                                     game.getScore(),
				                                     game.getGameTime(),
				                                     game.getDifficulty(),
				                                     game.getTotalLinesCleared(),
				                                     game.getLevel()));
				
				JOptionPane.showMessageDialog(null, "Score saved! Your rank: " + rank);
				dispose();
			}
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error saving score: " + e1.getMessage());
				return;
			}
		});
		
		btnClose.addActionListener(e -> dispose());
		
		setLayout(new GridLayout(3,1));
		add(lblScore);
		add(inputPanel);
		add(buttonPanel);
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_S:
						btnSaveScore.doClick();
						break;
					case KeyEvent.VK_C:
						btnClose.doClick();
						break;
				}
			}
		});
		
		SwingUtility.setIcon(this, "/images/save-icon.png");
		setTitle("Save Score");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
