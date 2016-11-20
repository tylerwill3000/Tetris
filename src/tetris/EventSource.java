package tetris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventSource {

	protected Map<String, List<Consumer<Object>>> eventKey_listeners = new HashMap<>();
	
	protected void publish(String eventStr, Object eventData) {
		for (String event : eventStr.split(" ")) {
			eventKey_listeners.computeIfAbsent(event, e -> new ArrayList<>()).forEach(l -> l.accept(eventData));
		}
	}
	
	public void subscribe(String eventStr, Consumer<Object> listener) {
		for (String event : eventStr.split(" ")) {
			eventKey_listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
		}
	}
	
}
