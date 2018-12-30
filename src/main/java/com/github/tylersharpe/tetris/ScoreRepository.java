package com.github.tylersharpe.tetris;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ScoreRepository {

  private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".tetris-scores");
  private static final int MIN_RANK = 25;

  public static boolean isLeaderBoardRank(int rank) {
    return rank < MIN_RANK;
  }

  public List<Score> getScores(Difficulty difficulty, Integer limit) throws IOException {
    var scoresStream = getAllScores().stream();

    if (difficulty != null) {
      scoresStream = scoresStream.filter(score -> difficulty == score.difficulty);
    }
    if (limit != null) {
      scoresStream = scoresStream.limit(limit);
    }

    return scoresStream.collect(toList());
  }

  public void saveScore(Score score) throws IOException {
    int rank = determineRank(score.points);
    if (!isLeaderBoardRank(rank)) {
      throw new IllegalArgumentException("Cannot save " + score + ", score does not meet minimum rank requirement of " + MIN_RANK);
    }

    List<Score> allScores = new ArrayList<>(getAllScores());
    allScores.add(score);

    if (allScores.size() > MIN_RANK) {
      allScores = allScores.subList(0, MIN_RANK);
    }

    try (var objOut = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
      objOut.writeObject(allScores);
    }
  }

  public int determineRank(int pointsOfScoreToSave) {
    try {
      long numScoresGreater = getAllScores().stream().filter(score -> score.points > pointsOfScoreToSave).count();
      return (int) numScoresGreater + 1;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public List<Score> getAllScores() throws IOException {
    if (!Files.exists(SAVE_PATH)) {
      return List.of();
    }

    try (var scoresInput = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
      @SuppressWarnings("unchecked")
      List<Score> scores = ((List<Score>) scoresInput.readObject());
      scores.sort(Score.POINTS_HIGH_TO_LOW);

      for (int rank = 1; rank <= scores.size(); rank++) {
        scores.get(rank -1).rank = rank;
      }

      return scores;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Malformed high scores file", e);
    }
  }

}
