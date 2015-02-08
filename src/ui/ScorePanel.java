package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.GameBoardModel;

/**
 * Displays score info [points, level, lines cleared]
 * @author Tyler
 */
public class ScorePanel extends JPanel {
	
	private JLabel _jlbScoreLabel = new JLabel("Score: 0", JLabel.CENTER);
	private JLabel _jlbTotalLinesLabel = new JLabel("Lines: 0", JLabel.CENTER);
	private JLabel _jlbLevelLabel = new JLabel("Level: 1", JLabel.CENTER);
	
	private final FlashTextTask FLASH_WIN = new FlashTextTask("You Win!!!", Color.YELLOW);
	private final FlashTextTask FLASH_GAME_OVER = new FlashTextTask("Game Over!!!", Color.RED);
	
	ScorePanel() {
		
		setLayout(new GridLayout(3,1));
		setBorder(new TitledBorder("Scoring Info"));
		
		for (JLabel l : new JLabel[]{_jlbScoreLabel, _jlbTotalLinesLabel, _jlbLevelLabel}) {
			l.setFont(GameFrame.LABEL_FONT);
			add(l);
		}
		
	}
	
	/**
	 *  Pulls the current data from the GameBoardModel and then displays it
	 */
	public void refreshScoreInfo() {
		_jlbScoreLabel.setText("Score: " + GameBoardModel.getScore());
		_jlbTotalLinesLabel.setText("Lines: " + GameBoardModel.getLinesCompleted());
		_jlbLevelLabel.setText("Level: " + GameBoardModel.getLevel());
	}
	
	/**
	 * Can't declare a static task for flashing the level label since the label changes
	 * each time!
	 */
	public void flashLevelLabel() {
		FlashTextTask task = new FlashTextTask(_jlbLevelLabel.getText(), Color.YELLOW);
		GameFrame.THREAD_EXECUTOR.execute(task);
	}
	
	public void flashWinMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_WIN); }
	
	public void flashGameOverMessage() { GameFrame.THREAD_EXECUTOR.execute(FLASH_GAME_OVER); }
	
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
