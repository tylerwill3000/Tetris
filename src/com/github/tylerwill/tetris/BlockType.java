package com.github.tylerwill.tetris;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public enum BlockType {

  BOX(

    // Offsets. All the same for each direction
    // XX
    // XX
    new int[][][]{
      { {0,0},{-1,0},{-1,1},{0,1} },
      { {0,0},{-1,0},{-1,1},{0,1} },
      { {0,0},{-1,0},{-1,1},{0,1} },
      { {0,0},{-1,0},{-1,1},{0,1} }
    },

    // Next panel squares
    new int[][]{ {1,1},{1,2},{2,1},{2,2} },

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
      { {-2,0},{-1,0},{-1,1},{-1,2} },

      // .XX
      // .X.
      // .X.
      { {-2,1},{-2,2},{-1,1},{0,1} },

      // ...
      // XXX
      // ..X
      { {-1,0},{-1,1},{-1,2},{0,2} },

      // .X.
      // .X.
      // XX.
      { {0,0},{0,1},{-1,1},{-2,1} }

    },

    // Next panel squares
    new int[][]{ {1,1},{2,1},{2,2},{2,3} },

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
      { {-1,0},{-1,1},{-1,2},{-2,2} },

      // .X.
      // .X.
      // .XX
      { {0,1},{0,2},{-1,1},{-2,1} },

      // ...
      // XXX
      // X..
      { {0,0},{-1,0},{-1,1},{-1,2} },

      // XX.
      // .X.
      // .X.
      { {-2,0},{-2,1},{-1,1},{0,1} },

    },

    // Next panel squares
    new int[][]{ {1,3},{2,3},{2,2},{2,1} },

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
      { {-1,0},{-1,1},{0,1},{0,2} },

      // .X.
      // XX.
      // X..
      { {0,0},{-1,0},{-1,1},{-2,1} },

      // Other 2 Offsets are the same as the first 2, so
      // just cycle through them
      { {-1,0},{-1,1},{0,1},{0,2} },
      { {0,0},{-1,0},{-1,1},{-2,1} }

    },

    // Next panel squares
    new int[][]{ {1,1},{1,2},{2,2},{2,3} },

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
      { {0,0},{0,1},{-1,1},{-1,2} },

      // .X.
      // .XX
      // ..X
      { {-2,1},{-1,1},{-1,2},{0,2} },

      // Other 2 Offsets are the same as the first 2, so
      // just cycle through them
      { {0,0},{0,1},{-1,1},{-1,2} },
      { {-2,1},{-1,1},{-1,2},{0,2} }

    },

    // Next panel squares
    new int[][]{ {1,2},{1,3},{2,1},{2,2} },

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
      { {-1,0},{-1,1},{-1,2},{-1,3} },

      // .X..
      // .X..
      // .X..
      // .X..
      { {0,1},{-1,1},{-2,1},{-3,1} },

      // Other 2 Offsets are the same as the first 2, so
      // just cycle through them
      { {-1,0},{-1,1},{-1,2},{-1,3} },
      { {0,1},{-1,1},{-2,1},{-3,1} }

    },

    // Next panel squares
    new int[][]{ {0,2},{1,2},{2,2},{3,2} },

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
      { {-1,0},{-1,1},{-1,2},{0,1} },

      // .X.
      // XX.
      // .X.
      { {-1,0},{-2,1},{-1,1},{0,1} },

      // .X.
      // XXX
      // ...
      { {-2,1},{-1,0},{-1,1},{-1,2} },

      // .X.
      // .XX
      // .X.
      { {0,1},{-1,1},{-1,2},{-2,1} }

    },

    // Next panel squares
    new int[][]{ {1,1},{1,2},{1,3},{2,2} },

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
      { {0,0},{-1,0},{-1,2},{0,2} },

      // XX.
      // ...
      // XX.
      { {-2,0},{-2,1},{0,0},{0,1} },

      // Other two Offsets are the same, so cycle through them again
      { {0,0},{-1,0},{-1,2},{0,2} },
      { {-2,0},{-2,1},{0,0},{0,1} },

    },

    // Next panel squares
    new int[][]{ {1,1},{2,1},{1,3},{2,3} },

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
      {{0, 1}, {-1, 1}, {-2, 2}, {-3, 2}},


      // ....
      // XX..
      // ..XX
      // ....
      {{-2, 0}, {-2, 1}, {-1, 2}, {-1, 3}},

      // Other 2 orientations cycle back through
      {{0, 1}, {-1, 1}, {-2, 2}, {-3, 2}},
      {{-2, 0}, {-2, 1}, {-1, 2}, {-1, 3}},
    },

    // Next panel squares
    new int[][]{ {3,1},{2,1},{1,2},{0,2} },

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
      { {0,0},{-1,1},{-2,1},{0,2} },

      // X..
      // .XX
      // X..
      { {-2,0},{0,0},{-1,1},{-1,2} },

      // X.X
      // .X.
      // .X.
      { {-2,0},{-2,2},{-1,1},{0,1} },

      // ..X
      // XX.
      // ..X
      { {-1,0},{-1,1},{0,2},{-2,2} },

    },

    // Next panel squares
    new int[][]{ {3,1},{3,3},{2,2},{1,2} },

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
      { {0,1},{-1,0},{-1,2},{-2,1} },
      { {0,1},{-1,0},{-1,2},{-2,1} },
      { {0,1},{-1,0},{-1,2},{-2,1} },
      { {0,1},{-1,0},{-1,2},{-2,1} }
    },

    // Next panel squares
    new int[][]{ {1,2},{2,1},{2,3},{3,2} },

    // Start row
    4,

    // Color
    Color.LIGHT_GRAY,

    // Special
    true,

    // Bonus points per line
    10
  ),

  ;

  private int[][][] offsetMap;
  private int[][] nextPanelSquares;
  private int startRow;
  private Color color;
  private boolean special;
  private int bonusPointsPerLine;

  BlockType(int[][][] offsetMap, int[][] nextPanelSquares, int startRow, Color color) {
    this(offsetMap, nextPanelSquares, startRow, color, false, 0);
  }

  BlockType(int[][][] offsetMap, int[][] nextPanelSquares, int startRow, Color color, boolean special, int bonusPointsPerLine) {
    this.offsetMap = offsetMap;
    this.nextPanelSquares = nextPanelSquares;
    this.startRow = startRow;
    this.color = color;
    this.special = special;
    this.bonusPointsPerLine = bonusPointsPerLine;
  }

  public List<Block.ColoredSquare> getNextPanelSquares() {
    return Arrays.stream(nextPanelSquares)
                 .map(coordinates -> new Block.ColoredSquare(color, coordinates[0], coordinates[1]))
                 .collect(toList());
  }

  public int getStartRow() {
    return startRow;
  }

  public int getBonusPointsPerLine() {
    return bonusPointsPerLine;
  }

  /** Calculates the list of squares this type occupies at the given orientation, row and column */
  public List<Block.ColoredSquare> calcOccupiedSquares(int orientation, int row, int col) {
    if (orientation < 0 || orientation > 3) {
      throw new IllegalArgumentException("Orientation value must be between 0 and 3");
    }
    int[][] offsets = offsetMap[orientation];
    return Arrays.stream(offsets)
                 .map(offset -> new Block.ColoredSquare(color, row + offset[0], col + offset[1]))
                 .collect(toList());
  }

  @Override
  public String toString() {
    return Arrays.stream(name().split("_"))
                 .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                 .collect(Collectors.joining(" "));
  }

  public static List<BlockType> getDefaultBlocks() {
    return Arrays.stream(BlockType.values()).filter(type -> !type.special).collect(toList());
  }

  public static List<BlockType> getSpecialBlocks() {
    return Arrays.stream(BlockType.values()).filter(type -> type.special).collect(toList());
  }

  public static Color getRandomColor() {
    return Utility.sample(values()).color;
  }

}
