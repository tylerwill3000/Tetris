package com.github.tylersharpe.tetris;

import java.util.*;

public final class BlockConveyor {

  private final Set<BlockType> enabledTypes = EnumSet.noneOf(BlockType.class);
  private final List<BlockType> typeSampleList = new ArrayList<>();
  private final Queue<Block> conveyor = new ArrayDeque<>();
  private final Set<BlockType> enabledSpecialTypes = EnumSet.noneOf(BlockType.class);

  public Block next() {
    conveyor.offer(generateBlock());
    return conveyor.poll();
  }

  public Block peek() {
    return conveyor.peek();
  }

  void reset() {
    conveyor.clear();
    conveyor.add(generateBlock());
    conveyor.add(generateBlock());
  }

  void applySpawnRates(Difficulty difficulty) {
    typeSampleList.clear();

    for (BlockType defaultType : BlockType.getDefaultBlocks()) {
      enableBlock(difficulty, defaultType);
    }
  }

  public void enableBlock(Difficulty difficulty, BlockType blockType) {
    if (blockType.isSpecial()) {
      enabledSpecialTypes.add(blockType);
    }
    enabledTypes.add(blockType);
    typeSampleList.removeIf(sampleType -> sampleType == blockType);

    int spawnRate = difficulty.getSpawnRate(blockType);
    for (int i = 1; i <= spawnRate; i++) {
      typeSampleList.add(blockType);
    }
  }

  public void disableBlock(BlockType toRemove) {
    enabledSpecialTypes.remove(toRemove);
    enabledTypes.remove(toRemove);
    typeSampleList.removeIf(type -> type == toRemove);
  }

  Set<BlockType> getEnabledSpecialTypes() {
    return enabledSpecialTypes;
  }

  public boolean isEnabled(BlockType type) {
    return enabledTypes.contains(type);
  }

  private Block generateBlock() {
    return new Block(Utility.sample(typeSampleList));
  }

}

