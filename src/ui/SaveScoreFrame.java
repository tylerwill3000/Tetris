package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.db.DBComm;
import model.GameBoardModel;

// Frame prompting the user whether they want to save their score
public class SaveScoreFrame extends JFrame {
	
	JLabel attainedScore = new JLabel();
	JLabel saveStatus = new JLabel();
	
	JTextField name = new JTextField(8);
	
	JButton saveScore = new JButton("Save");
	JButton cancel = new JButton("Cancel");
	
	ActionListener saveScoreListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			saveScore.setEnabled(false); // Doesn't make sense to allow user to save score again
			
			saveStatus.setText("Writing...");
			saveStatus.setForeground(Color.BLACK);
		
			try {
				
				DBComm.writeScore(
					name.getText(),
					GameBoardModel.getScore(),
					GameBoardModel.getLevel(),
					GameBoardModel.getLinesCompleted(),
					GameFrame.settingsPanel.getDifficulty()
				);
				
				saveStatus.setForeground(Color.GREEN);
				saveStatus.setText("Score Saved!");
				cancel.setText("OK");
				
			}
			catch (ClassNotFoundException | SQLException e1) {
				
				saveStatus.setForeground(Color.RED);
				saveStatus.setText("Error writing to database");
				
				e1.printStackTrace();
				
			}
			
		}
		
	};
	
	SaveScoreFrame() {
		
		setLayout(new GridLayout(4,1));
		
		attainedScore.setHorizontalAlignment(JLabel.CENTER);
		attainedScore.setText("Your score: " + GameBoardModel.getScore());
		add(attainedScore);
		
		// Input area with instructions and name text field
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Enter the name to save your score under or press cancel: "));
		inputPanel.add(name);
		add(inputPanel);
		
		saveStatus.setHorizontalAlignment(JLabel.CENTER);
		add(saveStatus);
		
		// Button panel for saving / canceling
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(saveScore);
		buttonPanel.add(cancel);
		add(buttonPanel);
		
		saveScore.setMnemonic('s');
		cancel.setMnemonic('c');
		
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		saveScore.addActionListener(saveScoreListener);
		
		addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent e) {
				
				switch (e.getKeyCode()) {
				
					case KeyEvent.VK_S:
						saveScore.doClick();
						break;
						
					case KeyEvent.VK_C:
						cancel.doClick();
						break;
						
				}
			}
			
		});
		
		setTitle("Save Score");
		setSize(450,170);
		setResizable(false);
		setLocationRelativeTo(null);		
		setVisible(true);
		
	}
	
}
