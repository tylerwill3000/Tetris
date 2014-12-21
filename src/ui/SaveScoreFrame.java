package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.DBComm;
import model.GameBoardModel;

// Frame prompting the user whether they want to save their score
public class SaveScoreFrame extends JFrame {
	
	public final static int NAME_LENGTH = 20;
	
	private static String cachedName = null;
	
	private JPanel buttonPanel;
	
	private JLabel attainedScore = new JLabel();
	
	private JTextField name = new JTextField(8);
	
	private JButton saveScore = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	
	private ActionListener saveScoreListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			if (name.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "You must enter a name to save your score");
				return;
			}
			
			cachedName = name.getText();
			
			int rank = 0;
			try {
				
				rank = DBComm.writeScore(
					name.getText(),
					GameBoardModel.getScore(),
					GameBoardModel.getLevel(),
					GameBoardModel.getLinesCompleted(),
					GameFrame.settingsPanel.getDifficulty()
				);
				
			}
			catch (ClassNotFoundException | SQLException e1) {
				JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage());
				return;
			}
			
			JOptionPane.showMessageDialog(null, "Score saved! Your rank: " + rank);
			dispose();
			
			// After saving score, display high scores frame with maximum record selection
			HighScoreFrame.cachedSelectedDifficulty = 3;
			HighScoreFrame.cachedSelectedRecordCount = 3;
			new HighScoreFrame();
			
		}
		
	};
	
	SaveScoreFrame() {
		
		// Validates name length
		name.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (name.getText().length() > NAME_LENGTH)
					name.setText(name.getText().substring(0, NAME_LENGTH));
			}
		});
		
		if (cachedName != null) name.setText(cachedName);
		
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
		
		
		
		FrameUtils.setIcon(this, "save-icon.png");
		setTitle("Save Score");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
}
