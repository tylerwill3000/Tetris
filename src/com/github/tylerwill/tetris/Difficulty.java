package com.github.tylerwill.tetris;

import java.util.Map;

import static com.github.tylerwill.tetris.BlockType.*;
import static com.github.tylerwill.tetris.Utility.map;

public enum Difficulty {

  EASY(15, 600, 100, 4, 250, map(
    BOX, 14,
    L_BLOCK_L, 14,
    L_BLOCK_R, 14,
    S_BLOCK_L, 14,
    S_BLOCK_R, 14,
    STRAIGHT_LINE, 14,
    T_BLOCK, 14,
    TWIN_PILLARS, 10,
    ROCKET, 8,
    DIAMOND, 5
  )),

  MEDIUM(20, 575, 150, 4, 500, map(
    BOX, 13,
    L_BLOCK_L, 15,
    L_BLOCK_R, 14,
    S_BLOCK_L, 14,
    S_BLOCK_R, 15,
    STRAIGHT_LINE, 13,
    T_BLOCK, 14,
    TWIN_PILLARS, 10,
    ROCKET, 8,
    DIAMOND, 5
  )),

  HARD(25, 550, 200, 3, 1000, map(
    BOX, 12,
    L_BLOCK_L, 15,
    L_BLOCK_R, 14,
    S_BLOCK_L, 15,
    S_BLOCK_R, 15,
    STRAIGHT_LINE, 11,
    T_BLOCK, 14,
    TWIN_PILLARS, 11,
    ROCKET, 8,
    DIAMOND, 6
  ));

  public static final int TIMER_SPEEDUP = 55;

  protected int linesPerLevel;
  protected int initialTimerDelay;
  protected int timeAttackBonus;
  protected int timeAttackSecondsPerLine;
  protected int winBonus;
  protected Map<BlockType, Integer> type_spawn;

  Difficulty(int linesPerLevel, int initialTimerDelay, int timeAttackBonus, int timeAttackSecondsPerLine, int winBonus, Map<BlockType, Integer> type_spawn) {
    this.linesPerLevel = linesPerLevel;
    this.initialTimerDelay = initialTimerDelay;
    this.timeAttackBonus = timeAttackBonus;
    this.timeAttackSecondsPerLine = timeAttackSecondsPerLine;
    this.winBonus = winBonus;
    this.type_spawn = type_spawn;
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

  public int getTimeAttackSecondsPerLine() {
    return timeAttackSecondsPerLine;
  }

  public int getWinBonus() {
    return winBonus;
  }

  public int getSpawnRate(BlockType type) {
    return type_spawn.get(type);
  }

  public String toString() {
    return name().charAt(0) + name().substring(1).toLowerCase();
  }


}
