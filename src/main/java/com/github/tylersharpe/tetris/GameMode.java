package com.github.tylersharpe.tetris;

public enum GameMode {
    CAMPAIGN("Campaign"),
    TIME_ATTACK("Time Attack"),
    FREE_PLAY("Free Play");

    private final String name;

    GameMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
