package com.github.tylersharpe.tetris;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ScoreRepository {

    private static final Comparator<Score> SCORE_COMPARATOR = Comparator
            .comparing(Score::points).reversed()
            .thenComparing(Score::date);

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

    public List<Score> getScores(Difficulty difficulty, GameMode gameMode) throws IOException {
        return readScoresFromDisk()
                .stream()
                .filter(score -> score.difficulty() == difficulty && score.gameMode() == gameMode)
                .sorted(SCORE_COMPARATOR)
                .toList();
    }

    public int determineRank(int pointsOfScoreToSave, Difficulty difficulty, GameMode gameMode, LocalDateTime scoreDate) throws IOException {
        long numScoresGreater = readScoresFromDisk()
                .stream()
                .filter(score -> score.difficulty() == difficulty && score.gameMode() == gameMode)
                .filter(existingScore -> {
                    if (existingScore.points() == pointsOfScoreToSave) {
                        return existingScore.date().isBefore(scoreDate);
                    }

                    return existingScore.points() > pointsOfScoreToSave;
                })
                .count();
        return (int) numScoresGreater + 1;
    }

    public void saveScore(Score score) throws IOException {
        Collection<Score> allScores = new ArrayList<>(readScoresFromDisk());
        allScores.add(score);

        try (var objectOutputStream = new ObjectOutputStream(new FileOutputStream(SAVE_PATH.toFile()))) {
            objectOutputStream.writeObject(allScores);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Score> readScoresFromDisk() throws IOException {
        if (!Files.exists(SAVE_PATH)) {
            return Collections.emptyList();
        }

        try (var scoresInputStream = new ObjectInputStream(new FileInputStream(SAVE_PATH.toFile()))) {
            return (Collection<Score>) scoresInputStream.readObject();
        } catch (ClassCastException | ClassNotFoundException e) {
            throw new IOException("Malformed high scores file", e);
        }
    }

}
