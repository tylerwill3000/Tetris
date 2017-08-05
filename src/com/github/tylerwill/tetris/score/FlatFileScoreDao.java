package com.github.tylerwill.tetris.score;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FlatFileScoreDao implements ScoreDao {

  private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".tetris-scores");

  @Override
  public List<Score> getAllScores() throws Exception {
    if (!Files.exists(SAVE_PATH)) {
      return new ArrayList<>();
    }
    try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {

      @SuppressWarnings("unchecked")
      List<Score> scores = (List<Score>) objIn.readObject();
      scores.sort((s1, s2) -> s2.points - s1.points); // Sort by points DESC

      for (int rank = 1; rank <= scores.size(); rank++) {
        scores.get(rank -1).rank = rank;
      }

      return scores;
    } catch (IOException | ClassNotFoundException e) {
      throw new Exception("Malformed high scores file", e);
    }
  }

  @Override
  public void _saveScore(Score toSave) throws Exception  {
    List<Score> allScores = getAllScores();
    allScores.add(toSave);

    if (allScores.size() > MIN_RANK) {
      allScores = allScores.subList(0, MIN_RANK);
    }

    try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
      objOut.writeObject(allScores);
    }
  }

  @Override
  public void clearAll() throws Exception {
    Files.deleteIfExists(SAVE_PATH);
  }

}
