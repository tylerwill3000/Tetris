package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.GameBoardModel;

// Dedicated to the "View" portion of the scoring data
public class ScorePanel extends JPanel {
	
	private JLabel scoreLabel = new JLabel("Score: 0", JLabel.CENTER);
	private JLabel totalLinesLabel = new JLabel("Lines: 0", JLabel.CENTER);
	private JLabel levelLabel = new JLabel("Level: 1", JLabel.CENTER);
	
	private final FlashTextTask FLASH_WIN = new FlashTextTask("You Win!!!", Color.YELLOW);
	private final FlashTextTask FLASH_GAME_OVER = new FlashTextTask("Game Over!!!", Color.RED);
	
	ScorePanel() {
		
		setLayout(new GridLayout(3,1));
		setBorder(new TitledBorder("Scoring Info"));
		
		for (JLabel l : new JLabel[]{scoreLabel, totalLinesLabel, levelLabel}) {
			l.setFont(GameFrame.LABEL_FONT);
			add(l);
		}
		
	}
	
	// Pulls the current data from the GameBoardModel and then displays it
	public void refreshScoreInfo() {
		scoreLabel.setText("Score: " + GameBoardModel.getScore());
		totalLinesLabel.setText("Lines: " + GameBoardModel.getLinesCompleted());
		levelLabel.setText("Level: " + GameBoardModel.getLevel());
	}
	
	// Can't declare a static task for flashing the level label since the label changes
	// each time!
	public void flashLevelLabel() {
		FlashTextTask task = new FlashTextTask(levelLabel.getText(), Color.YELLOW);
		GameFrame.THREAD_EXECUTOR.execute(task);
	}
	
	public void flashWinMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_WIN); }
	
	public void flashGameOverMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_GAME_OVER); }
	
	// Thread task that can be configured to flash the level text a certain color
	private class FlashTextTask implements Runnable {
		
		private String textToFlash;
		private Color flashColor;
		
		// Create a task to flash a customized string
		public FlashTextTask(String textToFlash, Color flashColor) {
			this.textToFlash = textToFlash;
			this.flashColor = flashColor;
		}
		
		public void run() {
			
			levelLabel.setText(textToFlash);
			
			try {
				
				for (int i = 1; i <= 60; i++) {
					levelLabel.setForeground(i % 2 == 0 ? Color.BLACK : flashColor);
					Thread.sleep(50);
				}
				
			}
			catch (InterruptedException e) {}
			
		}
		
	}
	
}
