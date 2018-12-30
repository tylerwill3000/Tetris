package com.github.tylersharpe.tetris;

import com.github.tylersharpe.tetris.event.TetrisEventBus;
import com.github.tylersharpe.tetris.event.TetrisEvent;

import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

public class TetrisGame extends TetrisEventBus {

  public static final int MAX_LEVEL = 10;
  private static final int VERTICAL_CELLS = 23;
  private static final int HORIZONTAL_CELLS = 10;

  private Block activeBlock;
  private Block holdBlock;
  private BlockConveyor conveyor = new BlockConveyor();
  private LinkedList<Color[]> persistedBlocks; // Persisted colors for previous blocks; doesn't include active block squares
  private Difficulty difficulty;
  private int totalLinesCleared;
  private int score;
  private int level;
  private int verticalDimension;
  private int gameTime;
  private int horizontalDimension;
  private boolean ghostSquaresEnabled = true;
  private boolean timeAttack;
  private int currentLevelTime;
  private Timer fallTimer;
  private Timer gameTimer;
  private boolean isGameWon;

  public TetrisGame() {
    this(VERTICAL_CELLS, HORIZONTAL_CELLS);
  }

  private TetrisGame(int verticalDimension, int horizontalDimension) {
    this.verticalDimension = verticalDimension;
    this.horizontalDimension = horizontalDimension;
    this.persistedBlocks = IntStream.range(0, verticalDimension)
                                    .mapToObj(i -> new Color[horizontalDimension])
                                    .collect(toCollection(LinkedList::new));

    this.fallTimer = new Timer(0, e -> tryFall());

    this.gameTimer = new Timer(1000, e -> {
      setGameTime(gameTime + 1);
      currentLevelTime++;

      if (timeAttack && currentLevelTime >= difficulty.getTimeAttackSecondsPerLevel()) {
        publish(TetrisEvent.TIME_ATTACK_FAIL);
        ((Timer) e.getSource()).stop();
        fallTimer.stop();
      }
    });
  }

  public int getHorizontalDimension() {
    return horizontalDimension;
  }

  public int getVerticalDimension() {
    return verticalDimension;
  }

  public Block getActiveBlock() {
    return activeBlock;
  }

  public Optional<Block> getHoldBlock() {
    return Optional.ofNullable(holdBlock);
  }

  public void setHoldBlock(Block block) {
    this.holdBlock = block;
  }

  public void clearHoldBlock() {
    setHoldBlock(null);
  }

  public void setGhostSquaresEnabled(boolean ghostSquaresEnabled) {
    this.ghostSquaresEnabled = ghostSquaresEnabled;
  }

  public Timer getFallTimer() {
    return fallTimer;
  }

  public Timer getGameTimer() {
    return gameTimer;
  }

  public int getScore() {
    return score;
  }

  private void setScore(int newScore) {
    if (newScore != this.score) {
      this.score = newScore;
      publish(TetrisEvent.SCORE_CHANGED, score);
    }
  }

  public int getGameTime() {
    return gameTime;
  }

  private void setGameTime(int time) {
    gameTime = time;
  }

  public BlockConveyor getConveyor() {
    return conveyor;
  }

  public Difficulty getDifficulty() {
    return this.difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
    this.conveyor.applySpawnRates(difficulty);
    this.fallTimer.setDelay(difficulty.initialTimerDelay);
  }

  public boolean isTimeAttack() {
    return this.timeAttack;
  }

  public void setTimeAttack(boolean timeAttack) {
    this.timeAttack = timeAttack;
  }

  public int getCurrentLevelTime() {
    return this.currentLevelTime;
  }

  public int getLevel() {
    return Math.min(level, MAX_LEVEL);
  }

  private void setLevel(int newLevel) {
    this.level = newLevel;

    if (newLevel > MAX_LEVEL) {
      this.isGameWon = true;
      fallTimer.stop();
      gameTimer.stop();
      clearActiveBlock(); // Needed so that this block's squares don't get re-painted during victory clear animation
      publish(TetrisEvent.GAME_WON, level);
    } else {
      int initialDelay = difficulty.getInitialTimerDelay();
      int totalSpeedup = (level - 1) * Difficulty.TIMER_SPEEDUP;
      int newDelay = initialDelay - totalSpeedup;
      fallTimer.setDelay(newDelay);
      this.currentLevelTime = 0;
      publish(TetrisEvent.LEVEL_CHANGED, newLevel);
    }
  }

  public void clearSquare(int row, int col) {
    setColor(row, col, null);
  }

  public boolean isOpen(int row, int col) {
    return getColor(row, col) == null;
  }

  private Color getColor(int row, int col) {
    return persistedBlocks.get(row)[col];
  }

  public void setColor(int row, int col, Color c) {
    persistedBlocks.get(row)[col] = c;
  }

  public int getTotalLinesCleared() {
    return totalLinesCleared;
  }

  public int getCurrentLevelLinesCleared() {
    int lastLevelThreshold = level == 0 ? 0 : difficulty.getLinesPerLevel() * (level - 1);
    return totalLinesCleared - lastLevelThreshold;
  }

  public boolean moveActiveBlockRight() {
    return moveActiveBlock(0, 1);
  }

  public boolean moveActiveBlockLeft() {
    return moveActiveBlock(0, -1);
  }

  public boolean moveActiveBlockDown() {
    return moveActiveBlock(1, 0);
  }

  public void clearActiveBlock() {
    this.activeBlock = null;
  }

  public void dropCurrentBlock() {
    while (moveActiveBlockDown()) {
      // Move method returns boolean to control loop
    }
  }

  public void superSlideActiveBlockLeft() {
    while (moveActiveBlockLeft()) {
      // Move method returns boolean to control loop
    }
  }

  public void superSlideActiveBlockRight() {
    while (moveActiveBlockRight()) {
      // Move method returns boolean to control loop
    }
  }

  /** @return True if piece could successfully move, false if there was not enough room */
  private boolean moveActiveBlock(int rowMove, int colMove) {
    boolean canMoveBeMade = activeBlock.getOccupiedSquares()
            .stream()
            .map(square -> new Block.ColoredSquare(square.getRow() + rowMove, square.getColumn() + colMove))
            .allMatch(moveSquare -> isInBounds(moveSquare.getRow(), moveSquare.getColumn()) &&
                                    isOpen(moveSquare.getRow(), moveSquare.getColumn()));

    if (canMoveBeMade) {
      activeBlock.move(rowMove, colMove);
      return true;
    } else {
      return false;
    }
  }

  public boolean rotateActiveBlock(Rotation rotation) {
    activeBlock.rotate(rotation);

    boolean areRotatedSquaresLegal = activeBlock.getOccupiedSquares()
            .stream()
            .allMatch(moveSquare -> isInBounds(moveSquare.getRow(), moveSquare.getColumn()) &&
                                    isOpen(moveSquare.getRow(), moveSquare.getColumn()));
    if (areRotatedSquaresLegal) {
      return true;
    } else {
      activeBlock.rotate(rotation.reverse());
      return false;
    }
  }

  private Collection<Block.ColoredSquare> getGhostSquares() {
    if (activeBlock == null) {
      return List.of();
    }

    int currentRow = activeBlock.getRow();
    int currentCol = activeBlock.getColumn();

    dropCurrentBlock();

    Collection<Block.ColoredSquare> ghostSquares = activeBlock.getOccupiedSquares();
    ghostSquares.forEach(Block.ColoredSquare::clearColor);

    // Returns block to location it was in before dropping to ghost position
    activeBlock.setLocation(currentRow, currentCol);

    return ghostSquares;
  }

  /**
   * Attempts to vertically drop the current active piece 1 square.
   * If the piece could not be dropped, its colors are logged to the color grid and any complete rows removed
   */
  public void tryFall() {
    if (moveActiveBlockDown()) {
      return;
    }

    logActiveBlock();

    int completeRowScanIndex = Math.min(activeBlock.getRow(), verticalDimension - 1);
    int minRowScanIndex = Math.max(0, completeRowScanIndex - 3);

    int linesCleared = 0;
    while (completeRowScanIndex >= minRowScanIndex && linesCleared <= 4) {

      Color[] rowToScan = persistedBlocks.get(completeRowScanIndex);
      boolean isRowComplete = Arrays.stream(rowToScan).allMatch(Objects::nonNull);
      if (isRowComplete) {
        persistedBlocks.remove(completeRowScanIndex);
        persistedBlocks.offerFirst(new Color[horizontalDimension]);
        linesCleared++;
      } else {
        completeRowScanIndex--;
      }
    }

    if (linesCleared > 0) {
      increaseScore(linesCleared);
      publish(TetrisEvent.LINES_CLEARED, linesCleared);
    }

    if (!isGameWon) {
      spawn(conveyor.next());
    }
  }

  public void logActiveBlock() {
    if (activeBlock != null) {
      for (var square : activeBlock.getOccupiedSquares()) {
        setColor(square.getRow(), square.getColumn(), square.getColor());
      }
    }
  }

  public void beginNew() {
    setGameTime(0);
    setScore(0);
    setLevel(1);

    this.isGameWon = false;
    this.totalLinesCleared = 0;
    this.currentLevelTime = 0;

    clearActiveBlock();
    clearHoldBlock();

    this.persistedBlocks.forEach(row -> Arrays.fill(row, null));

    this.conveyor.prepareForStart();
    spawn(this.conveyor.next());

    this.gameTimer.start();
    this.fallTimer.start();
  }

  private void increaseScore(int completedLines) {
    int newScore = this.score;

    switch (completedLines) {
      case 1: newScore += 10; break;
      case 2: newScore += 30; break;
      case 3: newScore += 50; break;
      case 4: newScore += 100; break;
    }

    // Special pieces bonus
    newScore += conveyor.getEnabledSpecials()
                        .stream()
                        .mapToInt(special -> completedLines * special.getBonusPointsPerLine())
                        .sum();

    int maxGameLines = difficulty.getLinesPerLevel() * MAX_LEVEL;
    totalLinesCleared = Math.min(maxGameLines, totalLinesCleared + completedLines);

    int levelsCompleted = totalLinesCleared / difficulty.getLinesPerLevel();
    int newLevel = levelsCompleted + 1;
    int levelIncrease = newLevel - this.level;

    if (timeAttack && levelIncrease > 0) {
      newScore += (difficulty.getTimeAttackBonus() * levelIncrease);
    }

    if (newLevel > MAX_LEVEL) {
      newScore += difficulty.getWinBonus();
    }

    if (levelIncrease > 0) {
      setLevel(newLevel);
    }

    setScore(newScore);
  }

  /**
   * Attempts to spawn the given block object in the board model, replacing the current active block.
   * @return True if there was room to spawn the piece, false otherwise
   */
  public boolean spawn(Block block) {
    int startRow = block.getType().getStartRow();
    int startCol = horizontalDimension / 2;

    while (true) {
      var spawnSquares = block.getType().calculateOccupiedSquares(0, startRow, startCol);

      boolean anyVisible = spawnSquares.stream().anyMatch(square -> square.getRow() >= 3);
      if (!anyVisible) {
        fallTimer.stop();
        gameTimer.stop();
        publish(TetrisEvent.SPAWN_FAIL, block);
        return false;
      }

      boolean allOpen = spawnSquares.stream().allMatch(square -> isOpen(square.getRow(), square.getColumn()));
      if (allOpen) {
        block.setLocation(startRow, startCol);
        this.activeBlock = block;
        return true;
      } else {
        startRow--; // Try to push piece upwards past board bounds if we can
      }
    }
  }

  public Collection<Block.ColoredSquare> getColoredSquares() {

    Set<Block.ColoredSquare> squares = new HashSet<>();

    // Important we add occupied squares before ghost squares so that ghost squares don't overwrite occupied squares
    if (activeBlock != null) {
      squares.addAll(activeBlock.getOccupiedSquares());
      if (this.ghostSquaresEnabled) {
        squares.addAll(getGhostSquares());
      }
    }

    for (int rowIndex = 0; rowIndex < verticalDimension; rowIndex++) {
      Color[] rowColors = persistedBlocks.get(rowIndex);
      for (int colIndex = 0; colIndex < rowColors.length; colIndex++) {
        if (rowColors[colIndex] != null) {
          squares.add(new Block.ColoredSquare(rowColors[colIndex], rowIndex, colIndex));
        }
      }
    }

    return squares;
  }

  private boolean isInBounds(int row, int col) {
    return row >= 0 && row < verticalDimension && col >= 0 && col < horizontalDimension;
  }

}
