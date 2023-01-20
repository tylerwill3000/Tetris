package com.github.tylersharpe.tetris;

import java.util.*;

public final class TetronimoConveyor {

    private final Set<TetronimoType> enabledTetronimoTypes = EnumSet.noneOf(TetronimoType.class);
    private final List<TetronimoType> tetronimoTypeSampleList = new ArrayList<>();
    private final Queue<Tetronimo> conveyor = new ArrayDeque<>();
    private final Set<TetronimoType> enabledSpecialTetronimoTypes = EnumSet.noneOf(TetronimoType.class);

    public Tetronimo next() {
        conveyor.offer(generateBlock());
        return conveyor.poll();
    }

    public Tetronimo peek() {
        return conveyor.peek();
    }

    void reset() {
        conveyor.clear();
        conveyor.add(generateBlock());
        conveyor.add(generateBlock());
    }

    void setDifficulty(Difficulty difficulty) {
        tetronimoTypeSampleList.clear();

        for (TetronimoType defaultTetronimoType : TetronimoType.DEFAULT_TYPES) {
            enableTetronimoType(difficulty, defaultTetronimoType);
        }
    }

    public void enableTetronimoType(Difficulty difficulty, TetronimoType tetronimoType) {
        if (tetronimoType.isSpecial()) {
            enabledSpecialTetronimoTypes.add(tetronimoType);
        }
        enabledTetronimoTypes.add(tetronimoType);
        tetronimoTypeSampleList.removeIf(sampleType -> sampleType == tetronimoType);

        int spawnRate = difficulty.getSpawnRate(tetronimoType);
        for (int i = 1; i <= spawnRate; i++) {
            tetronimoTypeSampleList.add(tetronimoType);
        }
    }

    public void disableTetronimoType(TetronimoType typeToDisable) {
        enabledSpecialTetronimoTypes.remove(typeToDisable);
        enabledTetronimoTypes.remove(typeToDisable);
        tetronimoTypeSampleList.removeIf(type -> type == typeToDisable);
    }

    Set<TetronimoType> getEnabledSpecialTetronimoTypes() {
        return enabledSpecialTetronimoTypes;
    }

    public boolean isEnabled(TetronimoType type) {
        return enabledTetronimoTypes.contains(type);
    }

    private Tetronimo generateBlock() {
        return new Tetronimo(Utility.sample(tetronimoTypeSampleList));
    }

}

