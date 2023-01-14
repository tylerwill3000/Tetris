package com.github.tylersharpe.tetris;

import com.github.tylersharpe.tetris.event.Broker;
import com.github.tylersharpe.tetris.event.TetrisEvent;

import javax.swing.Timer;
import java.awt.*;
import java.util.List;
import java.util.*;

public class TetrisGame extends Broker {

    public static final int MAX_LEVEL = 10;
    public static final int VERTICAL_DIMENSION = 23; // includes 3 invisible rows at top
    public static final int HORIZONTAL_DIMENSION = 10;

    private Block activeBlock;
    private Block holdBlock;
    private final BlockConveyor conveyor;
    private final LinkedList<Color[]> persistedBlocks; // Persisted colors for previous blocks; doesn't include active block squares
    private Difficulty difficulty;
    private int totalLinesCleared;
    private int score;
    private int level;
    private int gameTimeSeconds;
    private boolean ghostSquaresEnabled = true;
    private boolean timeAttack;
    private int currentLevelTime;
    private final Timer fallTimer;
    private final Timer gameTimer;
    private boolean isGameWon;

    public TetrisGame() {
        this.conveyor = new BlockConveyor();

        this.persistedBlocks = new LinkedList<>();
        for (int i = 1; i <= VERTICAL_DIMENSION; i++) {
            persistedBlocks.add(new Color[HORIZONTAL_DIMENSION]);
        }

        this.fallTimer = new Timer(0, e -> tryMoveActiveBlockDown());

        this.gameTimer = new Timer(1000, e -> {
            setGameTime(gameTimeSeconds + 1);
            currentLevelTime++;

            if (timeAttack && currentLevelTime >= difficulty.getTimeAttackSecondsPerLevel()) {
                publish(TetrisEvent.TIME_ATTACK_FAIL);
                ((Timer) e.getSource()).stop();
                fallTimer.stop();
            }
        });
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
        return gameTimeSeconds;
    }

    private void setGameTime(int time) {
        gameTimeSeconds = time;
    }

    public BlockConveyor getConveyor() {
        return conveyor;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.conveyor.setDifficulty(difficulty);
        this.fallTimer.setDelay(difficulty.getInitialTimerDelay());
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

    public void clearSquare(int row, int column) {
        setColor(row, column, null);
    }

    public boolean isOpenAndInBounds(int row, int column) {
        boolean isInBounds = row >= 0 && row < VERTICAL_DIMENSION && column >= 0 && column < HORIZONTAL_DIMENSION;
        return isInBounds && persistedBlocks.get(row)[column] == null;
    }

    public void setColor(int row, int col, Color color) {
        persistedBlocks.get(row)[col] = color;
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    public int getCurrentLevelLinesCleared() {
        int lastLevelThreshold = level == 0 ? 0 : difficulty.getLinesPerLevel() * (level - 1);
        return totalLinesCleared - lastLevelThreshold;
    }

    public boolean moveActiveBlockRight() {
        return moveBlock(activeBlock, 0, 1);
    }

    public boolean moveActiveBlockLeft() {
        return moveBlock(activeBlock, 0, -1);
    }

    public boolean moveActiveBlockDown() {
        return moveBlock(activeBlock, 1, 0);
    }

    public void clearActiveBlock() {
        this.activeBlock = null;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void dropCurrentBlock() {
        while (moveActiveBlockDown()) {
            // Move method returns boolean to control loop
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void superSlideActiveBlockLeft() {
        while (moveActiveBlockLeft()) {
            // Move method returns boolean to control loop
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void superSlideActiveBlockRight() {
        while (moveActiveBlockRight()) {
            // Move method returns boolean to control loop
        }
    }

    private boolean moveBlock(Block block, int rowMove, int columnMove) {
        boolean canMoveBeMade = block.calculateOccupiedSquares()
                .stream()
                .map(currentSquare -> {
                    int potentialRow = currentSquare.row() + rowMove;
                    int potentialColumn = currentSquare.column() + columnMove;
                    return new ColoredSquare(BlockType.getRandomColor(),potentialRow, potentialColumn);
                })
                .allMatch(squareAfterMove -> isOpenAndInBounds(squareAfterMove.row(), squareAfterMove.column()));

        if (canMoveBeMade) {
            block.move(rowMove, columnMove);
            return true;
        } else {
            return false;
        }
    }

    public boolean rotateActiveBlock(Rotation rotation) {
        Collection<ColoredSquare> squaresAfterRotation = activeBlock.copy().rotate(rotation).calculateOccupiedSquares();

        boolean areRotatedSquaresLegal = squaresAfterRotation
                .stream()
                .allMatch(squareAfterRotation -> isOpenAndInBounds(squareAfterRotation.row(), squareAfterRotation.column()));

        if (areRotatedSquaresLegal) {
            activeBlock.rotate(rotation);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private Collection<ColoredSquare> getGhostSquares() {
        if (activeBlock == null) {
            return List.of();
        }

        Block activeBlockCopy = activeBlock.copy();
        Collection<ColoredSquare> currentActiveBlockSquares = activeBlockCopy.calculateOccupiedSquares();

        while (moveBlock(activeBlockCopy, 1, 0)) {
            // drop as far as possible
        }
        Collection<ColoredSquare> ghostSquares = activeBlockCopy.calculateOccupiedSquares();

        // remove any ghost squares that overlap with the current active block
        ghostSquares.removeIf(ghostSquare ->
                currentActiveBlockSquares.stream().anyMatch(activeBlockSquare ->
                        activeBlockSquare.row() == ghostSquare.row() && activeBlockSquare.column() == ghostSquare.column()
                )
        );

        return ghostSquares.stream()
                .map(sq -> new ColoredSquare(null, sq.row(), sq.column()))
                .toList();
    }

    /**
     * Attempts to vertically drop the current active piece 1 square.
     * If the piece could not be dropped, its colors are logged to the color grid and any complete rows removed
     */
    public void tryMoveActiveBlockDown() {
        if (moveActiveBlockDown()) {
            return;
        }

        persistActiveBlockColors();
        int linesCleared = clearCompleteLines();

        if (linesCleared > 0) {
            increaseScore(linesCleared);
            publish(TetrisEvent.LINES_CLEARED, linesCleared);
        }

        if (!isGameWon) {
            spawn(conveyor.next());
        }
    }

    private int clearCompleteLines() {
        int completeRowScanIndex = Math.min(activeBlock.getRow(), VERTICAL_DIMENSION - 1);
        int minRowScanIndex = Math.max(0, completeRowScanIndex - 3);

        int linesCleared = 0;
        while (completeRowScanIndex >= minRowScanIndex && linesCleared <= 4) {

            Color[] rowToScan = persistedBlocks.get(completeRowScanIndex);
            boolean isRowComplete = Arrays.stream(rowToScan).allMatch(Objects::nonNull);
            if (isRowComplete) {
                persistedBlocks.remove(completeRowScanIndex);
                persistedBlocks.offerFirst(new Color[HORIZONTAL_DIMENSION]);
                linesCleared++;
            } else {
                completeRowScanIndex--;
            }
        }

        return linesCleared;
    }

    public void persistActiveBlockColors() {
        if (activeBlock != null) {
            for (var square : activeBlock.calculateOccupiedSquares()) {
                setColor(square.row(), square.column(), square.color());
            }
        }
    }

    public void reset() {
        setGameTime(0);
        setScore(0);
        setLevel(1);

        this.isGameWon = false;
        this.totalLinesCleared = 0;
        this.currentLevelTime = 0;

        clearActiveBlock();
        clearHoldBlock();

        this.persistedBlocks.forEach(row -> Arrays.fill(row, null));

        this.conveyor.reset();
        spawn(this.conveyor.next());

        this.gameTimer.start();
        this.fallTimer.start();
    }

    private void increaseScore(int completedLines) {
        int newScore = this.score;

        newScore += switch (completedLines) {
            case 1 -> 10;
            case 2 -> 30;
            case 3 -> 50;
            case 4 -> 100;
            default -> throw new RuntimeException("Completed lines must be in range 1 - 4");
        };

        // Special pieces bonus
        newScore += conveyor.getEnabledSpecialBlockTypes()
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
     */
    public void spawn(Block block) {
        int startRow = block.getType().getStartRow();
        int startCol = HORIZONTAL_DIMENSION / 2;

        while (true) {
            var spawnSquares = block.getType().calculateOccupiedSquares(0, startRow, startCol);

            boolean anyVisible = spawnSquares.stream().anyMatch(square -> square.row() >= 3);
            if (!anyVisible) {
                fallTimer.stop();
                gameTimer.stop();
                publish(TetrisEvent.SPAWN_FAIL, block);
                return;
            }

            boolean allOpen = spawnSquares.stream().allMatch(square -> isOpenAndInBounds(square.row(), square.column()));
            if (allOpen) {
                block.setLocation(startRow, startCol);
                this.activeBlock = block;
                return;
            } else {
                startRow--; // Try to push piece upwards past board bounds if we can
            }
        }
    }

    public Collection<ColoredSquare> getColoredSquares() {
        List<ColoredSquare> squares = new ArrayList<>(HORIZONTAL_DIMENSION * VERTICAL_DIMENSION);

        if (activeBlock != null) {
            squares.addAll(activeBlock.calculateOccupiedSquares());
            if (this.ghostSquaresEnabled) {
                squares.addAll(getGhostSquares());
            }
        }

        for (int rowIndex = 0; rowIndex < VERTICAL_DIMENSION; rowIndex++) {
            Color[] rowColors = persistedBlocks.get(rowIndex);
            for (int columnIndex = 0; columnIndex < rowColors.length; columnIndex++) {
                if (rowColors[columnIndex] != null) {
                    squares.add(new ColoredSquare(rowColors[columnIndex], rowIndex, columnIndex));
                }
            }
        }

        return squares;
    }

}
