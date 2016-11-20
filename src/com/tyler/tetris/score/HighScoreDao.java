package com.tyler.tetris.score;

import java.util.List;
import java.util.stream.Collectors;

import com.tyler.tetris.Difficulty;

public interface HighScoreDao {

	public default List<HighScore> getScores(Difficulty diff) throws Exception {
		return getHighScores().stream().filter(s -> s.difficulty == diff).collect(Collectors.toList());
	};
	
	public List<HighScore> getHighScores() throws Exception;
	
	public int saveHighScore(HighScore score) throws Exception;
	
}
