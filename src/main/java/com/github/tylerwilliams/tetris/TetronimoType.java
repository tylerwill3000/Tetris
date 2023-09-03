package com.github.tylerwilliams.tetris;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public enum TetronimoType {

    BOX("Box",

        // Offsets. All the same for each direction
        // XX
        // XX
        new int[][][]{
            {{0, 0}, {-1, 0}, {-1, 1}, {0, 1}},
            {{0, 0}, {-1, 0}, {-1, 1}, {0, 1}},
            {{0, 0}, {-1, 0}, {-1, 1}, {0, 1}},
            {{0, 0}, {-1, 0}, {-1, 1}, {0, 1}}
        },

        // Preview panel squares
        new int[][]{{1, 1}, {1, 2}, {2, 1}, {2, 2}},

        // Start row
        4,

        // Color
        new Color(0, 70, 255)
    ),

    INVERTED_L("Inverted L",

        // Offsets
        new int[][][]{

            // X..
            // XXX
            // ...
            {{-2, 0}, {-1, 0}, {-1, 1}, {-1, 2}},

            // .XX
            // .X.
            // .X.
            {{-2, 1}, {-2, 2}, {-1, 1}, {0, 1}},

            // ...
            // XXX
            // ..X
            {{-1, 0}, {-1, 1}, {-1, 2}, {0, 2}},

            // .X.
            // .X.
            // XX.
            {{0, 0}, {0, 1}, {-1, 1}, {-2, 1}}

        },

        // Preview panel squares
        new int[][]{{1, 1}, {2, 1}, {2, 2}, {2, 3}},

        // Start row
        5,

        // Color
        Color.YELLOW
    ),

    L("L",

        // Offsets
        new int[][][]{

            // ..X
            // XXX
            // ...
            {{-1, 0}, {-1, 1}, {-1, 2}, {-2, 2}},

            // .X.
            // .X.
            // .XX
            {{0, 1}, {0, 2}, {-1, 1}, {-2, 1}},

            // ...
            // XXX
            // X..
            {{0, 0}, {-1, 0}, {-1, 1}, {-1, 2}},

            // XX.
            // .X.
            // .X.
            {{-2, 0}, {-2, 1}, {-1, 1}, {0, 1}}

        },

        // Preview panel squares
        new int[][]{{1, 3}, {2, 3}, {2, 2}, {2, 1}},

        // Start row
        5,

        // Color
        Color.PINK
    ),

    INVERTED_S("Inverted S",

        // Offsets
        new int[][][]{

            // ...
            // XX.
            // .XX
            {{-1, 0}, {-1, 1}, {0, 1}, {0, 2}},

            // .X.
            // XX.
            // X..
            {{0, 0}, {-1, 0}, {-1, 1}, {-2, 1}},

            // Other 2 Offsets are the same as the first 2, so
            // just cycle through them
            {{-1, 0}, {-1, 1}, {0, 1}, {0, 2}},
            {{0, 0}, {-1, 0}, {-1, 1}, {-2, 1}}

        },

        // Preview panel squares
        new int[][]{{1, 1}, {1, 2}, {2, 2}, {2, 3}},

        // Start row
        4,

        // Color
        Color.GREEN
    ),

    S("S",

        // Offsets
        new int[][][]{

            // ...
            // .XX
            // XX.
            {{0, 0}, {0, 1}, {-1, 1}, {-1, 2}},

            // .X.
            // .XX
            // ..X
            {{-2, 1}, {-1, 1}, {-1, 2}, {0, 2}},

            // Other 2 Offsets are the same as the first 2, so
            // just cycle through them
            {{0, 0}, {0, 1}, {-1, 1}, {-1, 2}},
            {{-2, 1}, {-1, 1}, {-1, 2}, {0, 2}}

        },

        // Preview panel squares
        new int[][]{{1, 2}, {1, 3}, {2, 1}, {2, 2}},

        // Start row
        4,

        // Color
        new Color(170, 45, 255) // Purple
    ),

    LINE("Line",

        // Offsets
        new int[][][]{

            // ....
            // ....
            // XXXX
            // ....
            {{-1, 0}, {-1, 1}, {-1, 2}, {-1, 3}},

            // .X..
            // .X..
            // .X..
            // .X..
            {{0, 1}, {-1, 1}, {-2, 1}, {-3, 1}},

            // Other 2 Offsets are the same as the first 2, so
            // just cycle through them
            {{-1, 0}, {-1, 1}, {-1, 2}, {-1, 3}},
            {{0, 1}, {-1, 1}, {-2, 1}, {-3, 1}}

        },

        // Preview panel squares
        new int[][]{{0, 2}, {1, 2}, {2, 2}, {3, 2}},

        // Start row
        4,

        // Color
        new Color(0, 200, 200) // Blue-green
    ),

    T("T",

        // Offsets
        new int[][][]{

            // ...
            // XXX
            // .X.
            {{-1, 0}, {-1, 1}, {-1, 2}, {0, 1}},

            // .X.
            // XX.
            // .X.
            {{-1, 0}, {-2, 1}, {-1, 1}, {0, 1}},

            // .X.
            // XXX
            // ...
            {{-2, 1}, {-1, 0}, {-1, 1}, {-1, 2}},

            // .X.
            // .XX
            // .X.
            {{0, 1}, {-1, 1}, {-1, 2}, {-2, 1}}

        },

        // Preview panel squares
        new int[][]{{1, 1}, {1, 2}, {1, 3}, {2, 2}},

        // Start row
        4,

        // Color
        new Color(255, 30, 0) // Red
    );

    private static final List<Color> COLORS = Stream.of(values()).map(TetronimoType::getColor).toList();

    private final String name;
    private final int[][][] offsets;
    private final Collection<ColoredSquare> previewPanelSquares;
    private final int startRow;
    private final Color color;

    TetronimoType(String name, int[][][] offsets, int[][] previewPanelSquares, int startRow, Color color) {
        this.name = name;
        this.offsets = offsets;
        this.previewPanelSquares = Stream.of(previewPanelSquares)
                .map(coordinates -> new ColoredSquare(color, coordinates[0], coordinates[1]))
                .toList();
        this.startRow = startRow;
        this.color = color;
    }

    public Collection<ColoredSquare> getPreviewPanelSquares() {
        return this.previewPanelSquares;
    }

    public int getStartRow() {
        return startRow;
    }

    public Color getColor() {
        return color;
    }

    public Collection<ColoredSquare> calculateOccupiedSquares(int orientation, int row, int col) {
        if (orientation < 0 || orientation > 3) {
            throw new IllegalArgumentException("Orientation value must be between 0 and 3");
        }

        int[][] offsetsForOrientation = this.offsets[orientation];

        return Stream.of(offsetsForOrientation)
                .map(offset -> new ColoredSquare(color, row + offset[0], col + offset[1]))
                .toList();
    }

    public static Color getRandomColor() {
        return Utility.sample(COLORS);
    }

    @Override
    public String toString() {
        return name;
    }
}
