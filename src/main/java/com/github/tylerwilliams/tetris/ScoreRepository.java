package com.github.tylerwilliams.tetris;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class ScoreRepository {

    private static final Comparator<Score> SCORE_COMPARATOR = Comparator
            .comparing(Score::points).reversed()
            .thenComparing(Score::date);

    private static final Path LEADERBOARD_FILE = TetrisConfigDir.resolve("leaderboard");
    private static final int LEADER_BOARD_RANK_THRESHOLD = 10;

    public static boolean isLeaderBoardRank(int rank) {
        return rank <= LEADER_BOARD_RANK_THRESHOLD;
    }

    public static List<Score> getScores(Difficulty difficulty, GameMode gameMode) throws IOException {
        return readScoresFromDisk()
                .stream()
                .filter(score -> score.difficulty() == difficulty && score.gameMode() == gameMode)
                .sorted(SCORE_COMPARATOR)
                .limit(LEADER_BOARD_RANK_THRESHOLD)
                .toList();
    }

    public static int determineRank(int pointsOfScoreToSave, Difficulty difficulty, GameMode gameMode, LocalDateTime scoreDate) throws IOException {
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

    public static void saveScore(Score score) throws IOException {
        Collection<Score> allScores = new ArrayList<>(readScoresFromDisk());
        allScores.add(score);

        try (var objectOutputStream = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE.toFile()))) {
            objectOutputStream.writeObject(allScores);
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<Score> readScoresFromDisk() throws IOException {
        if (!Files.exists(LEADERBOARD_FILE)) {
            return Collections.emptyList();
        }

        try (var scoresInputStream = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE.toFile()))) {
            return (Collection<Score>) scoresInputStream.readObject();
        } catch (ClassCastException | ClassNotFoundException e) {
            throw new IOException("Malformed high scores file", e);
        }
    }

}
