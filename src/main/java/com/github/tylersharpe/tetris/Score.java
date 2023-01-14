package com.github.tylersharpe.tetris;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;

public record Score(int points,
                    int linesCleared,
                    int maxLevel,
                    Duration gameTime,
                    String name,
                    Difficulty difficulty,
                    boolean completedGame,
                    LocalDate date) implements Serializable {}
