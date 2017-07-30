package com.github.tylerwill.tetris.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventSource {

  private Map<String, List<Consumer<Object>>> event_listeners = new HashMap<>();

  protected void publish(String eventStr, Object eventData) {
    for (String event : eventStr.split(" ")) {
      event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).forEach(l -> l.accept(eventData));
    }
  }

  public void subscribe(String eventStr, Consumer<Object> listener) {
    subscribe(eventStr.split(" "), listener);
  }

  public void subscribe(String[] events, Consumer<Object> listener) {
    for (String event : events) {
      event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
    }
  }

}
