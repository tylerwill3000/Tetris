package com.github.tylersharpe.tetris.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Allows game model components to communicate with UI components in a decoupled manner
 */
public class TetrisPubSub {

  private Map<TetrisEvent, Collection<Consumer<Object>>> listeners = new EnumMap<>(TetrisEvent.class);

  public void publish(TetrisEvent event) {
    publish(event, null);
  }

  public void publish(TetrisEvent event, Object eventData) {
    listeners.computeIfAbsent(event, __ -> new ArrayList<>()).forEach(listener -> listener.accept(eventData));
  }

  public void subscribe(TetrisEvent event, Consumer<Object> listener) {
    listeners.computeIfAbsent(event, __ -> new ArrayList<>()).add(listener);
  }

}
