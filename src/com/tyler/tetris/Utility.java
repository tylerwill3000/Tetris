package com.tyler.tetris;

import java.util.List;
import java.util.Random;

public final class Utility {

	private static final Random RANDOM = new Random();
	
	private Utility() {}
	
	public static <T> T sample(List<T> items) {
		return items.get(RANDOM.nextInt(items.size()));
	}

	public static <T> T sample(T[] values) {
		return values[RANDOM.nextInt(values.length)];
	}
	
}
