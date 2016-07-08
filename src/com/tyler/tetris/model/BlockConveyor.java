package com.tyler.tetris.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Manages generating new blocks and maintaining the queue of upcoming blocks
 */
public final class BlockConveyor implements Supplier<Block> {
	
	private List<BlockType> activeTypes;
	private Queue<Block> pieceConveyor;
	
	public BlockConveyor() {
		activeTypes = new ArrayList<>(BlockType.getDefaultBlocks());
		refreshConveyor();
	}
	
	@Override
	public Block get() {
		return shift();
	}
	
	/**
	 *  Pops the first piece off the conveyor belt and adds a new one to replace it
	 */
	public Block shift() {
		Block pieceToReturn = pieceConveyor.poll();
		Block newTail = generatePiece();
		pieceConveyor.offer(newTail);
		return pieceToReturn;
	}
	
	public Block peek() {
		return pieceConveyor.peek();		
	}
	
	public boolean addActivePiece(BlockType pieceType) {
		return activeTypes.add(pieceType);
	}
	
	public boolean removeActivePiece(BlockType pieceType) {
		return activeTypes.remove(pieceType);
	}
	
	public boolean isActive(BlockType pieceType) {
		return activeTypes.contains(pieceType);
	}
	
	/**
	 *  Resets the piece conveyor with 2 random pieces
	 */
	public void refreshConveyor() {
		pieceConveyor = IntStream.rangeClosed(1, 2)
		                         .mapToObj(i -> generatePiece())
		                         .collect(Collectors.toCollection(LinkedList::new));
	}
	
	private Block generatePiece() {
		BlockType pieceType = activeTypes.get(randInRange(0, activeTypes.size() -1));
		return new Block(pieceType);
	}
	
	private static int randInRange(int min, int max) {
		return (int)(Math.random() * (max - min + 1)) + min;
	}

	public static Color getRandomColor() {
		return Color.RED; // TODO
	}
	
}
