package com.tyler.tetris;

public final class Utility {

	private Utility() {}
	
	public static int randInRange(int min, int max) {
		return (int)(Math.random() * (max - min + 1)) + min;
	}
	
}
