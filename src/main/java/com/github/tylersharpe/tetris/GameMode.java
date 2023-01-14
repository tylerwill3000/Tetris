package com.github.tylersharpe.tetris;

public enum GameMode {
    NORMAL("Normal"),
    TIME_ATTACK("Time Attack"),
    FREE_PLAY("Free Play"); // TODO implement

    private final String name;

    GameMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
