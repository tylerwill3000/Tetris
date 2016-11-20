package com.tyler.tetris.score;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.tyler.tetris.Difficulty;

public class FlatFileHighScoreDao implements HighScoreDao {

	private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home"), ".tetris-scores");
	private static final int MAX_SAVE_COUNT = 50;
	private static final String RECORD_SEP = "|";
	
	@Override
	public List<HighScore> getHighScores() throws Exception {
		if (!SAVE_PATH.toFile().exists()) {
			return new ArrayList<>();
		}
		else {
			try {
				return Files.readAllLines(SAVE_PATH)
				            .stream()
				            .filter(s -> !s.isEmpty())
				            .map(line -> line.split("\\" + RECORD_SEP))
				            .map(FlatFileHighScoreDao::deserialize)
				            .collect(Collectors.toList());
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Malformed high scores file: " + e.getMessage());
			}
		}
	}

	@Override
	public int saveHighScore(HighScore toSave) throws Exception  {
		
		List<HighScore> currentScores = getHighScores();
		currentScores.add(toSave);
		Collections.sort(currentScores);
		
		String recordStr = currentScores.stream()
		                                .limit(MAX_SAVE_COUNT)
		                                .map(FlatFileHighScoreDao::serialize)
		                                .collect(Collectors.joining("\n"));
		
		Files.write(SAVE_PATH, recordStr.getBytes(StandardCharsets.UTF_8));
		return currentScores.indexOf(toSave) + 1;
	}

	private static String serialize(HighScore score) {
		
		List<String> recordData = Arrays.asList(
		                                  score.name,
		                                  score.score + "",
		                                  score.gameTime + "",
		                                  score.difficulty.name(),
		                                  score.linesCleared + "",
		                                  score.maxLevel + "",
		                                  score.timeAchieved.toString());
		
		return recordData.stream().collect(Collectors.joining(RECORD_SEP));
	}
	
	private static HighScore deserialize(String[] record) {
		return new HighScore(
		            record[0],
                    parseInt(record[1]),
                    parseLong(record[2]),
                    Difficulty.valueOf(record[3]),
                    parseInt(record[4]),
                    parseInt(record[5]),
                    LocalDateTime.parse(record[6]));
	}
	
}
