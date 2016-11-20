package com.tyler.tetris.score;

import java.time.LocalDateTime;
import java.util.Objects;

import com.tyler.tetris.Difficulty;

public class HighScore implements Comparable<HighScore> {

	public final String name;
	public final int score;
	public final long gameTime;
	public final Difficulty difficulty;
	public final int linesCleared;
	public final int maxLevel;
	public final LocalDateTime timeAchieved;
	
	public HighScore(String name, int score, long gameTime, Difficulty difficulty, int linesCleared, int maxLevel) {
		this(name, score, gameTime, difficulty, linesCleared, maxLevel, LocalDateTime.now());
	}
	
	HighScore(String name, int score, long gameTime, Difficulty difficulty, int linesCleared, int maxLevel, LocalDateTime timeAchieved) {
		this.name = name;
		this.score = score;
		this.gameTime = gameTime;
		this.difficulty = difficulty;
		this.linesCleared = linesCleared;
		this.maxLevel = maxLevel;
		this.timeAchieved = timeAchieved;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, score, gameTime, difficulty, linesCleared, maxLevel, timeAchieved);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HighScore) {
			HighScore other = (HighScore) o;
			return Objects.equals(name, other.name) &&
			       Objects.equals(score, other.score) &&
			       Objects.equals(gameTime, other.gameTime) &&
			       Objects.equals(difficulty, other.difficulty) &&
			       Objects.equals(linesCleared, other.linesCleared) &&
			       Objects.equals(maxLevel, other.maxLevel) &&
			       Objects.equals(timeAchieved, other.timeAchieved);
		}
		return false;
	}

	@Override
	public int compareTo(HighScore other) {
		return other.score - score;
	}

	@Override
	public String toString() {
		return "HighScore [name=" + name + ", score=" + score + ", gameTime=" + gameTime + ", difficulty=" + difficulty
				+ ", linesCleared=" + linesCleared + ", maxLevel=" + maxLevel + ", timeAchieved=" + timeAchieved + "]";
	}
	
}
