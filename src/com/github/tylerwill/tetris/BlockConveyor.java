package com.github.tylerwill.tetris;

import java.util.*;
import java.util.stream.Collectors;

/** Manages generating new blocks and maintaining the queue of upcoming blocks */
public final class BlockConveyor {

  private Set<BlockType> enabledTypes;
  private List<BlockType> typeSampleList;
  private Queue<Block> conveyor;
  private Set<BlockType> activeSpecialTypes; // Cached for performance when calculating score

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

  void prepareForStart() {
    conveyor.clear();
    conveyor.add(generateBlock());
    conveyor.add(generateBlock());
  }

  void applySpawnRates(Difficulty difficulty) {
    typeSampleList.clear();
    for (BlockType defaultType : BlockType.getDefaultBlocks()) {
      enableBlockType(difficulty, defaultType);
    }
  }

  public void enableBlockType(Difficulty diff, BlockType blockType) {
    activeSpecialTypes = null;
    enabledTypes.add(blockType);
    typeSampleList.removeIf(type -> type == blockType);
    int spawnRate = diff.getSpawnRate(blockType);
    for (int i = 1; i <= spawnRate; i++) {
      typeSampleList.add(blockType);
    }
  }

  public void disableBlockType(BlockType toRemove) {
    activeSpecialTypes = null;
    enabledTypes.remove(toRemove);
    typeSampleList.removeIf(type -> type == toRemove);
  }

  public Set<BlockType> getEnabledSpecials() {
    if (activeSpecialTypes == null) {
      activeSpecialTypes = BlockType.getSpecialBlocks().stream().filter(this::isEnabled).collect(Collectors.toSet());
    }
    return activeSpecialTypes;
  }

  public boolean isEnabled(BlockType blockType) {
    return enabledTypes.contains(blockType);
  }

  private Block generateBlock() {
    return new Block(Utility.sample(typeSampleList));
  }

}

