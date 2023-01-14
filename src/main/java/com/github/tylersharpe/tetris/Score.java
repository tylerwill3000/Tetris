package com.github.tylersharpe.tetris;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public record Score(int points,
                    int linesCleared,
                    int maxLevel,
                    Duration gameTime,
                    String name,
                    Difficulty difficulty,
                    boolean completedGame,
                    LocalDateTime date) implements Serializable {}
