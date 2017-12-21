package com.github.tylerwill.tetris.score;

import com.github.tylerwill.tetris.Difficulty;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface ScoreDao {

  int MIN_RANK = 25;

  default List<Score> getScores(Difficulty difficulty, Integer limit) throws Exception {
    Stream<Score> scoresStream = getAllScores().stream();
    if (difficulty != null) {
      scoresStream = scoresStream.filter(score -> difficulty == score.difficulty);
    }
    if (limit != null) {
      scoresStream = scoresStream.limit(limit);
    }
    // Sort by points DESC
    return scoresStream.sorted((s1, s2) -> s2.points - s1.points).collect(toList());
  }

  default int saveScore(Score score) throws Exception {
    int rank = determineRank(score.points);
    if (!isLeaderBoardRank(rank)) {
      throw new IllegalArgumentException("Cannot save " + score + ", score does not meet minimum rank requirement of " + MIN_RANK);
    }
    _saveScore(score);
    return rank;
  }

  static boolean isLeaderBoardRank(int rank) {
    return rank < MIN_RANK;
  }

  default int determineRank(int pointsOfScoreToSave) throws Exception {
    long numScoresGreater = getAllScores().stream().filter(score -> score.points > pointsOfScoreToSave).count();
    return (int) numScoresGreater + 1;
  }

  List<Score> getAllScores() throws Exception;

  void _saveScore(Score score) throws Exception;

  void clearAll() throws Exception;

}
