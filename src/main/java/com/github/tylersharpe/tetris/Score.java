package com.github.tylersharpe.tetris;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Score implements Serializable, Comparable<Score> {

    public final int points, linesCleared, maxLevel;
    public final long gameTime;
    public final String name;
    public final Difficulty difficulty;
    public final LocalDate dateAchieved;

    public Score(String name, int score, long gameTime, Difficulty difficulty, int linesCleared, int maxLevel, LocalDate date) {
        this.name = name;
        this.points = score;
        this.gameTime = gameTime;
        this.difficulty = difficulty;
        this.linesCleared = linesCleared;
        this.maxLevel = maxLevel;
        this.dateAchieved = date;
    }

    public boolean completedGame() {
        return linesCleared == difficulty.getLinesPerLevel() * TetrisGame.MAX_LEVEL;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, points, gameTime, difficulty, linesCleared, maxLevel, dateAchieved);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Score) {
            Score other = (Score) o;
            return Objects.equals(name, other.name) &&
                    Objects.equals(points, other.points) &&
                    Objects.equals(gameTime, other.gameTime) &&
                    Objects.equals(difficulty, other.difficulty) &&
                    Objects.equals(linesCleared, other.linesCleared) &&
                    Objects.equals(maxLevel, other.maxLevel) &&
                    Objects.equals(dateAchieved, other.dateAchieved);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Score(" +
                "points=" + points +
                ", linesCleared=" + linesCleared +
                ", maxLevel=" + maxLevel +
                ", gameTime=" + gameTime +
                ", name='" + name + '\'' +
                ", difficulty=" + difficulty +
                ", dateAchieved=" + dateAchieved +
                ')';
    }

    @Override
    public int compareTo(Score other) {
        return other.points - points;
    }

}
