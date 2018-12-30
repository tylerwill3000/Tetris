package com.github.tylersharpe.tetris;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Block {

  private Type type;
  private int row, column;
  private int orientation;
  private boolean isHoldBlock;

  public Block(Type type) {
    this.type = type;
  }

  int getRow() {
    return row;
  }

  int getColumn() {
    return column;
  }

  Collection<ColoredSquare> getOccupiedSquares() {
    return type.calculateOccupiedSquares(orientation, getRow(), getColumn());
  }

  public Collection<ColoredSquare> getNextPanelSquares() {
    return type.getNextPanelSquares();
  }

  public void tagAsHoldBlock() {
    isHoldBlock = true;
  }

  public boolean isHoldBlock() {
    return isHoldBlock;
  }

  Type getType() {
    return type;
  }

  void move(int rowMove, int colMove) {
    setLocation(row + rowMove, column + colMove);
  }

  void setLocation(int row, int col) {
    this.row = row;
    this.column = col;
  }

  void rotate(Rotation rotation) {
    int orientationChange = rotation == Rotation.CLOCKWISE ? 1 : -1;
    orientation += orientationChange;
    if (orientation > 3) orientation = 0;
    if (orientation < 0) orientation = 3;
  }

  public static class ColoredSquare {

    private Color color;
    private int row, col;

    public ColoredSquare(int row, int col) {
      this(Type.getRandomColor(), row, col);
    }

    public ColoredSquare(Color color, int row, int col) {
      this.color = color;
      this.row = row;
      this.col = col;
    }

    public Color getColor() {
      return color;
    }

    void clearColor() {
      this.color = null;
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return col;
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, col);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ColoredSquare) {
        ColoredSquare other = (ColoredSquare) o;
        return row == other.row && col == other.col;
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format("(row=%d, column=%d, color=%s)", row, col, color);
    }
  }

  public enum Type {

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

      // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

       // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

      // Next panel squares
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

    private int[][][] offsetMap;
    private int[][] nextPanelSquares;
    private int startRow;
    private Color color;
    private boolean special;
    private int bonusPointsPerLine;

    Type(int[][][] offsetMap, int[][] nextPanelSquares, int startRow, Color color) {
      this(offsetMap, nextPanelSquares, startRow, color, false, 0);
    }

    Type(int[][][] offsetMap, int[][] nextPanelSquares, int startRow, Color color, boolean special, int bonusPointsPerLine) {
      this.offsetMap = offsetMap;
      this.nextPanelSquares = nextPanelSquares;
      this.startRow = startRow;
      this.color = color;
      this.special = special;
      this.bonusPointsPerLine = bonusPointsPerLine;
    }

    public Collection<Block.ColoredSquare> getNextPanelSquares() {
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

    public Collection<Block.ColoredSquare> calculateOccupiedSquares(int orientation, int row, int col) {
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

    public static Collection<Type> getDefaultBlocks() {
      return filterTypes(type -> !type.special);
    }

    public static Collection<Type> getSpecialBlocks() {
      return filterTypes(type -> type.special);
    }

    private static Collection<Type> filterTypes(Predicate<Type> test) {
      return Arrays.stream(Type.values()).filter(test).collect(toList());
    }

    public static Color getRandomColor() {
      return Utility.sample(values()).color;
    }

  }

}
