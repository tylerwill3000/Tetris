package com.tyler.tetris;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of game score data. Score data is composed of player score, player Level,
 * lines completed and total game time
 * @author Tyler
 */
public final class ScoreKeeper {
	
	// Index of the selected option in the difficulty list is used to index into this array to get lines per level
	private static final int[] LINES_PER_LEVEL = { 15, 20, 25 };
	
	// Point values per line based on lines cleared
	private static final int[] LINE_POINTS_MAP = { 10, 15, 20, 30 };
	
	// Bonus points on level up when in time attack mode
	private static final int[] TIME_ATTACK_BONUSES_PER_LEVEL = { 150, 175, 200 };
	
	// Number of milliseconds allowed per line in time attack mode
	private static final int TIME_ATTACK_MILLIS_PER_LINE = 3000;
	
	// Bonus points granted upon winning the game. Determined by difficulty
	private static final int[] WIN_BONUSES = { 500, 750, 1000 };
	
	public static final int MAX_LEVEL = 11;
	
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
	
	public ScoreKeeper() {
		this.totalLinesCleared = 0;
		this.score = 0;
		this.level = 1;
		this.difficulty = 0;
		this.timeAttack = false;
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
	
	public int getCurrentLevelLinesNeeded() {
		return level * getLinesPerLevel();
	}
	
	public int getCurrentTimeAttackLimit() {
		return getCurrentLevelLinesNeeded() * TIME_ATTACK_MILLIS_PER_LINE;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getLevel() {
		return level;
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
	
	/*
	 * Resets the game time label text to '00:00' and then restarts the actual timer.
	 * If time attack mode is on, game timer will display the limit as well
	 *
	public static void restartGameTimer() {
		String timeLabel = "Time: 00:00";
		if (GameFrame._settingsPanel.timeAttackOn()) {
			timeLabel += " / " + FormatUtils.millisToString(getCurrentTimeAttackLimit()); 
		}
		GameFrame._scorePanel.setTimeLabel(timeLabel);
		_gameTimer.restart();
	}
	*/
	
	/*
	public String getCurrentTimeLabel() {
		
		String timeLabel = "Time: " + FormatUtils.millisToString(totalElapsedTime);
		
		if (timeAttack) {
			long levelLimitMillis = getCurrentTimeAttackLimit();
			timeLabel += " / " + FormatUtils.millisToString(levelLimitMillis);
		}
		
		return timeLabel;
	}
	*/
	
	/**
	 * @return The current level after processing the completed lines
	 */
	public int increaseScore(int completedLines) {
		
		totalLinesCleared += completedLines;

		{ // Points from raw lines cleared
			int linePoints = completedLines * LINE_POINTS_MAP[completedLines - 1];
			int difficultyBonus = completedLines * (5 * difficulty);
			score += (linePoints + difficultyBonus);
		}
		
		 // Bonuses for special blocks
		if (activeBlocks != null) {
			score += BlockType.getSpecialBlocks()
			                  .stream()
			                  .filter(activeBlocks::contains)
			                  .mapToInt(special -> completedLines * SPECIAL_PIECE_BONUSES.get(special))
			                  .sum();
		}
		
		{ // Level ups
			while (totalLinesCleared >= getCurrentLevelLinesNeeded()) {
				
				level++;
				if (timeAttack) {
					score += TIME_ATTACK_BONUSES_PER_LEVEL[difficulty];
				}
				
				if (level == MAX_LEVEL) {
					score += WIN_BONUSES[difficulty];
					break;
				}
				
			}
			
			return level;
		}
		
	}
	
}
