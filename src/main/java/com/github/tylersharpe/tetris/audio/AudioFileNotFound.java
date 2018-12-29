package com.github.tylersharpe.tetris.audio;

public class AudioFileNotFound extends RuntimeException {

  public AudioFileNotFound(String fileName) {
    super("Audio file '" + fileName + "' was not found");
  }

}
