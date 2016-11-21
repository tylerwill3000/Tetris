package tetris;

import static tetris.BlockType.BOX;
import static tetris.BlockType.DIAMOND;
import static tetris.BlockType.L_BLOCK_L;
import static tetris.BlockType.L_BLOCK_R;
import static tetris.BlockType.ROCKET;
import static tetris.BlockType.STRAIGHT_LINE;
import static tetris.BlockType.S_BLOCK_L;
import static tetris.BlockType.S_BLOCK_R;
import static tetris.BlockType.TWIN_PILLARS;
import static tetris.BlockType.T_BLOCK;

import java.util.HashMap;
import java.util.Map;

public enum Difficulty {
	
	EASY {{
		
		linesPerLevel = 15;
		initialTimerDelay = 600;
		timerSpeedup = 55;
		timeAttackBonus = 100;
		timeAttackSecondsPerLine = 4;
		winBonus = 250;
		
		type_spawn.put(BOX, 14);
		type_spawn.put(L_BLOCK_L, 14);
		type_spawn.put(L_BLOCK_R, 14);
		type_spawn.put(S_BLOCK_L, 14);
		type_spawn.put(S_BLOCK_R, 14);
		type_spawn.put(STRAIGHT_LINE, 14);
		type_spawn.put(T_BLOCK, 14);
		type_spawn.put(TWIN_PILLARS, 10);
		type_spawn.put(ROCKET, 8);
		type_spawn.put(DIAMOND, 5);
		
	}},
	
	MEDIUM {{
		
		linesPerLevel = 20;
		initialTimerDelay = 575;
		timerSpeedup = 55;
		timeAttackBonus = 150;
		timeAttackSecondsPerLine = 4;
		winBonus = 500;
		
		type_spawn.put(BOX, 13);
		type_spawn.put(L_BLOCK_L, 15);
		type_spawn.put(L_BLOCK_R, 14);
		type_spawn.put(S_BLOCK_L, 14);
		type_spawn.put(S_BLOCK_R, 15);
		type_spawn.put(STRAIGHT_LINE, 13);
		type_spawn.put(T_BLOCK, 14);
		type_spawn.put(TWIN_PILLARS, 10);
		type_spawn.put(ROCKET, 8);
		type_spawn.put(DIAMOND, 5);
		
	}},
	
	HARD {{
		
		linesPerLevel = 25;
		initialTimerDelay = 550;
		timerSpeedup = 55;
		timeAttackBonus = 200;
		timeAttackSecondsPerLine = 3;
		winBonus = 1000;
		
		type_spawn.put(BOX, 12);
		type_spawn.put(L_BLOCK_L, 15);
		type_spawn.put(L_BLOCK_R, 14);
		type_spawn.put(S_BLOCK_L, 15);
		type_spawn.put(S_BLOCK_R, 15);
		type_spawn.put(STRAIGHT_LINE, 11);
		type_spawn.put(T_BLOCK, 14);
		type_spawn.put(TWIN_PILLARS, 11);
		type_spawn.put(ROCKET, 8);
		type_spawn.put(DIAMOND, 6);
		
	}};

	protected int linesPerLevel;
	protected int initialTimerDelay;
	protected int timerSpeedup;
	protected int timeAttackBonus;
	protected int timeAttackSecondsPerLine;
	protected int winBonus;
	protected int linesClearedBonus;
	protected Map<BlockType, Integer> type_spawn = new HashMap<>();
	
	public int getLinesPerLevel() {
		return linesPerLevel;
	}
	
	public int getInitialTimerDelay() {
		return initialTimerDelay;
	}
	
	public int getTimerSpeedup() {
		return timerSpeedup;
	}
	
	public int getTimeAttackBonus() {
		return timeAttackBonus;
	}
	
	public int getTimeAttackSecondsPerLine() {
		return timeAttackSecondsPerLine;
	}
	
	public int getWinBonus() {
		return winBonus;
	}
	
	public int getLinesClearedBonus() {
		return linesClearedBonus;
	}
	
	public int getSpawnRate(BlockType type) {
		return type_spawn.get(type);
	}
	
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
	
	
}
