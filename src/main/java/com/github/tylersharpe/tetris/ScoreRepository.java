package com.github.tylersharpe.tetris;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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
    Stream<Score> scores = readScoresFromDisk().stream();

    scores = scores.sorted();

    if (difficulty != null) {
      scores = scores.filter(score -> difficulty == score.difficulty);
    }
    if (limit != null) {
      scores = scores.limit(limit);
    }

    return scores.collect(toList());
  }

  public int determineRank(int pointsOfScoreToSave) throws IOException {
    long numScoresGreater = readScoresFromDisk().stream().filter(score -> score.points > pointsOfScoreToSave).count();
    return (int) numScoresGreater + 1;
  }

  public void saveScore(Score score) throws IOException {
    List<Score> allScores = new ArrayList<>(readScoresFromDisk());
    allScores.add(score);

    try (var objectOutputStream = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
      objectOutputStream.writeObject(allScores);
    }
  }

  @SuppressWarnings("unchecked")
  private List<Score> readScoresFromDisk() throws IOException {
    if (!Files.exists(SAVE_PATH)) {
      return Collections.emptyList();
    }

    try (var scoresInputStream = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
      return (List<Score>) scoresInputStream.readObject();
    } catch (ClassCastException | ClassNotFoundException e) {
      throw new RuntimeException("Malformed high scores file", e);
    }
  }

}
