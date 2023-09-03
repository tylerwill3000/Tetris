package com.github.tylerwilliams.tetris;

import java.io.Serializable;

public class Settings implements Serializable {
    public boolean ghostSquares = true;
    public boolean soundtrack = true;
    public boolean soundEffects = true;
    public GameMode gameMode = GameMode.CAMPAIGN;
    public Difficulty difficulty = Difficulty.EASY;

    @Override
    public String toString() {
        return "Settings{" +
                "ghostSquares=" + ghostSquares +
                ", soundtrack=" + soundtrack +
                ", soundEffects=" + soundEffects +
                ", gameMode=" + gameMode +
                ", difficulty=" + difficulty +
                '}';
    }
}
