package com.github.tylerwilliams.tetris;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public record Score(int points,
                    int linesCleared,
                    int originalRank,
                    Integer maxLevel,
                    Duration gameTime,
                    String name,
                    Difficulty difficulty,
                    GameMode gameMode,
                    Boolean completedGame,
                    LocalDateTime date) implements Serializable {}
