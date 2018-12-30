package com.github.tylersharpe.tetris;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class ScoreRepository {

  private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".tetris-scores");
  private static final int LEADER_BOARD_RANK_THRESHOLD = 20;

  public static boolean isLeaderBoardRank(int rank) {
    return rank <= LEADER_BOARD_RANK_THRESHOLD;
  }

  public SortedSet<Score> getScores(Difficulty difficulty, Integer limit) throws IOException {
    var scoresStream = getAllScores().stream();

    if (difficulty != null) {
      scoresStream = scoresStream.filter(score -> difficulty == score.difficulty);
    }
    if (limit != null) {
      scoresStream = scoresStream.limit(limit);
    }

    return scoresStream.collect(toCollection(TreeSet::new));
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
    var allScores = new TreeSet<>(getAllScores());
    allScores.add(score);

    try (var objOut = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
      objOut.writeObject(allScores);
    }
  }

  public SortedSet<Score> getAllScores() throws IOException {
    if (!Files.exists(SAVE_PATH)) {
      return Collections.emptySortedSet();
    }

    try (var scoresInput = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
      @SuppressWarnings("unchecked")
      List<Score> scores = new ArrayList<>((Collection<Score>) scoresInput.readObject());

      for (int rank = 1; rank <= scores.size(); rank++) {
        scores.get(rank -1).rank = rank;
      }

      return new TreeSet<>(scores);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Malformed high scores file", e);
    }
  }

}
