package com.github.tylersharpe.tetris;

public class AudioFileNotFound extends RuntimeException {

    public AudioFileNotFound(String fileName) {
        super("Audio file '" + fileName + "' was not found");
    }

}
