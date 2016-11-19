package com.tyler.tetris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventSource {

	protected Map<String, List<Consumer<Object>>> eventKey_listeners = new HashMap<>();
	
	protected void publish(String event, Object eventData) {
		eventKey_listeners.computeIfAbsent(event, e -> new ArrayList<>()).forEach(l -> l.accept(eventData));
	}
	
	public void subscribe(String event, Consumer<Object> listener) {
		eventKey_listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
	}
	
}
