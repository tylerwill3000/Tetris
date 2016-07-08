package com.tyler.tetris.model;

import static java.util.stream.Collectors.toCollection;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class BlockBoardModel {

	public static final int DEFAULT_V_CELLS = 20;
	public static final int DEFAULT_H_CELLS = 10;

	private Block activeBlock;
	private LinkedList<Color[]> quilt; // Persisted colors for previous blocks; doesn't include active block squares
	private int vCells, hCells;

	public BlockBoardModel() {
		this(DEFAULT_V_CELLS, DEFAULT_H_CELLS);
	}

	public BlockBoardModel(int vCells, int hCells) {
		this.vCells = vCells;
		this.hCells = hCells;
		this.quilt = IntStream.range(0, vCells).mapToObj(i -> new Color[hCells]).collect(toCollection(LinkedList::new));
	}

	public void clearSquare(int row, int col) {
		setSquare(row, col, null);
	}
	
	public void setSquare(int row, int col, Color c) {
		quilt.get(row)[col] = c;
	}
	
	public boolean isOpenSquare(int row, int col) {
		return quilt.get(row)[col] == null;
	}

	public boolean isInBounds(Block.ColoredSquare cd) {
		return isInBounds(cd.getRow(), cd.getCol());
	}

	public boolean isInBounds(int row, int col) {
		return row >= 0 && row < vCells && col >= 0 && col < hCells;
	}

	public int getHorizontalDimension() {
		return hCells;
	}

	public int getVerticalDimension() {
		return vCells;
	}

	public Block getActiveBlock() {
		return activeBlock;
	}

	/**
	 * Returns true if piece could successfully move, false if there was not enough room
	 */
	private boolean moveActiveBlock(int rowMove, int colMove) {

		boolean canMoveBeMade = activeBlock.getOccupiedSquares()
		                                   .stream()
		                                   .map(cd -> new Block.ColoredSquare(cd.getRow() + rowMove, cd.getCol() + colMove))
		                                   .allMatch(moveCd -> isInBounds(moveCd) && isOpenSquare(moveCd.getRow(), moveCd.getCol()));
		if (canMoveBeMade) {
			activeBlock.move(rowMove, colMove);
			return true;
		} else {
			return false;
		}
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

	public void dropCurrentBlock() {
		while (moveActiveBlockDown()) {
			// Move method returns boolean to control loop
		}
	}

	public void ssCurrentBlockLeft() {
		while (moveActiveBlockLeft()) {
			// Move method returns boolean to control loop
		}
	}

	public void ssCurrentBlockRight() {
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
		activeBlock.rotate(dir);

		boolean areRotatedSquaresLegal = activeBlock.getOccupiedSquares()
		                                            .stream()
		                                            .allMatch(moveSq -> isInBounds(moveSq) && isOpenSquare(moveSq.getRow(), moveSq.getCol()));
		if (areRotatedSquaresLegal) {
			return true;
		} else {
			activeBlock.rotate(dir * -1);
			return false;
		}
	}

	public List<Block.ColoredSquare> getGhostSquares() {
		int currentRow = activeBlock.getRow();
		int currentCol = activeBlock.getCol();
		dropCurrentBlock();
		List<Block.ColoredSquare> ghostSquares = activeBlock.getOccupiedSquares();
		activeBlock.setLocation(currentRow, currentCol);
		return ghostSquares;
	}

	/**
	 * Attempts to vertically drop the current active piece 1 square.
	 * If the piece could not be dropped, its colors are permanately logged to the color grid and any complete rows removed
	 * 
	 * @return The number of lines cleared if the piece was placed, else empty
	 */
	public Optional<Integer> tryFall() {
		
		if (moveActiveBlockDown()) {
			return Optional.empty();
		};
		
		// Persist squares for current piece to quilt
		activeBlock.getOccupiedSquares().forEach(sq -> quilt.get(sq.getRow())[sq.getCol()] = sq.getColor());
		
		// Remove each complete row that is within the piece's row range.
		// Tallest piece is line, so we will only have to scan a 3 row range at max
		int completeRowScanIndex = activeBlock.getRow();
		int maxRowScanIndex = Math.min(vCells - 1, completeRowScanIndex + 3);
		
		int linesCleared = 0;
		for (; completeRowScanIndex <= maxRowScanIndex; completeRowScanIndex++) {
			
			Color[] rowToScan = quilt.get(completeRowScanIndex);
			boolean isRowComplete = Arrays.stream(rowToScan).allMatch(color -> color != null);
			if (isRowComplete) {
				quilt.remove(completeRowScanIndex);
				quilt.offerFirst(new Color[hCells]);
				linesCleared++;
			}
		}
		
		return Optional.of(linesCleared);
	}

	/**
	 * Attempts to spawn the given block object in the board model, replacing
	 * the current active piece. Returns true if there was room to spawn the piece, false otherwise
	 */
	public boolean spawn(Block block) {

		BlockType pieceType = block.getType();
		int startRow = pieceType.getStartRow();
		int startCol = hCells / 2;

		while (true) {

			List<Block.ColoredSquare> occupiedSquares = pieceType.calcOccupiedSquares(0, startRow, startCol);
			boolean anyVisible = occupiedSquares.stream().filter(sq -> sq.getRow() >= 0).count() > 0;
			boolean allOpen = occupiedSquares.stream().allMatch(sq -> quilt.get(sq.getRow())[sq.getCol()] == null);

			if (!anyVisible) {
				return false;
			}

			if (allOpen) {
				block.setLocation(startRow, startCol);
				this.activeBlock = block;
				return true;
			} else {
				startRow--; // Try to push piece upwards past board bounds if we can
			}
		}
	}

}
