package com.tyler.tetris;

import static java.util.stream.Collectors.toCollection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import com.tyler.tetris.Block.ColoredSquare;

public class TetrisBoard extends EventSource {

	public static final int DEFAULT_V_CELLS = 23;
	public static final int DEFAULT_H_CELLS = 10;

	private Block activeBlock;
	private LinkedList<Color[]> persistedBlocks; // Persisted colors for previous blocks; doesn't include active block squares
	private int verticalDimension;
	private int horizontalDimension;
	private boolean ghostSquaresEnabled;
	private Block holdBlock;

	public TetrisBoard() {
		this(DEFAULT_V_CELLS, DEFAULT_H_CELLS);
	}

	public TetrisBoard(int verticalDimension, int horizontalDimension) {
		this.verticalDimension = verticalDimension;
		this.horizontalDimension = horizontalDimension;
		this.ghostSquaresEnabled = true;
		this.persistedBlocks = IntStream.range(0, verticalDimension)
		                                .mapToObj(i -> new Color[horizontalDimension])
		                                .collect(toCollection(LinkedList::new));
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
		this.holdBlock = null;
	}
	
	public void clearSquare(int row, int col) {
		setColor(row, col, null);
	}
	
	public void setColor(int row, int col, Color c) {
		persistedBlocks.get(row)[col] = c;
	}
	
	public void clear() {
		this.activeBlock = null;
		this.holdBlock = null;
		for (Color[] row : persistedBlocks) {
			Arrays.fill(row, null);
		}
	}
	
	public boolean isOpen(int row, int col) {
		return getColor(row, col) == null;
	}
	
	public Color getColor(int row, int col) {
		return persistedBlocks.get(row)[col];
	}
	
	public void setGhostSquaresEnabled(boolean ghostSquaresEnabled) {
		this.ghostSquaresEnabled = ghostSquaresEnabled;
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

	public List<Block.ColoredSquare> getGhostSquares() {
		if (activeBlock == null) {
			return new ArrayList<>();
		}
		int currentRow = activeBlock.getRow();
		int currentCol = activeBlock.getColumn();
		dropCurrentBlock();
		List<Block.ColoredSquare> ghostSquares = activeBlock.getOccupiedSquares();
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
		
		publish("blockPlaced", activeBlock);
		if (linesCleared > 0) {
			publish("linesCleared", linesCleared);
		}
	}
	
	/**
	 * Logs the squares for the active block to this game grid
	 */
	public void logActiveBlock() {
		activeBlock.getOccupiedSquares().forEach(sq -> {
			setColor(sq.getRow(), sq.getColumn(), sq.getColor());
		});
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
		return getColoredSquares(true);
	}
	
	public Collection<ColoredSquare> getColoredSquares(boolean includeGhosts) {
		
		Set<ColoredSquare> squares = new HashSet<>();

		// Important we add occupied squares before ghost squares so that ghost squares don't overwrite occupied squares
		if (activeBlock != null) {
			squares.addAll(activeBlock.getOccupiedSquares());
			if (includeGhosts && this.ghostSquaresEnabled) {
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
