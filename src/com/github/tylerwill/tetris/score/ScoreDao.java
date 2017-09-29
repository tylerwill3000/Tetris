package com.github.tylerwill.tetris.score;

import com.github.tylerwill.tetris.Difficulty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ScoreDao {

  int MIN_RANK = 25;

  default List<Score> getScores(Optional<Difficulty> difficulty, Optional<Integer> limit) throws Exception {
    List<Score> allScores = getAllScores();
    if (!difficulty.isPresent() && !limit.isPresent()) {
      return allScores;
    }

    Stream<Score> scoresStream = allScores.stream();
    if (difficulty.isPresent()) {
      scoresStream = scoresStream.filter(score -> difficulty.get() == score.difficulty);
    }
    if (limit.isPresent()) {
      scoresStream = scoresStream.limit(limit.get());
    }
    return scoresStream.collect(Collectors.toList());
  }

  default int saveScore(Score score) throws Exception {
    int rank = determineRank(score.points);
    if (!isLeaderboardRank(rank)) {
      throw new IllegalArgumentException("Cannot save " + score + ", score does not meet minimum rank requirement of " + MIN_RANK);
    }
    _saveScore(score);
    return rank;
  }

  static boolean isLeaderboardRank(int rank) {
    return rank < MIN_RANK;
  }

  default int determineRank(int pointsOfScoreToSave) throws Exception {
    long numScoresGreater = getAllScores().stream()
                                          .filter(existingScore -> existingScore.points > pointsOfScoreToSave)
                                          .count();
    return (int) numScoresGreater + 1;
  }

  List<Score> getAllScores() throws Exception;

  void _saveScore(Score score) throws Exception;

  void clearAll() throws Exception;

}
