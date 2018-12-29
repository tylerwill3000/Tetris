package com.github.tylersharpe.tetris;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public enum Difficulty {

  EASY(15, 600, 100, 60, 250, Utility.map(
    Block.Type.BOX, 14,
    Block.Type.L_BLOCK_L, 14,
    Block.Type.L_BLOCK_R, 14,
    Block.Type.S_BLOCK_L, 14,
    Block.Type.S_BLOCK_R, 14,
    Block.Type.STRAIGHT_LINE, 14,
    Block.Type.T_BLOCK, 14,
    Block.Type.TWIN_PILLARS, 11,
    Block.Type.WAVE, 10,
    Block.Type.ROCKET, 7,
    Block.Type.DIAMOND, 7
  )),

  MEDIUM(20, 575, 150, 70, 500, Utility.map(
    Block.Type.BOX, 13,
    Block.Type.L_BLOCK_L, 14,
    Block.Type.L_BLOCK_R, 14,
    Block.Type.S_BLOCK_L, 15,
    Block.Type.S_BLOCK_R, 14,
    Block.Type.STRAIGHT_LINE, 13,
    Block.Type.T_BLOCK, 14,
    Block.Type.TWIN_PILLARS, 12,
    Block.Type.WAVE, 10,
    Block.Type.ROCKET, 7,
    Block.Type.DIAMOND, 7
  )),

  HARD(25, 550, 200, 80, 1000, Utility.map(
    Block.Type.BOX, 12,
    Block.Type.L_BLOCK_L, 15,
    Block.Type.L_BLOCK_R, 14,
    Block.Type.S_BLOCK_L, 15,
    Block.Type.S_BLOCK_R, 15,
    Block.Type.STRAIGHT_LINE, 12,
    Block.Type.T_BLOCK, 13,
    Block.Type.TWIN_PILLARS, 12,
    Block.Type.WAVE, 11,
    Block.Type.ROCKET, 8,
    Block.Type.DIAMOND, 8
  ));

  public static final int TIMER_SPEEDUP = 55;

  protected int linesPerLevel;
  protected int initialTimerDelay;
  protected int timeAttackBonus;
  protected int timeAttackSecondsPerLevel;
  protected int winBonus;
  protected Map<Block.Type, Integer> type_spawn;

  Difficulty(int linesPerLevel, int initialTimerDelay, int timeAttackBonus, int timeAttackSecondsPerLevel, int winBonus, Map<Block.Type, Integer> type_spawn) {
    this.linesPerLevel = linesPerLevel;
    this.initialTimerDelay = initialTimerDelay;
    this.timeAttackBonus = timeAttackBonus;
    this.timeAttackSecondsPerLevel = timeAttackSecondsPerLevel;
    this.winBonus = winBonus;
    this.type_spawn = Collections.unmodifiableMap(type_spawn);
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

  public int getSpawnRate(Block.Type type) {
    return type_spawn.get(type);
  }

  public String toString() {
    return name().charAt(0) + name().substring(1).toLowerCase();
  }

}
