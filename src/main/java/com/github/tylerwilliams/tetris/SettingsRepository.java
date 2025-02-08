package com.github.tylerwilliams.tetris;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SettingsRepository {
    private static final Path SETTINGS_FILE = TetrisConfigDir.resolve("settings");

    public static void save(Settings settings) {
        try (var outputStream = new ObjectOutputStream(Files.newOutputStream(SETTINGS_FILE))) {
            outputStream.writeObject(settings);
        } catch (IOException e) {
            System.err.println("Could not save settings file");
            e.printStackTrace();
        }
    }

    public static Optional<Settings> load() {
        if (!Files.exists(SETTINGS_FILE)) {
            return Optional.empty();
        }

        try (var inputStream = new ObjectInputStream(Files.newInputStream(SETTINGS_FILE))) {
            return Optional.of((Settings) inputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Could not read settings file");
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
