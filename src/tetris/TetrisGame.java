package tetris;

import static java.util.stream.Collectors.toCollection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.Timer;

import tetris.Block.ColoredSquare;

public class TetrisGame extends EventSource {

	public static final int MAX_LEVEL = 10;
	public static final int DEFAULT_V_CELLS = 23;
	public static final int DEFAULT_H_CELLS = 10;
	
	private static final int[] LINE_POINTS_MAP = { 10, 15, 20, 30 };
	private static final Map<BlockType, Integer> SPECIAL_PIECE_BONUSES;
	static {
		Map<BlockType, Integer> temp = new HashMap<>();
		temp.put(BlockType.TWIN_PILLARS, 4);
		temp.put(BlockType.ROCKET, 6);
		temp.put(BlockType.DIAMOND, 10);
		SPECIAL_PIECE_BONUSES = Collections.unmodifiableMap(temp);
	}
	
	private Block activeBlock;
	private Block holdBlock;
	private BlockConveyor conveyor;
	private Collection<BlockType> activeTypes;
	private LinkedList<Color[]> persistedBlocks; // Persisted colors for previous blocks; doesn't include active block squares
	private Difficulty difficulty;
	private int totalLinesCleared;
	private int score;
	private int level;
	private int verticalDimension;
	private int gameTime;
	private int horizontalDimension;
	private boolean ghostSquaresEnabled;
	private boolean timeAttack;
	private Timer fallTimer = new Timer(0, e -> tryFall());
	private Timer gameTimer = new Timer(1000, e -> {
		setGameTime(gameTime + 1);
		if (timeAttack && gameTime >= getCurrentTimeAttackLimit()) {
			publish("timeAttackFail", null);
			((Timer) e.getSource()).stop();
		}
	});
	
	public TetrisGame() {
		this(DEFAULT_V_CELLS, DEFAULT_H_CELLS);
	}

	public TetrisGame(int verticalDimension, int horizontalDimension) {
		this.verticalDimension = verticalDimension;
		this.horizontalDimension = horizontalDimension;
		this.ghostSquaresEnabled = true;
		this.conveyor = new BlockConveyor();
		this.persistedBlocks = IntStream.range(0, verticalDimension)
		                                .mapToObj(i -> new Color[horizontalDimension])
		                                .collect(toCollection(LinkedList::new));
		setDifficulty(Difficulty.EASY);
		setLevel(1);
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
		this.holdBlock = Objects.requireNonNull(block, "Hold block may not be null");
	}
	
	public void setGhostSquaresEnabled(boolean ghostSquaresEnabled) {
		this.ghostSquaresEnabled = ghostSquaresEnabled;
	}
	
	public void clearHoldBlock() {
		this.holdBlock = null;
	}
	
	public Timer getFallTimer() {
		return fallTimer;
	};
	
	public Timer getGameTimer() {
		return gameTimer;
	}

	public int getScore() {
		return score;
	}
	
	private void setScore(int newScore) {
		this.score = newScore;
		publish("scoreChanged", score);
	}
	
	public int getGameTime() {
		return gameTime;
	}
	
	private void setGameTime(int time) {
		gameTime = time;
		publish("gameTimeChanged", gameTime);
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
		this.fallTimer.setDelay(difficulty.initialTimerDelay);
	}

	public boolean isTimeAttack() {
		return this.timeAttack;
	}
	
	public void setTimeAttack(boolean timeAttack) {
		this.timeAttack = timeAttack;
	}
	
	public int getLevel() {
		return level;
	}
	
	private void setLevel(int newLevel) {
		this.level = Math.min(newLevel, MAX_LEVEL);
		if (newLevel == MAX_LEVEL) {
			fallTimer.stop();
			gameTimer.stop();
			publish("gameWon", level);
		}
		else {
			int initialDelay = difficulty.getInitialTimerDelay();
			int totalSpeedup = (level - 1) * difficulty.getTimerSpeedup();
			int newDelay = initialDelay - totalSpeedup;
			fallTimer.setDelay(newDelay);
			publish("levelChanged", this.level);
		}
	}
	
	public void clearSquare(int row, int col) {
		setColor(row, col, null);
	}
		
	public boolean isOpen(int row, int col) {
		return getColor(row, col) == null;
	}
	
	public Color getColor(int row, int col) {
		return persistedBlocks.get(row)[col];
	}
	
	public void setColor(int row, int col, Color c) {
		persistedBlocks.get(row)[col] = c;
	}
	
	public int getTotalLinesCleared() {
		return totalLinesCleared;
	}
	
	public int getCurrentLevelLinesNeeded() {
		return level * difficulty.getLinesPerLevel();
	}
	
	public int getCurrentTimeAttackLimit() {
		return getCurrentLevelLinesNeeded() * difficulty.getTimeAttackSecondsPerLine();
	}
	
	public int getCurrentLevelLinesCleared() {
		int lastLevelThreshold = difficulty.getLinesPerLevel() * (level - 1);
		return totalLinesCleared - lastLevelThreshold;
	}
	
	public static int getSpecialPieceBonusPoints(BlockType pieceType) {
		return SPECIAL_PIECE_BONUSES.get(pieceType);
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

	public void ssActiveBlockLeft() {
		while (moveActiveBlockLeft()) {
			// Move method returns boolean to control loop
		}
	}

	public void ssActiveBlockRight() {
		while (moveActiveBlockRight()) {
			// Move method returns boolean to control loop
		}
	}

	public boolean rotateActiveBlockCW() {
		return rotateActiveBlock(1);
	}

	public boolean rotateActiveBlockCCW() {
		return rotateActiveBlock(-1);
	}

	/**
	 * Returns true if piece could successfully move, false if there was not enough room
	 */
	private boolean moveActiveBlock(int rowMove, int colMove) {

		boolean canMoveBeMade = activeBlock.getOccupiedSquares()
		                                   .stream()
		                                   .map(sq -> new ColoredSquare(sq.getRow() + rowMove, sq.getColumn() + colMove))
		                                   .allMatch(moveSq -> isInBounds(moveSq.getRow(), moveSq.getColumn()) && isOpen(moveSq.getRow(), moveSq.getColumn()));
		if (canMoveBeMade) {
			activeBlock.move(rowMove, colMove);
			publish("blockMoved", activeBlock);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean rotateActiveBlock(int dir) {
		
		if (dir != 1 && dir != -1) {
			throw new IllegalArgumentException("Rotation argument must be either 1 or -1");
		}
		
		activeBlock.rotate(dir);

		boolean areRotatedSquaresLegal =
				activeBlock.getOccupiedSquares()
		                   .stream()
		                   .allMatch(moveSq -> moveSq.getColumn() >= 0 && moveSq.getColumn() < horizontalDimension && moveSq.getRow() < verticalDimension);
		
		if (areRotatedSquaresLegal) {
			publish("blockMoved", activeBlock);
			return true;
		} else {
			activeBlock.rotate(dir * -1);
			return false;
		}
	}

	public List<ColoredSquare> getGhostSquares() {
		if (activeBlock == null) {
			return new ArrayList<>();
		}
		int currentRow = activeBlock.getRow();
		int currentCol = activeBlock.getColumn();
		dropCurrentBlock();
		List<ColoredSquare> ghostSquares = activeBlock.getOccupiedSquares();
		ghostSquares.forEach(gs -> gs.setColor(null));
		activeBlock.setLocation(currentRow, currentCol);
		return ghostSquares;
	}

	/**
	 * Attempts to vertically drop the current active piece 1 square.
	 * If the piece could not be dropped, its colors are permanately logged to the color grid and any complete rows removed
	 */
	public void tryFall() {
		
		if (moveActiveBlockDown()) {
			return;
		};
		
		logActiveBlock();
		
		int completeRowScanIndex = Math.min(activeBlock.getRow(), verticalDimension - 1);
		int minRowScanIndex = Math.max(0, completeRowScanIndex - 3);
		int linesCleared = 0;
		while (completeRowScanIndex >= minRowScanIndex && linesCleared <= 4) {
			
			Color[] rowToScan = persistedBlocks.get(completeRowScanIndex);
			boolean isRowComplete = Arrays.stream(rowToScan).allMatch(color -> color != null);
			if (isRowComplete) {
				persistedBlocks.remove(completeRowScanIndex);
				persistedBlocks.offerFirst(new Color[horizontalDimension]);
				linesCleared++;
			}
			else {
				completeRowScanIndex--;
			}
		}
		
		if (linesCleared > 0) {
			increaseScore(linesCleared);
			publish("linesCleared", linesCleared);
		}
		
		if (level < MAX_LEVEL) {
			spawn(conveyor.next());
		}
	}
	
	public void logActiveBlock() {
		if (activeBlock != null) {
			activeBlock.getOccupiedSquares().forEach(sq -> {
				setColor(sq.getRow(), sq.getColumn(), sq.getColor());
			});
		}
	}
	
	public void beginNew() {
		
		setGameTime(0);
		setScore(0);
		setLevel(1);
		
		// The reason we use setters for the score info but not these are because score info changes publish events, these don't
		this.totalLinesCleared = 0;
		this.activeBlock = null;
		this.holdBlock = null;
		
		this.persistedBlocks.stream().forEach(row -> Arrays.fill(row, null));
		
		this.conveyor.refresh();
		spawn(this.conveyor.next());
		
		this.gameTimer.start();
		this.fallTimer.start();
	}
	
	private void increaseScore(int completedLines) {
		
		totalLinesCleared += completedLines;

		int newScore = this.score;
		
		// Points from raw lines cleared
		int linePoints = completedLines * LINE_POINTS_MAP[completedLines - 1];
		int difficultyBonus = completedLines * difficulty.getLinesClearedBonus();
		newScore += (linePoints + difficultyBonus);
		
		 // Bonuses for special blocks
		if (activeTypes != null) {
			newScore += BlockType.getSpecialBlocks()
			                     .stream()
			                     .filter(activeTypes::contains)
			                     .mapToInt(special -> completedLines * SPECIAL_PIECE_BONUSES.get(special))
			                     .sum();
		}
		
		// Level ups
		int newLevel = this.level;
		while (totalLinesCleared >= (newLevel * difficulty.getLinesPerLevel())) {
			
			newLevel++;
			
			if (timeAttack) {
				newScore += difficulty.getTimeAttackBonus();
			}
			
			if (newLevel == MAX_LEVEL) {
				newScore += difficulty.getWinBonus();
				break;
			}
		}

		if (newLevel > this.level) {
			setLevel(newLevel);
		}
		
		setScore(newScore);
	}
	
	/**
	 * Attempts to spawn the given block object in the board model, replacing
	 * the current active piece. Returns true if there was room to spawn the piece, false otherwise
	 */
	public boolean spawn(Block block) {

		int startRow = block.getType().getStartRow();
		int startCol = horizontalDimension / 2;

		while (true) {

			List<Block.ColoredSquare> occupiedSquares = block.getType().calcOccupiedSquares(0, startRow, startCol);
			boolean anyVisible = occupiedSquares.stream().filter(sq -> sq.getRow() >= 3).count() > 0;
			if (!anyVisible) {
				fallTimer.stop();
				gameTimer.stop();
				publish("spawnFail", block);
				return false;
			}
			
			boolean allOpen = occupiedSquares.stream().allMatch(sq -> isOpen(sq.getRow(), sq.getColumn()));
			if (allOpen) {
				block.setLocation(startRow, startCol);
				this.activeBlock = block;
				return true;
			} else {
				startRow--; // Try to push piece upwards past board bounds if we can
			}
		}
	}

	public Collection<ColoredSquare> getColoredSquares() {
		
		Set<ColoredSquare> squares = new HashSet<>();

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
					squares.add(new ColoredSquare(rowColors[colIndex], rowIndex, colIndex));
				}
			}
		}
		
		return squares;
	}
	
	private boolean isInBounds(int row, int col) {
		return row >= 0 && row < verticalDimension && col >= 0 && col < horizontalDimension;
	}

}
