package com.tyler.tetris;

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
		conveyor = new LinkedList<>();
	}
	
	public Block next() {
		conveyor.offer(generateBlock());
		return conveyor.poll();
	}
	
	public Block peek() {
		return conveyor.peek();
	}
	
	public boolean enableBlockType(BlockType blockType) {
		return activeTypes.add(blockType);
	}
	
	public boolean disableBlockType(BlockType blockType) {
		return activeTypes.remove(blockType);
	}
	
	public boolean isActive(BlockType pieceType) {
		return activeTypes.contains(pieceType);
	}
	
	public void refresh() {
		refresh(DEFAULT_INITIAL_BLOCKS);
	}
	
	public void refresh(int initialBlocks) {
		conveyor.clear();
		IntStream.range(0, initialBlocks).forEach(i -> conveyor.add(generateBlock()));
	}
	
	private Block generateBlock() {
		BlockType blockType = activeTypes.get(Utility.randInRange(0, activeTypes.size() -1));
		return new Block(blockType);
	}
	
}
