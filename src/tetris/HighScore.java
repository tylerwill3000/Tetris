package tetris;

import java.time.LocalDate;
import java.util.Objects;

public class HighScore implements Comparable<HighScore> {

	public final int score;
	public final int linesCleared;
	public final int maxLevel;
	public final int rank;
	public final long gameTime;
	public final String name;
	public final Difficulty difficulty;
	public final LocalDate dateAchieved;
	
	public HighScore(String name, int score, long gameTime, Difficulty difficulty, int linesCleared, int maxLevel) {
		this(-1, name, score, gameTime, difficulty, linesCleared, maxLevel, LocalDate.now());
	}
	
	HighScore(int rank, String name, int score, long gameTime, Difficulty difficulty, int linesCleared, int maxLevel, LocalDate date) {
		this.rank = rank;
		this.name = name;
		this.score = score;
		this.gameTime = gameTime;
		this.difficulty = difficulty;
		this.linesCleared = linesCleared;
		this.maxLevel = maxLevel;
		this.dateAchieved = date;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(rank, name, score, gameTime, difficulty, linesCleared, maxLevel, dateAchieved);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HighScore) {
			HighScore other = (HighScore) o;
			return Objects.equals(name, other.name) &&
			       Objects.equals(rank, other.rank) &&
			       Objects.equals(score, other.score) &&
			       Objects.equals(gameTime, other.gameTime) &&
			       Objects.equals(difficulty, other.difficulty) &&
			       Objects.equals(linesCleared, other.linesCleared) &&
			       Objects.equals(maxLevel, other.maxLevel) &&
			       Objects.equals(dateAchieved, other.dateAchieved);
		}
		return false;
	}

	@Override
	public int compareTo(HighScore other) {
		return other.score - score;
	}

}
