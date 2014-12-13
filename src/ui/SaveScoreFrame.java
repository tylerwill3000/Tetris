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

import model.DBComm;
import model.GameBoardModel;

// Frame prompting the user whether they want to save their score
public class SaveScoreFrame extends JFrame {
	
	private static String cachedName = null;
	
	private JLabel attainedScore = new JLabel();
	private JLabel saveStatus = new JLabel();
	
	private JTextField name = new JTextField(8);
	
	private JButton saveScore = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	
	private ActionListener saveScoreListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			if (name.getText().equals("")) {
				
				saveStatus.setForeground(Color.RED);
				saveStatus.setText("Error: you must enter a name to save your score");
				
				pack();
				setLocationRelativeTo(null);
				
				return;
				
			}
			
			cachedName = name.getText();
			
			saveStatus.setForeground(Color.BLACK);
			saveStatus.setText("Writing...");
		
			try {
				
				DBComm.writeScore(
					name.getText(),
					GameBoardModel.getScore(),
					GameBoardModel.getLevel(),
					GameBoardModel.getLinesCompleted(),
					GameFrame.settingsPanel.getDifficulty()
				);
				
			}
			catch (ClassNotFoundException | SQLException e1) {
				
				saveStatus.setForeground(Color.RED);
				saveStatus.setText("  " + e1.getMessage() + "  ");
				
				// Expand frame to be able to display full error text and then
				// re-center it
				pack();
				setLocationRelativeTo(null);
				
				return;
				
			}
			
			saveStatus.setForeground(Color.GREEN);
			saveStatus.setText("Score Saved!");
			saveScore.setEnabled(false); // Doesn't make sense to allow user to save score again
			cancel.setText("OK");
			
			// Make sure frame is not over-expanded from display of a previous error
			pack();
			setLocationRelativeTo(null);
			
		}
		
	};
	
	SaveScoreFrame() {
		
		if (cachedName != null) name.setText(cachedName);
		
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
				dispose();
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
		pack();
		setResizable(false);
		setLocationRelativeTo(null);		
		setVisible(true);
		
	}
	
}
