package com.github.tylersharpe.tetris.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class Broker {
    private final Map<TetrisEvent, Collection<Consumer<Object>>> subscribers;

    public Broker() {
        subscribers = new EnumMap<>(TetrisEvent.class);
        for (var event : TetrisEvent.values()) {
            subscribers.put(event, new ArrayList<>());
        }
    }

    public void publish(TetrisEvent event) {
        publish(event, null);
    }

    public void publish(TetrisEvent event, Object eventData) {
        subscribers.get(event).forEach(listener -> listener.accept(eventData));
    }

    public void subscribe(TetrisEvent event, Consumer<Object> listener) {
        subscribers.get(event).add(listener);
    }
}
