package com.github.tylerwill.tetris;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ScoreDao {

	static final int MIN_RANK = 25;
	
	public default List<Score> getScores(Optional<Difficulty> difficulty, Optional<Integer> limit) throws Exception {
		Stream<Score> scores = getScores().stream();
		if (difficulty.isPresent()) {
			scores = scores.filter(s -> difficulty.get() == s.difficulty);
		}
		if (limit.isPresent()) {
			scores = scores.limit(limit.get());
		}
		return scores.collect(Collectors.toList());
	};
	
	public default int saveScore(Score score) throws Exception {
		int rank = determineRank(score.points);
		if (isHighScore(rank)) {
			doSaveScore(score);
			return rank;
		}
		else {
			throw new IllegalArgumentException("Cannot save score " + score + ", score does not meet minimum rank requirement of " + MIN_RANK);
		}
	};
	
	/**
	 * Determines what rank the given score would have if saved
	 */
	public default int determineRank(int score) throws Exception {
		int numScoresGreater = (int) getScores().stream()
		                                        .filter(existingScore -> score <= existingScore.points)
		                                        .count();
		return numScoresGreater + 1;
	}
	
	public static boolean isHighScore(int rank) {
		return rank <= MIN_RANK;
	}
	
	public List<Score> getScores() throws Exception;

	void doSaveScore(Score score) throws Exception;
	
	public void clearAll() throws Exception;
	
}
