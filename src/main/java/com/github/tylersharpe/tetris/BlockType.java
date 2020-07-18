package com.github.tylersharpe.tetris;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum BlockType {

  BOX(

    // Offsets. All the same for each direction
    // XX
    // XX
    new int[][][]{
      { {0,0}, {-1,0}, {-1,1}, {0,1} },
      { {0,0}, {-1,0}, {-1,1}, {0,1} },
      { {0,0}, {-1,0}, {-1,1}, {0,1} },
      { {0,0}, {-1,0}, {-1,1}, {0,1} }
    },

    // Preview panel squares
    new int[][]{ {1,1}, {1,2}, {2,1}, {2,2} },

    // Start row
    4,

    // Color
    new Color(0, 70, 255)
  ),

  L_BLOCK_L(

    // Offsets
    new int[][][]{

      // X..
      // XXX
      // ...
      { {-2,0}, {-1,0}, {-1,1}, {-1,2} },

      // .XX
      // .X.
      // .X.
      { {-2,1}, {-2,2}, {-1,1}, {0,1} },

      // ...
      // XXX
      // ..X
      { {-1,0}, {-1,1}, {-1,2}, {0,2} },

      // .X.
      // .X.
      // XX.
      { {0,0}, {0,1}, {-1,1}, {-2,1} }

    },

    // Preview panel squares
    new int[][]{ {1,1}, {2,1}, {2,2}, {2,3} },

    // Start row
    5,

    // Color
    Color.YELLOW
  ),

  L_BLOCK_R(

    // Offsets
    new int[][][]{

      // ..X
      // XXX
      // ...
      { {-1,0}, {-1,1}, {-1,2}, {-2,2} },

      // .X.
      // .X.
      // .XX
      { {0,1}, {0,2}, {-1,1}, {-2,1} },

      // ...
      // XXX
      // X..
      { {0,0}, {-1,0}, {-1,1}, {-1,2} },

      // XX.
      // .X.
      // .X.
      { {-2,0}, {-2,1}, {-1,1}, {0,1} }

    },

    // Preview panel squares
    new int[][]{ {1,3}, {2,3}, {2,2}, {2,1} },

    // Start row
    5,

    // Color
    Color.PINK
  ),

  S_BLOCK_L(

    // Offsets
    new int[][][]{

    // ...
    // XX.
    // .XX
    { {-1,0}, {-1,1}, {0,1}, {0,2} },

    // .X.
    // XX.
    // X..
    { {0,0}, {-1,0}, {-1,1}, {-2,1} },

    // Other 2 Offsets are the same as the first 2, so
    // just cycle through them
    { {-1,0}, {-1,1}, {0,1}, {0,2} },
    { {0,0}, {-1,0}, {-1,1}, {-2,1} }

    },

    // Preview panel squares
    new int[][]{ {1,1}, {1,2}, {2,2}, {2,3} },

    // Start row
    4,

    // Color
    Color.GREEN
  ),

  S_BLOCK_R(

    // Offsets
    new int[][][]{

      // ...
      // .XX
      // XX.
      { {0,0}, {0,1}, {-1,1}, {-1,2} },

      // .X.
      // .XX
      // ..X
      { {-2,1}, {-1,1}, {-1,2}, {0,2} },

      // Other 2 Offsets are the same as the first 2, so
      // just cycle through them
      { {0,0}, {0,1}, {-1,1}, {-1,2} },
      { {-2,1}, {-1,1}, {-1,2}, {0,2} }

    },

    // Preview panel squares
    new int[][]{ {1,2}, {1,3}, {2,1}, {2,2} },

    // Start row
    4,

    // Color
    new Color(170, 45, 255) // Purple
  ),

  STRAIGHT_LINE(

    // Offsets
    new int[][][]{

      // ....
      // ....
      // XXXX
      // ....
      { {-1,0}, {-1,1}, {-1,2}, {-1,3} },

      // .X..
      // .X..
      // .X..
      // .X..
      { {0,1}, {-1,1}, {-2,1}, {-3,1} },

      // Other 2 Offsets are the same as the first 2, so
      // just cycle through them
      { {-1,0}, {-1,1}, {-1,2}, {-1,3} },
      { {0,1}, {-1,1}, {-2,1}, {-3,1} }

     },

     // Preview panel squares
     new int[][]{ {0,2}, {1,2}, {2,2}, {3,2} },

     // Start row
     4,

     // Color
     new Color(0, 200, 200) // Blue-green
  ),

  T_BLOCK(

    // Offsets
    new int[][][]{

      // ...
      // XXX
      // .X.
      { {-1,0}, {-1,1}, {-1,2}, {0,1} },

      // .X.
      // XX.
      // .X.
      { {-1,0}, {-2,1}, {-1,1}, {0,1} },

      // .X.
      // XXX
      // ...
      { {-2,1}, {-1,0}, {-1,1}, {-1,2} },

      // .X.
      // .XX
      // .X.
      { {0,1}, {-1,1}, {-1,2}, {-2,1} }

    },

    // Preview panel squares
    new int[][]{ {1,1}, {1,2}, {1,3}, {2,2} },

    // Start row
    4,

    // Color
    new Color(255, 30, 0) // Red
  ),

  TWIN_PILLARS(

    // Offsets
    new int[][][]{

      // ...
      // X.X
      // X.X
      { {0,0}, {-1,0}, {-1,2}, {0,2} },

      // XX.
      // ...
      // XX.
      { {-2,0}, {-2,1}, {0,0}, {0,1} },

      // Other two offsets are the same, so cycle through them again
      { {0,0}, {-1,0}, {-1,2}, {0,2} },
      { {-2,0}, {-2,1}, {0,0}, {0,1} }

    },

    // Preview panel squares
    new int[][]{ {1,1}, {2,1}, {1,3}, {2,3} },

    // Start row
    4,

    // Color
    new Color(80, 140, 45), // Forest-green

    // Special
    true,

    // Bonus points per line
    4
  ),

  WAVE(

    // Offsets
    new int[][][]{

      // ..X..
      // ..X..
      // .X...
      // .X...
      { {0,1}, {-1,1}, {-2,2}, {-3,2} },


      // ....
      // XX..
      // ..XX
      // ....
      { {-2,0}, {-2,1}, {-1,2}, {-1,3} },

      // Other 2 orientations cycle back through
      { {0,1}, {-1,1}, {-2,2}, {-3,2} },
      { {-2,0}, {-2,1}, {-1,2}, {-1,3} },
    },

    // Preview panel squares
    new int[][]{ {3,1}, {2,1}, {1,2}, {0,2} },

    // Start row
    6,

    // Color
    Color.CYAN,

    // Special
    true,

    // Bonus points per line
    6
  ),

  ROCKET(

    // Offsets
    new int[][][]{

      // .X.
      // .X.
      // X.X
      { {0,0}, {-1,1}, {-2,1}, {0,2} },

      // X..
      // .XX
      // X..
      { {-2,0}, {0,0}, {-1,1}, {-1,2} },

      // X.X
      // .X.
      // .X.
      { {-2,0}, {-2,2}, {-1,1}, {0,1} },

      // ..X
      // XX.
      // ..X
      { {-1,0}, {-1,1}, {0,2}, {-2,2} },

    },

    // Preview panel squares
    new int[][]{ {3,1}, {3,3}, {2,2}, {1,2} },

    // Start row
    5,

    // Color
    Color.ORANGE,

    //Special
    true,

    // Bonus points per line
    8
  ),

  DIAMOND(

    // Offsets. All the same for each direction:
    // .X.
    // X.X
    // .X.
    new int[][][]{
      { {0,1}, {-1,0}, {-1,2}, {-2,1} },
      { {0,1}, {-1,0}, {-1,2}, {-2,1} },
      { {0,1}, {-1,0}, {-1,2}, {-2,1} },
      { {0,1}, {-1,0}, {-1,2}, {-2,1} },
    },

    // Preview panel squares
    new int[][]{ {1,2}, {2,1}, {2,3}, {3,2} },

    // Start row
    4,

    // Color
    Color.LIGHT_GRAY,

    // Special
    true,

    // Bonus points per line
    10
  );

  private final int[][][] offsets;
  private final int[][] previewPanelSquares;
  private final int startRow;
  private final Color color;
  private final boolean isSpecial;
  private final int bonusPointsPerLine;

  BlockType(int[][][] offsets, int[][] previewPanelSquares, int startRow, Color color) {
    this(offsets, previewPanelSquares, startRow, color, false, 0);
  }

  BlockType(int[][][] offsets, int[][] previewPanelSquares, int startRow, Color color, boolean isSpecial, int bonusPointsPerLine) {
    this.offsets = offsets;
    this.previewPanelSquares = previewPanelSquares;
    this.startRow = startRow;
    this.color = color;
    this.isSpecial = isSpecial;
    this.bonusPointsPerLine = bonusPointsPerLine;
  }

  public static Collection<BlockType> getDefaultBlocks() {
    return filterTypes(type -> !type.isSpecial);
  }

  public static Collection<BlockType> getSpecialBlocks() {
    return filterTypes(type -> type.isSpecial);
  }

  private static Collection<BlockType> filterTypes(Predicate<BlockType> test) {
    return Arrays.stream(BlockType.values()).filter(test).collect(toList());
  }

  public Collection<ColoredSquare> getPreviewPanelSquares() {
    return Arrays.stream(previewPanelSquares)
            .map(coordinates -> new ColoredSquare(color, coordinates[0], coordinates[1]))
            .collect(toList());
  }

  public int getStartRow() {
    return startRow;
  }

  public int getBonusPointsPerLine() {
    return bonusPointsPerLine;
  }

  public Color getColor() {
    return color;
  }

  public boolean isSpecial() {
    return isSpecial;
  }

  public Collection<ColoredSquare> calculateOccupiedSquares(int orientation, int row, int col) {
    if (orientation < 0 || orientation > 3) {
      throw new IllegalArgumentException("Orientation value must be between 0 and 3");
    }

    int[][] offsetsForOrientation = this.offsets[orientation];

    return Arrays.stream(offsetsForOrientation)
            .map(offset -> new ColoredSquare(color, row + offset[0], col + offset[1]))
            .collect(toList());
  }

  @Override
  public String toString() {
    return Stream.of(name().split("_")).map(Utility::capitalize).collect(joining(" "));
  }

}
