package com.github.tylerwill.tetris.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventSource {

  private Map<Object, Collection<Consumer<Object>>> event_listeners = new HashMap<>();

  public <E extends Enum> void publish(E event) {
    publish(event, null);
  }

  public <E extends Enum> void publish(E event, Object eventData) {
    event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).forEach(listener -> listener.accept(eventData));
  }

  public <E extends Enum> void subscribe(E[] events, Consumer<Object> listener) {
    for (E event : events) {
      subscribe(event, listener);
    }
  }

  public <E extends Enum> void subscribe(E event, Consumer<Object> listener) {
    event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
  }

}
