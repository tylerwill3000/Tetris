package com.github.tylerwill.tetris;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Manages generating new blocks and maintaining the queue of upcoming blocks
 */
public final class BlockConveyor {

  private static final int DEFAULT_INITIAL_BLOCKS = 2;

  private Set<BlockType> enabledTypes;
  private List<BlockType> typeSampleList;
  private Queue<Block> conveyor;

  public BlockConveyor() {
    typeSampleList = new ArrayList<>();
    conveyor = new LinkedList<>();
    enabledTypes = new HashSet<>();
  }

  public Block next() {
    conveyor.offer(generateBlock());
    return conveyor.poll();
  }

  public Block peek() {
    return conveyor.peek();
  }

  public void refresh() {
    refresh(DEFAULT_INITIAL_BLOCKS);
  }

  public void refresh(int initialBlocks) {
    conveyor.clear();
    IntStream.range(0, initialBlocks).forEach(i -> conveyor.add(generateBlock()));
  }

  public void setDifficulty(Difficulty difficulty) {
    typeSampleList.clear();
    BlockType.getDefaultBlocks().forEach(type -> enableBlockType(difficulty, type));
  }

  public void enableBlockType(Difficulty diff, BlockType blockType) {
    enabledTypes.add(blockType);
    int spawnRate = diff.getSpawnRate(blockType);
    IntStream.range(0, spawnRate).forEach(i -> typeSampleList.add(blockType));
  }

  public void disableBlockType(BlockType toRemove) {
    enabledTypes.remove(toRemove);
    typeSampleList.removeIf(type -> type == toRemove);
  }

  public boolean isEnabled(BlockType blockType) {
    return enabledTypes.contains(blockType);
  }

  private Block generateBlock() {
    return new Block(Utility.sample(typeSampleList));
  }

}

