package com.github.tylersharpe.tetris;

import java.util.*;

import static java.util.stream.Collectors.toList;

public final class BlockConveyor {

  private final Set<BlockType> enabledTypes;
  private final List<BlockType> typeSampleList;
  private final Queue<Block> conveyor;
  private Set<BlockType> activeSpecialTypes; // Cached for performance when calculating score

  BlockConveyor() {
    typeSampleList = new ArrayList<>();
    conveyor = new ArrayDeque<>();
    enabledTypes = EnumSet.noneOf(BlockType.class);
  }

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
    activeSpecialTypes = null;
    enabledTypes.add(blockType);
    typeSampleList.removeIf(sampleType -> sampleType == blockType);

    int spawnRate = difficulty.getSpawnRate(blockType);
    for (int i = 1; i <= spawnRate; i++) {
      typeSampleList.add(blockType);
    }
  }

  public void disableBlock(BlockType toRemove) {
    activeSpecialTypes = null;
    enabledTypes.remove(toRemove);
    typeSampleList.removeIf(type -> type == toRemove);
  }

  Set<BlockType> getEnabledSpecials() {
    if (activeSpecialTypes == null) {
      var activeSpecialsList = BlockType.getSpecialBlocks().stream().filter(this::isEnabled).collect(toList());
      activeSpecialTypes = activeSpecialsList.isEmpty() ? EnumSet.noneOf(BlockType.class) : EnumSet.copyOf(activeSpecialsList);
    }
    return activeSpecialTypes;
  }

  public boolean isEnabled(BlockType type) {
    return enabledTypes.contains(type);
  }

  private Block generateBlock() {
    return new Block(Utility.sample(typeSampleList));
  }

}

