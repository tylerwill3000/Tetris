package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;

import model.PieceFactory.PieceType;
import ui.GameFrame;
import util.FormatUtils;

/**
 * Keeps track of game score data. Score data is composed of player score, player Level,
 * lines completed and total game time
 * @author Tyler
 */
public final class ScoreModel {
	
	private ScoreModel() {}
	
	/**
	 * Keeps track of amount of time elapsed for this game session
	 */
	private static long _gameTimeMillis = 0;
	
	/**
	 * Increments time elapsed for this game session
	 */
	private static Timer _gameTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			GameFrame._scorePanel.setTimeLabel("Time: " + FormatUtils.millisToString(_gameTimeMillis));
			_gameTimeMillis += 1000;
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
	
	private static int _linesCompleted = 0;
	private static int _score = 0;
	private static int _level = 0;
	
	// Flag for whether or not the player just increased level.
	// Used by the GUI components to know when to process level up functions
	public static boolean _justLeveled = false;
	
	public static int getLinesCompleted() { return _linesCompleted; }
	public static int getScore() { return _score; }
	public static int getLevel() { return _level; }
	public static long getCurrentGameTime() { return _gameTimeMillis; }
	
	public static int getSpecialPieceBonusPoints(PieceType pieceType) {
		return SPECIAL_PIECE_BONUSES.get(pieceType);
	}
	
	public static void startGameTimer() { _gameTimer.start(); }
	public static void stopGameTimer() { _gameTimer.stop(); }
	
	/**
	 * Resets the game time label text to '00:00' and then restarts the actual timer
	 */
	public static void restartGameTimer() {
		GameFrame._scorePanel.setTimeLabel("Time: 00:00");
		_gameTimer.restart();
	}
	
	static void increaseScore(int completedLines) {
		
		_linesCompleted += completedLines;
		
		int linePoints = completedLines * LINE_POINTS_MAP[completedLines-1];
		int difficultyBonus = completedLines * 5 * GameFrame._settingsPanel.getDifficulty();
		_score += (linePoints + difficultyBonus);
		
		// Add bonuses for all special pieces
		for (PieceType pieceType : PieceType.getSpecialPieces()) {
			if (PieceFactory.isPieceActive(pieceType))
				_score += (completedLines * SPECIAL_PIECE_BONUSES.get(pieceType));
		}
		
		// Process level ups
		while (_linesCompleted >= _level * LINES_PER_LEVEL[GameFrame._settingsPanel.getDifficulty()]) {
			
			AudioManager.stopCurrentSoundtrack();
			_level++;
			
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
		_linesCompleted = 0;
		_gameTimeMillis = 0;
		_level = 1;
	}
	
}
