package com.github.tylersharpe.tetris.score;

import com.github.tylersharpe.tetris.Difficulty;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface ScoreDao {

  int MIN_RANK = 25;

  static boolean isLeaderBoardRank(int rank) {
    return rank < MIN_RANK;
  }

  default List<Score> getScores(Difficulty difficulty, Integer limit) {
    Stream<Score> scoresStream = null;
    try {
      scoresStream = getAllScores().stream();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (difficulty != null) {
      scoresStream = scoresStream.filter(score -> difficulty == score.difficulty);
    }
    if (limit != null) {
      scoresStream = scoresStream.limit(limit);
    }
    // Sort by points DESC
    return scoresStream.sorted((s1, s2) -> s2.points - s1.points).collect(toList());
  }

  default int saveScore(Score score) throws IOException {
    int rank = determineRank(score.points);
    if (!isLeaderBoardRank(rank)) {
      throw new IllegalArgumentException("Cannot save " + score + ", score does not meet minimum rank requirement of " + MIN_RANK);
    }
    doSaveScore(score);
    return rank;
  }

  default int determineRank(int pointsOfScoreToSave)  {
    long numScoresGreater = 0;
    try {
      numScoresGreater = getAllScores().stream().filter(score -> score.points > pointsOfScoreToSave).count();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return (int) numScoresGreater + 1;
  }

  List<Score> getAllScores() throws IOException;

  void doSaveScore(Score score) throws IOException;

  void clearAll() throws Exception;

}
