package com.github.tylerwilliams.tetris;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class TetronimoConveyor {
    private final List<TetronimoType> tetronimoTypeSampleList = new ArrayList<>();
    private final Queue<Tetronimo> conveyor = new ArrayDeque<>();

    public Tetronimo next() {
        conveyor.offer(generateTetronimo());
        return conveyor.poll();
    }

    public Tetronimo peek() {
        return conveyor.peek();
    }

    void reset() {
        conveyor.clear();
        conveyor.add(generateTetronimo());
        conveyor.add(generateTetronimo());
    }

    void applySpawnRates(Difficulty difficulty) {
        tetronimoTypeSampleList.clear();

        for (TetronimoType tetronimoType : TetronimoType.values()) {
            int spawnRate = difficulty.getSpawnRate(tetronimoType);

            for (int i = 1; i <= spawnRate; i++) {
                tetronimoTypeSampleList.add(tetronimoType);
            }
        }
    }

    private Tetronimo generateTetronimo() {
        TetronimoType randomType = Utility.sample(tetronimoTypeSampleList);
        return new Tetronimo(randomType);
    }
}

