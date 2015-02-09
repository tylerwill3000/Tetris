package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.GameBoardModel;

/**
 * Displays score info [points, level, lines cleared, game time]
 * @author Tyler
 */
public class ScorePanel extends JPanel {
	
	private JLabel _jlbScoreLabel = new JLabel("Score: 0", JLabel.CENTER);
	private JLabel _jlbTotalLinesLabel = new JLabel("Lines: 0", JLabel.CENTER);
	private JLabel _jlbLevelLabel = new JLabel("Level: 1", JLabel.CENTER);
	private JLabel _jlbTime = new JLabel("Time: 00:00", JLabel.CENTER);
	
	private final FlashTextTask FLASH_WIN = new FlashTextTask("You Win!!!", Color.YELLOW);
	private final FlashTextTask FLASH_GAME_OVER = new FlashTextTask("Game Over!!!", Color.RED);
	
	ScorePanel() {
		
		setLayout(new GridLayout(4,1));
		setBorder(new TitledBorder("Scoring Info"));
		
		for (JLabel l : Arrays.asList(_jlbScoreLabel, _jlbTotalLinesLabel, _jlbLevelLabel, _jlbTime)) {
			l.setFont(GameFrame.LABEL_FONT);
			add(l);
		}
		
	}
	
	/**
	 *  Pulls the current data from the GameBoardModel and then displays it
	 */
	void refreshScoreInfo() {
		_jlbScoreLabel.setText("Score: " + GameBoardModel.getScore());
		_jlbTotalLinesLabel.setText("Lines: " + GameBoardModel.getLinesCompleted());
		_jlbLevelLabel.setText("Level: " + GameBoardModel.getLevel());
	}
	
	public void setTimeLabel(String label) {
		_jlbTime.setText(label);
	}
	
	/**
	 * Can't declare a static task for flashing the level label since the label changes
	 * each time!
	 */
	void flashLevelLabel() {
		FlashTextTask task = new FlashTextTask(_jlbLevelLabel.getText(), Color.YELLOW);
		GameFrame.THREAD_EXECUTOR.execute(task);
	}
	
	void flashWinMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_WIN); }
	
	void flashGameOverMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_GAME_OVER); }
	
	/**
	 *  Thread task that can be configured to flash the level text a certain color
	 * @author Tyler
	 */
	private class FlashTextTask implements Runnable {
		
		private String textToFlash;
		private Color flashColor;
		
		// Create a task to flash a customized string
		private FlashTextTask(String textToFlash, Color flashColor) {
			this.textToFlash = textToFlash;
			this.flashColor = flashColor;
		}
		
		public void run() {
			
			_jlbLevelLabel.setText(textToFlash);
			
			try {
				
				for (int i = 1; i <= 60; i++) {
					_jlbLevelLabel.setForeground(i % 2 == 0 ? Color.BLACK : flashColor);
					Thread.sleep(50);
				}
				
			}
			catch (InterruptedException e) {} // Munch
			
		}
		
	}
	
}
