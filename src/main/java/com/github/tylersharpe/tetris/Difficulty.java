package com.github.tylersharpe.tetris;

import java.util.Map;

import static java.util.Map.entry;

public enum Difficulty {

  EASY(15, 600, 100, 60, 250, Map.ofEntries(
    entry(BlockType.BOX, 14),
    entry(BlockType.L_BLOCK_L, 14),
    entry(BlockType.L_BLOCK_R, 14),
    entry(BlockType.S_BLOCK_L, 14),
    entry(BlockType.S_BLOCK_R, 14),
    entry(BlockType.STRAIGHT_LINE, 14),
    entry(BlockType.T_BLOCK, 14),
    entry(BlockType.TWIN_PILLARS, 11),
    entry(BlockType.WAVE, 10),
    entry(BlockType.ROCKET, 7),
    entry(BlockType.DIAMOND, 7)
  )),

  MEDIUM(20, 575, 150, 70, 500, Map.ofEntries(
    entry(BlockType.BOX, 13),
    entry(BlockType.L_BLOCK_L, 14),
    entry(BlockType.L_BLOCK_R, 14),
    entry(BlockType.S_BLOCK_L, 15),
    entry(BlockType.S_BLOCK_R, 14),
    entry(BlockType.STRAIGHT_LINE, 13),
    entry(BlockType.T_BLOCK, 14),
    entry(BlockType.TWIN_PILLARS, 12),
    entry(BlockType.WAVE, 10),
    entry(BlockType.ROCKET, 7),
    entry(BlockType.DIAMOND, 7)
  )),

  HARD(25, 550, 200, 80, 1000, Map.ofEntries(
    entry(BlockType.BOX, 12),
    entry(BlockType.L_BLOCK_L, 15),
    entry(BlockType.L_BLOCK_R, 14),
    entry(BlockType.S_BLOCK_L, 15),
    entry(BlockType.S_BLOCK_R, 15),
    entry(BlockType.STRAIGHT_LINE, 12),
    entry(BlockType.T_BLOCK, 13),
    entry(BlockType.TWIN_PILLARS, 12),
    entry(BlockType.WAVE, 11),
    entry(BlockType.ROCKET, 8),
    entry(BlockType.DIAMOND, 8)
  ));

  public static final int TIMER_SPEEDUP = 55;

  private final int linesPerLevel;
  private final int initialTimerDelay;
  private final int timeAttackBonus;
  private final int timeAttackSecondsPerLevel;
  private final int winBonus;
  private final Map<BlockType, Integer> spawnRates;

  Difficulty(int linesPerLevel, int initialTimerDelay, int timeAttackBonus, int timeAttackSecondsPerLevel, int winBonus, Map<BlockType, Integer> spawnRates) {
    this.linesPerLevel = linesPerLevel;
    this.initialTimerDelay = initialTimerDelay;
    this.timeAttackBonus = timeAttackBonus;
    this.timeAttackSecondsPerLevel = timeAttackSecondsPerLevel;
    this.winBonus = winBonus;
    this.spawnRates = spawnRates;
  }

  public int getLinesPerLevel() {
    return linesPerLevel;
  }

  public int getInitialTimerDelay() {
    return initialTimerDelay;
  }

  public int getTimeAttackBonus() {
    return timeAttackBonus;
  }

  public int getTimeAttackSecondsPerLevel() {
    return timeAttackSecondsPerLevel;
  }

  public int getWinBonus() {
    return winBonus;
  }

  public int getSpawnRate(BlockType type) {
    return spawnRates.get(type);
  }

  public static Difficulty fromDisplay(String display) {
    for (var difficulty : values()) {
      if (difficulty.getDisplay().equals(display)) {
        return difficulty;
      }
    }
    throw new IllegalArgumentException("No difficulty exists for display value '" + display + "'");
  }

  public String getDisplay() {
    return name().charAt(0) + name().substring(1).toLowerCase();
  }

  public String toString() {
    return getDisplay();
  }

}
