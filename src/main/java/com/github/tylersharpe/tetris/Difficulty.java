package com.github.tylersharpe.tetris;

import java.util.Map;

import static java.util.Map.entry;

public enum Difficulty {

    EASY("Easy", 15, 600, 60, Map.ofEntries(
        entry(TetronimoType.BOX, 14),
        entry(TetronimoType.INVERTED_L, 14),
        entry(TetronimoType.L, 14),
        entry(TetronimoType.INVERTED_S, 14),
        entry(TetronimoType.S, 14),
        entry(TetronimoType.LINE, 14),
        entry(TetronimoType.T, 14),
        entry(TetronimoType.TWIN_PILLARS, 11),
        entry(TetronimoType.WAVE, 10),
        entry(TetronimoType.ROCKET, 7),
        entry(TetronimoType.DIAMOND, 7)
    )),

    MEDIUM("Medium", 20, 575, 70, Map.ofEntries(
        entry(TetronimoType.BOX, 13),
        entry(TetronimoType.INVERTED_L, 14),
        entry(TetronimoType.L, 14),
        entry(TetronimoType.INVERTED_S, 15),
        entry(TetronimoType.S, 14),
        entry(TetronimoType.LINE, 13),
        entry(TetronimoType.T, 14),
        entry(TetronimoType.TWIN_PILLARS, 12),
        entry(TetronimoType.WAVE, 10),
        entry(TetronimoType.ROCKET, 7),
        entry(TetronimoType.DIAMOND, 7)
    )),

    HARD("Hard", 25, 550, 80, Map.ofEntries(
        entry(TetronimoType.BOX, 12),
        entry(TetronimoType.INVERTED_L, 15),
        entry(TetronimoType.L, 14),
        entry(TetronimoType.INVERTED_S, 15),
        entry(TetronimoType.S, 15),
        entry(TetronimoType.LINE, 12),
        entry(TetronimoType.T, 13),
        entry(TetronimoType.TWIN_PILLARS, 12),
        entry(TetronimoType.WAVE, 11),
        entry(TetronimoType.ROCKET, 8),
        entry(TetronimoType.DIAMOND, 8)
    ));

    public static final int TIMER_SPEEDUP = 55;

    private final String name;
    private final int linesPerLevel;
    private final int initialTimerDelay;
    private final int timeAttackSecondsPerLevel;
    private final Map<TetronimoType, Integer> spawnRates;

    Difficulty(String name, int linesPerLevel, int initialTimerDelay, int timeAttackSecondsPerLevel, Map<TetronimoType, Integer> spawnRates) {
        this.name = name;
        this.linesPerLevel = linesPerLevel;
        this.initialTimerDelay = initialTimerDelay;
        this.timeAttackSecondsPerLevel = timeAttackSecondsPerLevel;
        this.spawnRates = spawnRates;
    }

    public String getName() {
        return this.name;
    }

    public int getLinesPerLevel() {
        return linesPerLevel;
    }

    public int getInitialTimerDelay() {
        return initialTimerDelay;
    }

    public int getTimeAttackSecondsPerLevel() {
        return timeAttackSecondsPerLevel;
    }

    public int getSpawnRate(TetronimoType type) {
        return spawnRates.get(type);
    }

    @Override
    public String toString() {
        return getName();
    }
}
