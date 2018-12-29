package com.github.tylersharpe.tetris;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Utility {

  private static final Random RANDOM = new Random();

  private Utility() {}

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

  @SuppressWarnings("unchecked")
  static <K, V> Map<K, V> map(Object... entries) {
    if (entries.length % 2 != 0) {
      throw new IllegalArgumentException("Number of entry arguments must be divisible by 2");
    }
    Map<K, V> map = new HashMap<>(entries.length / 2);
    for (int i = 0; i < entries.length - 1; i += 2) {
      map.put((K) entries[i], (V) entries[i + 1]);
    }
    return map;
  }

}
