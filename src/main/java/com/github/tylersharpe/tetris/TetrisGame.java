package com.github.tylersharpe.tetris;

import com.github.tylersharpe.tetris.event.Broker;
import com.github.tylersharpe.tetris.event.TetrisEvent;

import javax.swing.Timer;
import java.awt.*;
import java.util.List;
import java.util.*;

public class TetrisGame extends Broker {
    public static final int FREE_PLAY_MINIMUM_FALL_TIMER_DELAY = 50;
    public static final int LEADING_OVERFLOW_ROWS = 3;
    public static final int MAX_LEVEL = 10;
    public static final int VERTICAL_DIMENSION = 20 + LEADING_OVERFLOW_ROWS;
    public static final int HORIZONTAL_DIMENSION = 10;

    private GameMode gameMode;
    private Tetronimo activeTetronimo;
    private Tetronimo holdTetronimo;
    private final TetronimoConveyor conveyor;
    private final LinkedList<Color[]> persistedSquares; // persisted colors for placed tetronimos; doesn't include active tetronimo squares
    private Difficulty difficulty;
    private int totalLinesCleared;
    private int score;
    private int level;
    private int gameTimeSeconds;
    private boolean ghostSquaresEnabled = true;
    private int currentLevelTime;
    private final Timer fallTimer;
    private final Timer gameTimer;
    private boolean isGameWon;

    public TetrisGame() {
        this.gameMode = GameMode.CAMPAIGN;
        this.conveyor = new TetronimoConveyor();

        this.persistedSquares = new LinkedList<>();
        for (int i = 1; i <= VERTICAL_DIMENSION; i++) {
            persistedSquares.add(new Color[HORIZONTAL_DIMENSION]);
        }

        this.fallTimer = new Timer(0, e -> tryMoveActiveTetronimoDown());

        this.gameTimer = new Timer(1000, e -> {
            setGameTime(gameTimeSeconds + 1);
            currentLevelTime++;

            if (gameMode == GameMode.TIME_ATTACK && currentLevelTime >= difficulty.getTimeAttackSecondsPerLevel()) {
                publish(TetrisEvent.TIME_ATTACK_FAIL);
                ((Timer) e.getSource()).stop();
                fallTimer.stop();
            }
        });
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Tetronimo getActiveTetronimo() {
        return activeTetronimo;
    }

    public Optional<Tetronimo> getHoldTetronimo() {
        return Optional.ofNullable(holdTetronimo);
    }

    public void setHoldTetronimo(Tetronimo tetronimo) {
        this.holdTetronimo = tetronimo;
    }

    public void clearHoldTetronimo() {
        setHoldTetronimo(null);
    }

    public void setGhostSquaresEnabled(boolean ghostSquaresEnabled) {
        this.ghostSquaresEnabled = ghostSquaresEnabled;
    }

    public boolean isGhostSquaresEnabled() {
        return ghostSquaresEnabled;
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

    public TetronimoConveyor getConveyor() {
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
            clearActiveTetronimo(); // Needed so that this tetronimo's squares don't get re-painted during victory clear animation
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
        return isInBounds && persistedSquares.get(row)[column] == null;
    }

    public void setColor(int row, int col, Color color) {
        persistedSquares.get(row)[col] = color;
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    public int getCurrentLevelLinesCleared() {
        int lastLevelThreshold = level == 0 ? 0 : difficulty.getLinesPerLevel() * (level - 1);
        return totalLinesCleared - lastLevelThreshold;
    }

    public boolean moveActiveTetronimoRight() {
        return moveTetronimo(activeTetronimo, 0, 1);
    }

    public boolean moveActiveTetronimoLeft() {
        return moveTetronimo(activeTetronimo, 0, -1);
    }

    public boolean moveActiveTetronimoDown() {
        return moveTetronimo(activeTetronimo, 1, 0);
    }

    public void clearActiveTetronimo() {
        this.activeTetronimo = null;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void dropCurrentTetronimo() {
        while (moveActiveTetronimoDown()) {}
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void superSlideActiveTetronimoLeft() {
        while (moveActiveTetronimoLeft()) {}
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void superSlideActiveTetronimoRight() {
        while (moveActiveTetronimoRight()) {}
    }

    private boolean moveTetronimo(Tetronimo tetronimo, int rowMove, int columnMove) {
        boolean canMoveBeMade = tetronimo.calculateOccupiedSquares()
                .stream()
                .allMatch(currentSquare -> {
                    int potentialRow = currentSquare.row() + rowMove;
                    int potentialColumn = currentSquare.column() + columnMove;
                    return isOpenAndInBounds(potentialRow, potentialColumn);
                });

        if (canMoveBeMade) {
            tetronimo.move(rowMove, columnMove);
            return true;
        } else {
            return false;
        }
    }

    public boolean rotateActiveTetronimo(Rotation rotation) {
        Collection<ColoredSquare> squaresAfterRotation = activeTetronimo.copy().rotate(rotation).calculateOccupiedSquares();

        boolean areRotatedSquaresLegal = squaresAfterRotation
                .stream()
                .allMatch(squareAfterRotation -> isOpenAndInBounds(squareAfterRotation.row(), squareAfterRotation.column()));

        if (areRotatedSquaresLegal) {
            activeTetronimo.rotate(rotation);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private Collection<ColoredSquare> getGhostSquares() {
        if (activeTetronimo == null) {
            return List.of();
        }

        Tetronimo activeTetronimoCopy = activeTetronimo.copy();
        Collection<ColoredSquare> currentActiveTetronimoSquares = activeTetronimoCopy.calculateOccupiedSquares();

        while (moveTetronimo(activeTetronimoCopy, 1, 0)) {
            // drop as far as possible
        }
        Collection<ColoredSquare> ghostSquares = activeTetronimoCopy.calculateOccupiedSquares();

        // remove any ghost squares that overlap with the current active tetronimo
        ghostSquares.removeIf(ghostSquare ->
                currentActiveTetronimoSquares.stream().anyMatch(activeTetronimoSquare ->
                        activeTetronimoSquare.row() == ghostSquare.row() && activeTetronimoSquare.column() == ghostSquare.column())
        );

        return ghostSquares.stream()
                .map(sq -> new ColoredSquare(null, sq.row(), sq.column()))
                .toList();
    }

    /**
     * Attempts to vertically drop the current active tetronimo 1 square.
     * If the tetronimo could not be dropped, its colors are logged to the color grid and any complete rows removed
     */
    public void tryMoveActiveTetronimoDown() {
        if (moveActiveTetronimoDown()) {
            return;
        }

        persistActiveTetronimoColors();
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
        int indexOfRowToCheck = Math.min(activeTetronimo.getRow(), VERTICAL_DIMENSION - 1);
        int minRowToCheck = Math.max(0, indexOfRowToCheck - 3);

        int linesCleared = 0;
        while (indexOfRowToCheck >= minRowToCheck && linesCleared <= 4) {

            Color[] rowToScan = persistedSquares.get(indexOfRowToCheck);
            boolean isRowComplete = Arrays.stream(rowToScan).allMatch(Objects::nonNull);
            if (isRowComplete) {
                persistedSquares.remove(indexOfRowToCheck);
                persistedSquares.offerFirst(new Color[HORIZONTAL_DIMENSION]);
                linesCleared++;
            } else {
                indexOfRowToCheck--;
            }
        }

        return linesCleared;
    }

    public void persistActiveTetronimoColors() {
        if (activeTetronimo != null) {
            for (var square : activeTetronimo.calculateOccupiedSquares()) {
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

        clearActiveTetronimo();
        clearHoldTetronimo();

        this.persistedSquares.forEach(row -> Arrays.fill(row, null));

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
        newScore += conveyor.getEnabledSpecialTetronimoTypes()
                .stream()
                .mapToInt(special -> completedLines * special.getBonusPointsPerLine())
                .sum();

        if (gameMode == GameMode.FREE_PLAY) {
            // increase lines cleared
            totalLinesCleared += completedLines;

            // speed up fall timer
            if (fallTimer.getDelay() > FREE_PLAY_MINIMUM_FALL_TIMER_DELAY) {
                int newDelay = Math.max(difficulty.getInitialTimerDelay() - (totalLinesCleared * 2), FREE_PLAY_MINIMUM_FALL_TIMER_DELAY);
                fallTimer.setDelay(newDelay);
            }
        } else {
            // increase lines cleared
            int maxGameLines = difficulty.getLinesPerLevel() * MAX_LEVEL;
            totalLinesCleared = Math.min(maxGameLines, totalLinesCleared + completedLines);

            // increase level
            int levelsCompleted = totalLinesCleared / difficulty.getLinesPerLevel();
            int newLevel = levelsCompleted + 1;
            int levelIncrease = newLevel - this.level;

            if (gameMode == GameMode.TIME_ATTACK && levelIncrease > 0) {
                newScore += (difficulty.getTimeAttackBonus() * levelIncrease);
            }

            if (newLevel > MAX_LEVEL) {
                newScore += difficulty.getWinBonus();
            }

            if (levelIncrease > 0) {
                setLevel(newLevel);
            }
        }

        setScore(newScore);
    }

    /**
     * Attempts to spawn the given tetronimo object in the board model, replacing the current active tetronimo.
     */
    public void spawn(Tetronimo tetronimo) {
        int startRow = tetronimo.getType().getStartRow();
        int startCol = HORIZONTAL_DIMENSION / 2;

        while (true) {
            var spawnSquares = tetronimo.getType().calculateOccupiedSquares(0, startRow, startCol);

            boolean anyVisible = spawnSquares.stream().anyMatch(square -> square.row() >= LEADING_OVERFLOW_ROWS);
            if (!anyVisible) {
                fallTimer.stop();
                gameTimer.stop();
                publish(TetrisEvent.SPAWN_FAIL, tetronimo);
                return;
            }

            boolean allOpen = spawnSquares.stream().allMatch(square -> isOpenAndInBounds(square.row(), square.column()));
            if (allOpen) {
                tetronimo.setLocation(startRow, startCol);
                this.activeTetronimo = tetronimo;
                return;
            } else {
                startRow--; // Try to push piece upwards past board bounds if we can
            }
        }
    }

    public Collection<ColoredSquare> getColoredSquares() {
        List<ColoredSquare> squares = new ArrayList<>(HORIZONTAL_DIMENSION * VERTICAL_DIMENSION);

        if (activeTetronimo != null) {
            squares.addAll(activeTetronimo.calculateOccupiedSquares());
            if (this.ghostSquaresEnabled) {
                squares.addAll(getGhostSquares());
            }
        }

        for (int rowIndex = 0; rowIndex < VERTICAL_DIMENSION; rowIndex++) {
            Color[] rowColors = persistedSquares.get(rowIndex);
            for (int columnIndex = 0; columnIndex < rowColors.length; columnIndex++) {
                if (rowColors[columnIndex] != null) {
                    squares.add(new ColoredSquare(rowColors[columnIndex], rowIndex, columnIndex));
                }
            }
        }

        return squares;
    }

}
