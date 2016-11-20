package com.tyler.tetris;

public enum Difficulty {
	
	EASY {{
		linesPerLevel = 15;
		initialTimerDelay = 650;
		timerSpeedup = 40;
		timeAttackBonus = 150;
		winBonus = 500;
	}},
	
	MEDIUM {{
		linesPerLevel = 20;
		initialTimerDelay = 600;
		timerSpeedup = 45;
		timeAttackBonus = 175;
		winBonus = 750;
	}},
	
	HARD {{
		linesPerLevel = 25;
		initialTimerDelay = 560;
		timerSpeedup = 55;
		timeAttackBonus = 200;
		winBonus = 1000;
	}};

	protected int linesPerLevel;
	protected int initialTimerDelay;
	protected int timerSpeedup;
	protected int timeAttackBonus;
	protected int winBonus;
	protected int linesClearedBonus;
	
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
	
	public int getWinBonus() {
		return winBonus;
	}
	
	public int getLinesClearedBonus() {
		return linesClearedBonus;
	}
	
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
	
}
