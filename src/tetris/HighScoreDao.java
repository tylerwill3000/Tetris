package tetris;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface HighScoreDao {

	public default List<HighScore> getHighScores(Optional<Difficulty> difficulty, Optional<Integer> limit) throws Exception {
		Stream<HighScore> scores = getHighScores().stream();
		if (difficulty.isPresent()) {
			scores = scores.filter(s -> difficulty.get() == s.difficulty);
		}
		if (limit.isPresent()) {
			scores = scores.limit(limit.get());
		}
		return scores.collect(Collectors.toList());
	};
	
	public List<HighScore> getHighScores() throws Exception;
	
	public int saveHighScore(HighScore score) throws Exception;
	
}
