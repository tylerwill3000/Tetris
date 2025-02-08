package com.github.tylerwilliams.tetris;

import java.net.URL;

public enum ImageFile {
    GAME_ICON("/images/game-icon.png"),
    PALETTE_ICON("/images/palette.png"),
    SAVE_ICON("/images/save-icon.png"),
    STAR_ICON("/images/star.png"),
    TROPHY_ICON("/images/trophy.png");

    private final URL url;

    ImageFile(String resourcePath) {
        url = ImageFile.class.getResource(resourcePath);
        if (url == null) {
            throw new RuntimeException("Image file at resource path '" + resourcePath + "' was not found");
        }
    }

    public URL getUrl() {
        return url;
    }
}
