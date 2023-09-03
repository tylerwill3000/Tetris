package com.github.tylerwilliams.tetris;

import java.util.List;
import java.util.Random;

public final class Utility {
    private static final Random RANDOM = new Random();

    public static String formatSeconds(long seconds) {
        long totalMinutes = seconds / 60;
        long secondsLeftover = seconds % 60;
        return (totalMinutes < 10 ? "0" : "") + totalMinutes +
                ":" +
                (secondsLeftover < 10 ? "0" : "") + secondsLeftover;
    }

    static <T> T sample(List<T> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
