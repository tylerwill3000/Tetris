package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;

import model.PieceFactory.PieceType;
import ui.Controller;
import ui.GameFrame;
import util.FormatUtils;

/**
 * Keeps track of game score data. Score data is composed of player score, player Level,
 * lines completed and total game time
 * @author Tyler
 */
public final class ScoreModel {
	
	private ScoreModel() {}
	
	private static int _totalLinesCleared = 0;
	private static int _score = 0;
	private static int _level = 1;
	
	// Flag for whether or not the player just increased level.
	// Used by the GUI components to know when to process level up functions
	public static boolean _justLeveled = false;
	
	/**
	 * Keeps track of amount of time elapsed for this game session
	 */
	private static long _gameTimeMillis = 0;
	
	/**
	 * Increments time elapsed for this game session
	 */
	private static Timer _gameTimer = new Timer(1000, new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			_gameTimeMillis += 1000;
			
			GameFrame._scorePanel.refreshProgressBar();
			
			if (GameFrame._settingsPanel.timeAttackOn() && _gameTimeMillis > getCurrentTimeAttackLimit()) {
				Controller.processGameOver();
				GameFrame._scorePanel.setTimeLabel("Time's Up!!!");
				return;
			}
			
			GameFrame._scorePanel.setTimeLabel(getCurrentTimeLabel());
		
		}
		
	});
	
	// Bonus points granted upon winning the game. Determined by difficulty
	private static final int[] WIN_BONUSES = {500,750,1000};
	
	private static final Map<PieceType, Integer> SPECIAL_PIECE_BONUSES = new HashMap<>();
	static {
		SPECIAL_PIECE_BONUSES.put(PieceType.TWIN_PILLARS, 4);
		SPECIAL_PIECE_BONUSES.put(PieceType.ROCKET, 6);
		SPECIAL_PIECE_BONUSES.put(PieceType.DIAMOND, 10);
	}
	
	// Index of the selected option in the difficulty list is used
	// to index into this array to get lines per level
	private static final int[] LINES_PER_LEVEL = {15,20,25};
	
	// Point values per line based on lines cleared
	private static final int[] LINE_POINTS_MAP = {10,15,20,30};
	
	// Bonus points on level up when in time attack mode
	private static final int[] TIME_ATTACK_BONUSES_PER_LEVEL = {150,175,200};
	
	// Number of milliseconds allowed per line in time attack mode
	private static final int TIME_ATTACK_MILLIS_PER_LINE = 3000;
	
	public static int getTotalLinesCleared() { return _totalLinesCleared; }
	
	/**
	 * Returns lines per level for the current game session
	 */
	public static int getLinesPerLevel() {
		return LINES_PER_LEVEL[GameFrame._settingsPanel.getDifficulty()];
	}
	
	/**
	 * Returns number of lines cleared on the current level
	 */
	public static int getCurrentLevelLinesCleared() {
		int currentLevelThreshold = getLinesPerLevel() * (_level - 1);
		return _totalLinesCleared - currentLevelThreshold;
	}
	
	/**
	 * Returns number of lines needed for the current level 
	 */
	public static int getCurrentLevelLinesNeeded() {
		return _level * getLinesPerLevel();
	}
	
	/**
	 * Returns the max milliseconds for the current level for time attack mode
	 */
	public static int getCurrentTimeAttackLimit() {
		return getCurrentLevelLinesNeeded() * TIME_ATTACK_MILLIS_PER_LINE;
	}
	
	public static int getScore() { return _score; }
	public static int getLevel() { return _level; }
	public static long getCurrentGameTime() { return _gameTimeMillis; }
	public static int getTimeAttackBonusPoints(int diff) { return TIME_ATTACK_BONUSES_PER_LEVEL[diff]; }	
	
	public static int getSpecialPieceBonusPoints(PieceType pieceType) {
		return SPECIAL_PIECE_BONUSES.get(pieceType);
	}
	
	public static void startGameTimer() { _gameTimer.start(); }
	public static void stopGameTimer() { _gameTimer.stop(); }
	
	/**
	 * Resets the game time label text to '00:00' and then restarts the actual timer.
	 * If time attack mode is on, game timer will display the limit as well
	 */
	public static void restartGameTimer() {
		String timeLabel = "Time: 00:00";
		if (GameFrame._settingsPanel.timeAttackOn()) {
			timeLabel += " / " + FormatUtils.millisToString(getCurrentTimeAttackLimit()); 
		}
		GameFrame._scorePanel.setTimeLabel(timeLabel);
		_gameTimer.restart();
	}
	
	public static String getCurrentTimeLabel() {
		
		String timeLabel = "Time: " + FormatUtils.millisToString(_gameTimeMillis);
		
		if (GameFrame._settingsPanel.timeAttackOn()) {
			long levelLimitMillis = getCurrentTimeAttackLimit();
			timeLabel += " / " + FormatUtils.millisToString(levelLimitMillis);
		}
		
		return timeLabel;
		
	}
	
	static void increaseScore(int completedLines) {
		
		_totalLinesCleared += completedLines;
		
		int linePoints = completedLines * LINE_POINTS_MAP[completedLines-1];
		int difficultyBonus = completedLines * 5 * GameFrame._settingsPanel.getDifficulty();
		_score += (linePoints + difficultyBonus);
		
		// Add bonuses for all special pieces
		for (PieceType pieceType : PieceType.getSpecialPieces()) {
			if (PieceFactory.isPieceActive(pieceType))
				_score += (completedLines * SPECIAL_PIECE_BONUSES.get(pieceType));
		}
		
		// Process level ups
		while (_totalLinesCleared >= getCurrentLevelLinesNeeded()) {
			
			AudioManager.stopCurrentSoundtrack();
			_level++;
			
			// Add in bonus from time attack
			if (GameFrame._settingsPanel.timeAttackOn()) {
				_score += TIME_ATTACK_BONUSES_PER_LEVEL[GameFrame._settingsPanel.getDifficulty()];
				
				// Update based on new level parameters
				GameFrame._scorePanel.refreshProgressBar();
				GameFrame._scorePanel.setTimeLabel(getCurrentTimeLabel());
				
			}
			
			if (_level == 11) // Game complete
				_score += WIN_BONUSES[GameFrame._settingsPanel.getDifficulty()];
			else
				AudioManager.beginCurrentSoundtrack(); // Soundtrack for next level
			
			// Used to signal the UI components to initiate level up functions
			_justLeveled = true;
			
		}
		
	}
	
	/**
	 * Resets all scoring info back to initial values
	 */
	public static void reset() {
		_score = 0;
		_totalLinesCleared = 0;
		_gameTimeMillis = 0;
		_level = 1;
	}
	
}
