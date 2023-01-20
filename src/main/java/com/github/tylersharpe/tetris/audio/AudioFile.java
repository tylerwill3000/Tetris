package com.github.tylersharpe.tetris.audio;

import java.net.URL;

enum AudioFile {
    TETRIS_THEME("/audio/soundtrack/tetris-theme.wav"),
    BEAN_MACHINE_BEGINNER("/audio/soundtrack/bean-machine-1-4.wav"),
    TETRIS_3("/audio/soundtrack/tetris-music-3.wav"),
    METROID1_KRAID_THEME("/audio/soundtrack/metroid-kraid.wav"),
    SONIC_SCRAP_BRAIN_ZONE("/audio/soundtrack/sonic-scrap-brain-zone.wav"),
    CHRONO_TRIGGER_BIKE_THEME("/audio/soundtrack/chrono-trigger-bike-theme.wav"),
    MEGA_MAN_DR_WILY_THEME("/audio/soundtrack/mega-man-dr-wily.wav"),
    SONIC_ICE_CAP_ZONE("/audio/soundtrack/sonic-ice-cap-zone.wav"),
    BEAN_MACHINE_ADVANCED("/audio/soundtrack/bean-machine-9-12.wav"),
    CHRONO_TRIGGER_FINAL_BATTLE("/audio/soundtrack/chrono-trigger-final-battle.wav"),
    ZELDA_GAME_OVER("/audio/soundtrack/zelda-game-over.wav"),
    FINAL_FANTASY_VICTORY_FANFARE("/audio/soundtrack/final-fantasy-victory-fanfare.wav"),
    MARIO_64_PAUSE("/audio/effects/mario-64-pause.wav"),
    PIPE("/audio/effects/pipe.wav"),
    LASER("/audio/effects/laser.wav"),
    EXPLOSION("/audio/effects/explosion.wav"),
    SWISH_UP("/audio/effects/swish-up.wav"),
    SWISH_DOWN("/audio/effects/swish-down.wav"),
    SUPER_SLIDE("/audio/effects/superslide.wav"),
    CLANG("/audio/effects/clang.wav"),
    WATER_DROP("/audio/effects/water-drop.wav");

    private final URL url;

    AudioFile(String resourcePath) {
        url = AudioFile.class.getResource(resourcePath);
        if (url == null) {
            throw new RuntimeException("Audio file at resource path '" + resourcePath + "' was not found");
        }
    }

    public URL getUrl() {
        return url;
    }
}
