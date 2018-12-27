package com.github.tylersharpe.tetris.score;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FlatFileScoreDao implements ScoreDao {

  private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".tetris-scores");

  @Override
  public List<Score> getAllScores() throws IOException {
    if (!Files.exists(SAVE_PATH)) {
      return List.of();
    }

    try (var scoresInput = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
      @SuppressWarnings("unchecked")
      List<Score> scores = (List<Score>) scoresInput.readObject();

      for (int rank = 1; rank <= scores.size(); rank++) {
        scores.get(rank -1).rank = rank;
      }

      return scores;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Malformed high scores file", e);
    }
  }

  @Override
  public void doSaveScore(Score toSave) throws IOException {
    List<Score> allScores = new ArrayList<>(getAllScores());
    allScores.add(toSave);

    if (allScores.size() > MIN_RANK) {
      allScores = allScores.subList(0, MIN_RANK);
    }

    try (var objOut = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
      objOut.writeObject(allScores);
    }
  }

  @Override
  public void clearAll() throws Exception {
    Files.deleteIfExists(SAVE_PATH);
  }

}
