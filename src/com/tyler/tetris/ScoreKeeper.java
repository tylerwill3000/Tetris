package com.tyler.tetris;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;

/**
 * Keeps track of game score data. Score data is composed of player score, player Level,
 * lines completed and total game time
 */
public final class ScoreKeeper extends EventSource {
	
	public static final int MAX_LEVEL = 11;
	
	// Index of the selected option in the difficulty list is used to index into this array to get lines per level
	private static final int[] LINES_PER_LEVEL = { 15, 20, 25 };
	
	private static final int[] INITIAL_TIMER_DELAYS = { 650, 600, 560 };
	
	private static final int[] TIMER_SPEEDUPS = { 40, 45, 55 };
	
	// Point values per line based on lines cleared
	private static final int[] LINE_POINTS_MAP = { 10, 15, 20, 30 };
	
	// Bonus points on level up when in time attack mode
	private static final int[] TIME_ATTACK_BONUSES_PER_LEVEL = { 150, 175, 200 };
	
	// Number of seconds allowed per line in time attack mode
	private static final int TIME_ATTACK_SECONDS_PER_LINE = 3;
	
	// Bonus points granted upon winning the game. Determined by difficulty
	private static final int[] WIN_BONUSES = { 500, 750, 1000 };
	
	// Maps block types to bonus points per line
	private static final Map<BlockType, Integer> SPECIAL_PIECE_BONUSES;
	static {
		Map<BlockType, Integer> temp = new HashMap<>();
		temp.put(BlockType.TWIN_PILLARS, 4);
		temp.put(BlockType.ROCKET, 6);
		temp.put(BlockType.DIAMOND, 10);
		SPECIAL_PIECE_BONUSES = Collections.unmodifiableMap(temp);
	}
	
	private int totalLinesCleared;
	private int score;
	private int level;
	private Integer difficulty;
	private Collection<BlockType> activeBlocks;
	private boolean timeAttack;
	private int gameTime;
	
	private Timer gameTimer = new Timer(1000, e -> {
		setTime(gameTime + 1);
		if (timeAttack && gameTime >= getCurrentTimeAttackLimit()) {
			publish("timeAttackFail", null);
			((Timer) e.getSource()).stop();
		}
	});
	
	public ScoreKeeper() {
		setDifficulty(0);
		setTimeAttack(false);
		resetScoreInfo();
	}
	
	public void resetScoreInfo() {
		setTime(0);
		setScore(0);
		setLevel(1);
		totalLinesCleared = 0;
	}
	
	public int getCurrentTimerDelay() {
		int initialDelay = INITIAL_TIMER_DELAYS[difficulty];
		int totalSpeedup = (level - 1) * TIMER_SPEEDUPS[difficulty];
		return initialDelay - totalSpeedup;
	}
	
	public int getGameTime() {
		return gameTime;
	}
	
	private void setTime(int time) {
		gameTime = time;
		publish("gameTimeChanged", gameTime);
	}
	
	public double getLinesClearedPercentage() {
		return 100.0 * (getCurrentLevelLinesCleared() * 1.0 / getLinesPerLevel());
	}
	
	public void pauseTimer() {
		this.gameTimer.stop();
	}
	
	public void startTimer() {
		this.gameTimer.start();
	}
	
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public void setActiveBlocks(Collection<BlockType> activeBlocks) {
		this.activeBlocks = activeBlocks;
	}

	public void setTimeAttack(boolean timeAttack) {
		this.timeAttack = timeAttack;
	}

	public boolean isTimeAttack() {
		return this.timeAttack;
	}
	
	public int getTotalLinesCleared() {
		return totalLinesCleared;
	}
	
	public int getLinesPerLevel() {
		return LINES_PER_LEVEL[difficulty];
	}
	
	public int getCurrentLevelLinesCleared() {
		int lastLevelThreshold = getLinesPerLevel() * (level - 1);
		return totalLinesCleared - lastLevelThreshold;
	}
	
	public int getLinesNeededFor(int level) {
		return level * getLinesPerLevel();
	}
	
	public int getCurrentLevelLinesNeeded() {
		return level * getLinesPerLevel();
	}
	
	public int getCurrentTimeAttackLimit() {
		return getCurrentLevelLinesNeeded() * TIME_ATTACK_SECONDS_PER_LINE;
	}
	
	public int getScore() {
		return score;
	}
	
	private void setScore(int newScore) {
		this.score = newScore;
		publish("scoreChanged", score);
	}
	
	public int getLevel() {
		return level;
	}
	
	private void setLevel(int newLevel) {
		this.level = newLevel;
		publish(newLevel == MAX_LEVEL ? "gameWon" : "levelChanged", level);
	}
	
	public static int getTimeAttackBonusPoints(int difficulty) {
		return TIME_ATTACK_BONUSES_PER_LEVEL[difficulty];
	}
	
	public int getTimeAttackBonusPoints() {
		return TIME_ATTACK_BONUSES_PER_LEVEL[difficulty];
	}
	
	public static int getSpecialPieceBonusPoints(BlockType pieceType) {
		return SPECIAL_PIECE_BONUSES.get(pieceType);
	}
	
	/**
	 * @return The current level after processing the completed lines
	 */
	public int increaseScore(int completedLines) {
		
		totalLinesCleared += completedLines;

		int newScore = this.score;
		
		// Points from raw lines cleared
		int linePoints = completedLines * LINE_POINTS_MAP[completedLines - 1];
		int difficultyBonus = completedLines * (5 * difficulty);
		newScore += (linePoints + difficultyBonus);
		
		 // Bonuses for special blocks
		if (activeBlocks != null) {
			newScore += BlockType.getSpecialBlocks()
			                     .stream()
			                     .filter(activeBlocks::contains)
			                     .mapToInt(special -> completedLines * SPECIAL_PIECE_BONUSES.get(special))
			                     .sum();
		}
		
		// Level ups
		int newLevel = this.level;
		while (totalLinesCleared >= getLinesNeededFor(newLevel)) {
			
			newLevel++;
			
			if (timeAttack) {
				newScore += TIME_ATTACK_BONUSES_PER_LEVEL[difficulty];
			}
			
			if (newLevel == MAX_LEVEL) {
				newScore += WIN_BONUSES[difficulty];
				break;
			}
			
		}

		if (newLevel > this.level) {
			setLevel(newLevel);
		}
		
		setScore(newScore);
		
		return level;
	}

}
