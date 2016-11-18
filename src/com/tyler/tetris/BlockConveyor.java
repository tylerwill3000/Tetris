package com.tyler.tetris;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

/**
 * Manages generating new blocks and maintaining the queue of upcoming blocks
 */
public final class BlockConveyor {
	
	private static final int DEFAULT_INITIAL_BLOCKS = 2;
	
	private List<BlockType> activeTypes;
	private Queue<Block> conveyor;
	
	public BlockConveyor() {
		activeTypes = new ArrayList<>(BlockType.getDefaultBlocks());
		refreshConveyor();
	}
	
	public Block next() {
		conveyor.offer(generateBlock());
		return conveyor.poll();
	}
	
	public Block peek() {
		return conveyor.peek();
	}
	
	public boolean enableBlockType(BlockType pieceType) {
		return activeTypes.add(pieceType);
	}
	
	public boolean disableBlockType(BlockType pieceType) {
		return activeTypes.remove(pieceType);
	}
	
	public boolean isActive(BlockType pieceType) {
		return activeTypes.contains(pieceType);
	}
	
	public void refreshConveyor() {
		refreshConveyor(DEFAULT_INITIAL_BLOCKS);
	}
	
	public void refreshConveyor(int initialBlocks) {
		conveyor = IntStream.rangeClosed(1, initialBlocks)
		                    .mapToObj(i -> generateBlock())
		                    .collect(toCollection(LinkedList::new));
	}
	
	private Block generateBlock() {
		BlockType pieceType = activeTypes.get(randInRange(0, activeTypes.size() -1));
		return new Block(pieceType);
	}
	
	private static int randInRange(int min, int max) {
		return (int)(Math.random() * (max - min + 1)) + min;
	}

}
