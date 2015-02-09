package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import model.GameBoardModel;

/**
 * Displays score info [points, level, lines cleared, game time]
 * @author Tyler
 */
public class ScorePanel extends JPanel {
	
	private int _gameTimeMillis = 0;
	
	private JLabel _jlbScoreLabel = new JLabel("Score: 0", JLabel.CENTER);
	private JLabel _jlbTotalLinesLabel = new JLabel("Lines: 0", JLabel.CENTER);
	private JLabel _jlbLevelLabel = new JLabel("Level: 1", JLabel.CENTER);
	private JLabel _jlbTime = new JLabel("Time: 00:00", JLabel.CENTER);
	
	private Timer _gameTimer = new Timer(1000, new ActionListener() {
		
		private final static int MILLIS_PER_MINUTE = 60000;
		
		public void actionPerformed(ActionEvent e) {
			
			String totalMinutes = String.valueOf(_gameTimeMillis / MILLIS_PER_MINUTE);
			String totalSeconds = String.valueOf(_gameTimeMillis % MILLIS_PER_MINUTE / 1000);
			
			// Pad with opening zero if necessary
			if (totalMinutes.length() == 1) totalMinutes = "0" + totalMinutes;
			if (totalSeconds.length() == 1) totalSeconds = "0" + totalSeconds;
			
			_jlbTime.setText("Time: " + totalMinutes + ":" + totalSeconds);
			_gameTimeMillis += 1000;
		}
		
	});
	
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
	
	void startGameTimer() { _gameTimer.start(); }
	void stopGameTimer() { _gameTimer.stop(); }
	
	/**
	 * Resets current game time int to 0, the game time label text to '00:00' and then
	 * restarts the actual timer
	 */
	void restartGameTimer() {
		_jlbTime.setText("Time: 00:00");
		_gameTimeMillis = 0;
		_gameTimer.restart();
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
