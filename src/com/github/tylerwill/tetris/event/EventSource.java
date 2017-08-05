package com.github.tylerwill.tetris.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventSource {

  private Map<Object, List<Consumer<Object>>> event_listeners = new HashMap<>();

  public <E extends Enum> void publish(E event, Object eventData) {
    _publish(event, eventData);
  }

  protected void publish(String eventStr, Object eventData) {
    for (String event : eventStr.split(" ")) {
      _publish(event, eventData);
    }
  }

  private void _publish(Object event, Object eventData) {
    event_listeners.computeIfAbsent(event, e -> new ArrayList<>())
                   .forEach(listener -> listener.accept(eventData));
  }

  public <E extends Enum> void subscribe(E[] events, Consumer<Object> listener) {
    for (E event : events) {
      subscribe(event, listener);
    }
  }

  public <E extends Enum> void subscribe(E event, Consumer<Object> listener) {
    _subscribe(event, listener);
  }

  public void subscribe(String eventStr, Consumer<Object> listener) {
    subscribe(eventStr.split(" "), listener);
  }

  public void subscribe(String[] events, Consumer<Object> listener) {
    for (String event : events) {
      _subscribe(event, listener);
    }
  }

  private void _subscribe(Object event, Consumer<Object> listener) {
    event_listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
  }

}
