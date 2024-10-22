package com.github.tylerwilliams.tetris;

import com.github.tylerwilliams.tetris.event.Broker;
import com.github.tylerwilliams.tetris.event.TetrisEvent;

import javax.swing.Timer;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

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
        this.conveyor.applySpawnRates(difficulty);
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

        int initialDelay = difficulty.getInitialTimerDelay();
        int totalSpeedup = (level - 1) * Difficulty.TIMER_SPEEDUP;
        int newDelay = initialDelay - totalSpeedup;
        this.fallTimer.setDelay(newDelay);

        this.currentLevelTime = 0;

        publish(TetrisEvent.LEVEL_CHANGED, newLevel);
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
        boolean canMoveBeMade = tetronimo.getCurrentSquares()
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
        Collection<ColoredSquare> squaresAfterRotation = activeTetronimo.copy().rotate(rotation).getCurrentSquares();

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
        Collection<ColoredSquare> currentActiveTetronimoSquares = activeTetronimoCopy.getCurrentSquares();

        while (moveTetronimo(activeTetronimoCopy, 1, 0)) {
            // drop as far as possible
        }
        Collection<ColoredSquare> ghostSquares = new ArrayList<>(activeTetronimoCopy.getCurrentSquares());

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
            onLinesCleared(linesCleared);
        }

        if (!isGameWon) {
            spawn(conveyor.next());
        }
    }

    private int clearCompleteLines() {
        int linesCleared = 0;

        for (int rowIndex = 0; rowIndex < VERTICAL_DIMENSION && linesCleared < 4; rowIndex++) {
            Color[] colorsForRow = persistedSquares.get(rowIndex);
            boolean isRowComplete = Stream.of(colorsForRow).allMatch(Objects::nonNull);
            if (isRowComplete) {
                persistedSquares.remove(rowIndex);
                persistedSquares.offerFirst(new Color[HORIZONTAL_DIMENSION]);
                linesCleared++;
            }
        }

        return linesCleared;
    }

    public void persistActiveTetronimoColors() {
        if (activeTetronimo != null) {
            for (var square : activeTetronimo.getCurrentSquares()) {
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

    private void onLinesCleared(int completedLines) {
        int newScore = score + switch (completedLines) {
            case 1 -> 10;
            case 2 -> 30;
            case 3 -> 60;
            case 4 -> 100;
            default -> throw new RuntimeException("Completed lines must be in range 1 - 4");
        };

        if (gameMode == GameMode.FREE_PLAY) {
            totalLinesCleared += completedLines;

            if (fallTimer.getDelay() > FREE_PLAY_MINIMUM_FALL_TIMER_DELAY) {
                int newDelay = Math.max(difficulty.getInitialTimerDelay() - (totalLinesCleared * 2), FREE_PLAY_MINIMUM_FALL_TIMER_DELAY);
                fallTimer.setDelay(newDelay);
            }
        } else {
            int maxLinesCleared = difficulty.getLinesPerLevel() * MAX_LEVEL;
            totalLinesCleared = Math.min(maxLinesCleared, totalLinesCleared + completedLines);

            if (totalLinesCleared == maxLinesCleared) {
                isGameWon = true;
                fallTimer.stop();
                gameTimer.stop();
                clearActiveTetronimo(); // Needed so that this tetronimo's squares don't get re-painted during victory clear animation
                publish(TetrisEvent.GAME_WON);
            } else {
                int levelsCompleted = totalLinesCleared / difficulty.getLinesPerLevel();
                int newLevel = levelsCompleted + 1;
                if (newLevel > level) {
                    setLevel(newLevel);
                }
            }
        }

        setScore(newScore);
        publish(TetrisEvent.LINES_CLEARED, completedLines);
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
            squares.addAll(activeTetronimo.getCurrentSquares());
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
