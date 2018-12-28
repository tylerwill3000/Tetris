package com.github.tylersharpe.tetris.event;

import java.util.*;
import java.util.function.Consumer;

/**
 * Allows game model components to communicate with UI components in a decoupled manner
 */
public class TetrisEventBus {

  private Map<TetrisEvent, Collection<Consumer<Object>>> event_listeners = new EnumMap<>(TetrisEvent.class);

  public void publish(TetrisEvent event) {
    publish(event, null);
  }

  public void publish(TetrisEvent event, Object eventData) {
    event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).forEach(listener -> listener.accept(eventData));
  }

  public void subscribe(TetrisEvent[] events, Consumer<Object> listener) {
    for (var event : events) {
      subscribe(event, listener);
    }
  }

  public void subscribe(TetrisEvent event, Consumer<Object> listener) {
    event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
  }

}
