package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.GameBoardModel;

// Holds all scoring info
@SuppressWarnings("serial")
public class ScorePanel extends JPanel {
	
	private JLabel scoreLabel = new JLabel("Score: 0", JLabel.CENTER);
	private JLabel totalLinesLabel = new JLabel("Lines: 0", JLabel.CENTER);
	private JLabel levelLabel = new JLabel("Level: 1", JLabel.CENTER);
	
	private final FlashText FLASH_LEVEL_UP = new FlashText(Color.YELLOW);
	private final FlashText FLASH_WIN = new FlashText("You Win!!!", Color.YELLOW);
	private final FlashText FLASH_GAME_OVER = new FlashText("Game Over!!!", Color.RED);
	
	public ScorePanel() {
		
		setLayout(new GridLayout(3,1));
		
		scoreLabel.setFont(GameFrame.LABEL_FONT);
		totalLinesLabel.setFont(GameFrame.LABEL_FONT);
		levelLabel.setFont(GameFrame.LABEL_FONT);
		
		add(scoreLabel);
		add(totalLinesLabel);
		add(levelLabel);
		
	}
	
	public void refreshScoreInfo() {
		scoreLabel.setText("Score: " + GameBoardModel.getScore());
		totalLinesLabel.setText("Lines: " + GameBoardModel.getLinesCompleted());
		levelLabel.setText("Level: " + GameBoardModel.getLevel());
	}

	public void flashLevelLabel() { GameFrame.THREAD_EXECUTOR.execute(FLASH_LEVEL_UP); }
	
	public void flashWinMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_WIN); }
	
	public void flashGameOverMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_GAME_OVER); }
	
	// Thread task that can be configured to flash the level text a certain color
	private class FlashText implements Runnable {
		
		private String textToFlash;
		private Color flashColor;
		
		// Create a task to flash the default score label (used on level up)
		public FlashText(Color flashColor) {
			this.textToFlash = levelLabel.getText(); // Text doesn't change
			this.flashColor = flashColor;
		}
		
		// Create a task to flash a customized string
		public FlashText(String textToFlash, Color flashColor) {
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
