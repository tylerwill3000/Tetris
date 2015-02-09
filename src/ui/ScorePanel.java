package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ui.components.ProgressBar;
import util.FrameUtils;
import model.ScoreModel;

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
	
	private ProgressBar _linesClearedProgressBar = new ProgressBar(11, Color.GREEN) {
		public double getCurrentPercentage() {
			return ((double) ScoreModel.getCurrentLevelLinesCleared()) / ScoreModel.getLinesPerLevel();
		}
	};
	
	private ProgressBar _timerProgressBar = new ProgressBar(11, Color.YELLOW) {
		public double getCurrentPercentage() {
			return ((double) ScoreModel.getCurrentGameTime()) / ScoreModel.getCurrentTimeAttackLimit();
		}
	};
	
	private GridLayout _layout;
	
	ScorePanel() {
		
		setBorder(new TitledBorder("Scoring Info"));
		_layout = new GridLayout(6,1);
		setLayout(_layout);
		
		for (JLabel l : Arrays.asList(_jlbScoreLabel, _jlbTotalLinesLabel, _jlbLevelLabel, _jlbTime))
			l.setFont(GameFrame.LABEL_FONT);
		
		add(_jlbScoreLabel);
		add(_jlbLevelLabel);
		add(_jlbTotalLinesLabel);
		add(FrameUtils.nestInPanel(_linesClearedProgressBar));
		add(_jlbTime);
		
		add(FrameUtils.nestInPanel(_timerProgressBar));
		_timerProgressBar.setVisible(GameFrame._settingsPanel.timeAttackOn());
		
	}
	
	void showProgressBar() {
		_timerProgressBar.setVisible(true);
	}
	
	void hideProgressBar() {
		_timerProgressBar.setVisible(false);
	}
	
	void refreshScoreInfo() {
		_jlbScoreLabel.setText("Score: " + ScoreModel.getScore());
		_jlbTotalLinesLabel.setText("Lines: " + ScoreModel.getCurrentLevelLinesCleared() + " / " + ScoreModel.getLinesPerLevel());
		_linesClearedProgressBar.repaint();
		_jlbLevelLabel.setText("Level: " + ScoreModel.getLevel());
	}
	
	public void refreshProgressBar() { _timerProgressBar.repaint(); }
	
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
