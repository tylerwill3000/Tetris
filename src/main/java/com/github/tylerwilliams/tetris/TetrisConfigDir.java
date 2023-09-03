package com.github.tylerwilliams.tetris;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides an interface to the directory where tetris-related information is stored on disk.
 */
class TetrisConfigDir {
    private static final Path TETRIS_CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".config", "tetris");
    static {
        if (!Files.isDirectory(TETRIS_CONFIG_DIR)) {
            try {
                Files.createDirectories(TETRIS_CONFIG_DIR);
            } catch (IOException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    static Path resolve(String fileName) {
        return TETRIS_CONFIG_DIR.resolve(fileName);
    }
}
