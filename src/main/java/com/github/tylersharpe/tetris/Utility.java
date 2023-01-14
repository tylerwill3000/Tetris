package com.github.tylersharpe.tetris;

import java.util.List;
import java.util.Random;

public final class Utility {

    private static final Random RANDOM = new Random();

    private Utility() {
    }

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

    static <T> T sample(T[] values) {
        return values[RANDOM.nextInt(values.length)];
    }

    public static String capitalize(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }

        if (str.length() > 1) {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
        } else {
            return String.valueOf(Character.toUpperCase(str.charAt(0)));
        }
    }
}
