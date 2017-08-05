package com.github.tylerwill.tetris;

import java.util.*;

import static com.github.tylerwill.tetris.Utility.nTimes;

/** Manages generating new blocks and maintaining the queue of upcoming blocks */
public final class BlockConveyor {

  private static final int DEFAULT_INITIAL_BLOCKS = 2;

  private Set<BlockType> enabledTypes;
  private List<BlockType> typeSampleList;
  private Queue<Block> conveyor;

  BlockConveyor() {
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

  void refresh() {
    refresh(DEFAULT_INITIAL_BLOCKS);
  }

  private void refresh(int initialBlocks) {
    conveyor.clear();
    nTimes(initialBlocks, i -> conveyor.add(generateBlock()));
  }

  void setDifficulty(Difficulty difficulty) {
    typeSampleList.clear();
    BlockType.getDefaultBlocks().forEach(type -> enableBlockType(difficulty, type));
  }

  public void enableBlockType(Difficulty diff, BlockType blockType) {
    enabledTypes.add(blockType);
    int spawnRate = diff.getSpawnRate(blockType);
    nTimes(spawnRate, i -> typeSampleList.add(blockType));
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

