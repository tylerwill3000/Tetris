package com.github.tylersharpe.tetris;

public enum Rotation {
  CLOCKWISE,
  COUNTER_CLOCKWISE;

  public Rotation reverse() {
    return this == CLOCKWISE ? COUNTER_CLOCKWISE : CLOCKWISE;
  }
}
