package com.github.tylersharpe.tetris;

import java.util.*;

public final class BlockConveyor {

  private final Set<BlockType> enabledBlockTypes = EnumSet.noneOf(BlockType.class);
  private final List<BlockType> blockTypeSampleList = new ArrayList<>();
  private final Queue<Block> conveyor = new ArrayDeque<>();
  private final Set<BlockType> enabledSpecialBlockTypes = EnumSet.noneOf(BlockType.class);

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

  void setDifficulty(Difficulty difficulty) {
    blockTypeSampleList.clear();

    for (BlockType defaultBlockType : BlockType.getDefaultBlockTypes()) {
      enableBlockType(difficulty, defaultBlockType);
    }
  }

  public void enableBlockType(Difficulty difficulty, BlockType blockType) {
    if (blockType.isSpecial()) {
      enabledSpecialBlockTypes.add(blockType);
    }
    enabledBlockTypes.add(blockType);
    blockTypeSampleList.removeIf(sampleType -> sampleType == blockType);

    int spawnRate = difficulty.getSpawnRate(blockType);
    for (int i = 1; i <= spawnRate; i++) {
      blockTypeSampleList.add(blockType);
    }
  }

  public void disableBlockType(BlockType typeToDisable) {
    enabledSpecialBlockTypes.remove(typeToDisable);
    enabledBlockTypes.remove(typeToDisable);
    blockTypeSampleList.removeIf(type -> type == typeToDisable);
  }

  Set<BlockType> getEnabledSpecialBlockTypes() {
    return enabledSpecialBlockTypes;
  }

  public boolean isEnabled(BlockType type) {
    return enabledBlockTypes.contains(type);
  }

  private Block generateBlock() {
    return new Block(Utility.sample(blockTypeSampleList));
  }

}

