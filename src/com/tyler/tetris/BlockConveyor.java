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
	
	private List<BlockType> typeSampleList;
	private Queue<Block> conveyor;
	
	public BlockConveyor() {
		
		typeSampleList = new ArrayList<>();
		conveyor = new LinkedList<>();
		
		BlockType.getDefaultBlocks().forEach(this::enableBlockType);
		
	}
	
	public Block next() {
		conveyor.offer(generateBlock());
		return conveyor.poll();
	}
	
	public Block peek() {
		return conveyor.peek();
	}
	
	public void enableBlockType(BlockType blockType) {
		IntStream.range(0, blockType.getSpawnRate()).forEach(i -> typeSampleList.add(blockType));
	}
	
	public void disableBlockType(BlockType toRemove) {
		typeSampleList.removeIf(type -> type == toRemove);
	}
	
	public boolean isActive(BlockType pieceType) {
		return typeSampleList.contains(pieceType);
	}
	
	public void refresh() {
		refresh(DEFAULT_INITIAL_BLOCKS);
	}
	
	public void refresh(int initialBlocks) {
		conveyor.clear();
		IntStream.range(0, initialBlocks).forEach(i -> conveyor.add(generateBlock()));
	}
	
	private Block generateBlock() {
		return new Block(Utility.sample(typeSampleList));
	}
	
}
