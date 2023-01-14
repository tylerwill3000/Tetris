package com.github.tylersharpe.tetris.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dead simple synchronous, in-process message broker
 */
public class Broker {

    private final Map<TetrisEvent, Collection<Consumer<Object>>> subscribers = new EnumMap<>(TetrisEvent.class);

    public void publish(TetrisEvent event) {
        publish(event, null);
    }

    public void publish(TetrisEvent event, Object eventData) {
        subscribers.computeIfAbsent(event, __ -> new ArrayList<>()).forEach(listener -> listener.accept(eventData));
    }

    public void subscribe(TetrisEvent event, Consumer<Object> listener) {
        subscribers.computeIfAbsent(event, __ -> new ArrayList<>()).add(listener);
    }

}
