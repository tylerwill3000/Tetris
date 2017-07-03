package com.github.tylerwill.tetris;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class FlatFileScoreDao implements ScoreDao {

	private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".tetris-scores");
	
	@Override
	public List<Score> getScores() throws Exception {
		if (!Files.exists(SAVE_PATH)) {
			return new ArrayList<>();
		} else {
			try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
				@SuppressWarnings("unchecked")
				List<Score> scores = (List<Score>) objIn.readObject();
				IntStream.rangeClosed(1, scores.size()).forEach(rank -> scores.get(rank -1).rank = rank);
				return scores;
			}
			catch (Exception e) {
				throw new Exception("Malformed high scores file", e);
			}
		}
	}

	@Override
	public void doSaveScore(Score toSave) throws Exception  {
		
		List<Score> currentScores = getScores();
		currentScores.add(toSave);
		Collections.sort(currentScores);
		
		if (currentScores.size() > MIN_RANK) {
			currentScores = currentScores.subList(0, MIN_RANK);
		}
		
		try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
			objOut.writeObject(currentScores);
		}
	}

	@Override
	public void clearAll() throws Exception {
		Files.deleteIfExists(SAVE_PATH);
	}

}
