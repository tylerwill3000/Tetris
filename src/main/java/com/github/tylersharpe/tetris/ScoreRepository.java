package com.github.tylersharpe.tetris;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ScoreRepository {

  private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".config", "tetris-scores");
  private static final int LEADER_BOARD_RANK_THRESHOLD = 20;

  static {
    final var dotConfigDir = SAVE_PATH.getParent();
    if (!Files.isDirectory(dotConfigDir)) {
      try {
        Files.createDirectory(dotConfigDir);
      } catch (IOException e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }

  public static boolean isLeaderBoardRank(int rank) {
    return rank <= LEADER_BOARD_RANK_THRESHOLD;
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

  public int determineRank(int pointsOfScoreToSave) {
    try {
      long numScoresGreater = getAllScores().stream().filter(score -> score.points > pointsOfScoreToSave).count();
      return (int) numScoresGreater + 1;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void saveScore(Score score) throws IOException {
    var allScores = new ArrayList<>(getAllScores());
    allScores.add(score);

    try (var objOut = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
      objOut.writeObject(allScores);
    }
  }

  @SuppressWarnings("unchecked")
  private List<Score> getAllScores() throws IOException {
    if (!Files.exists(SAVE_PATH)) {
      return Collections.emptyList();
    }

    try (var scoresInput = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
      var scores = (List<Score>) scoresInput.readObject();
      Collections.sort(scores);
      return scores;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Malformed high scores file", e);
    }
  }

}
