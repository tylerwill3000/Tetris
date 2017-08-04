package com.github.tylerwill.tetris.score;

import com.github.tylerwill.tetris.Difficulty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ScoreDao {

  int MIN_RANK = 25;

  default List<Score> getAllScores() throws Exception {
    return getScores(Optional.empty(), Optional.empty());
  }

  default List<Score> getScores(Optional<Difficulty> difficulty, Optional<Integer> limit) throws Exception {
    Stream<Score> scores = getAllScores().stream();
    if (difficulty.isPresent()) {
      scores = scores.filter(score -> difficulty.get() == score.difficulty);
    }
    if (limit.isPresent()) {
      scores = scores.limit(limit.get());
    }
    return scores.collect(Collectors.toList());
  }

  default int saveScore(Score score) throws Exception {
    int rank = determineRank(score.points);
    if (!isHighScore(rank)) {
      throw new IllegalArgumentException("Cannot save score " + score + ", score does not meet minimum rank requirement of " + MIN_RANK);
    }
    _saveScore(score);
    return rank;
  }

  default int determineRank(int pointsOfScoreToSave) throws Exception {
    long numScoresGreater = getAllScores().stream()
                                       .filter(existingScore -> existingScore.points > pointsOfScoreToSave)
                                       .count();
    return (int) numScoresGreater + 1;
  }

  static boolean isHighScore(int rank) {
    return rank <= MIN_RANK;
  }

  void _saveScore(Score score) throws Exception;

  void clearAll() throws Exception;

}
