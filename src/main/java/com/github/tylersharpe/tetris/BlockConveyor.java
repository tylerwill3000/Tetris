package com.github.tylersharpe.tetris;

import java.util.*;
import java.util.stream.Collectors;

/** Manages generating new blocks and maintaining the queue of upcoming blocks */
public final class BlockConveyor {

  private Set<Block.Type> enabledTypes;
  private List<Block.Type> typeSampleList;
  private Queue<Block> conveyor;
  private Set<Block.Type> activeSpecialTypes; // Cached for performance when calculating score

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
    for (Block.Type defaultType : Block.Type.getDefaultBlocks()) {
      enableBlock(difficulty, defaultType);
    }
  }

  public void enableBlock(Difficulty diff, Block.Type type) {
    activeSpecialTypes = null;
    enabledTypes.add(type);
    typeSampleList.removeIf(sampleType -> sampleType == type);
    int spawnRate = diff.getSpawnRate(type);
    for (int i = 1; i <= spawnRate; i++) {
      typeSampleList.add(type);
    }
  }

  public void disableBlock(Block.Type toRemove) {
    activeSpecialTypes = null;
    enabledTypes.remove(toRemove);
    typeSampleList.removeIf(type -> type == toRemove);
  }

  Set<Block.Type> getEnabledSpecials() {
    if (activeSpecialTypes == null) {
      activeSpecialTypes = Block.Type.getSpecialBlocks().stream().filter(this::isEnabled).collect(Collectors.toSet());
    }
    return activeSpecialTypes;
  }

  public boolean isEnabled(Block.Type type) {
    return enabledTypes.contains(type);
  }

  private Block generateBlock() {
    return new Block(Utility.sample(typeSampleList));
  }

}

