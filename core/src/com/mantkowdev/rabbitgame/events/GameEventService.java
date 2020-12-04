package com.mantkowdev.rabbitgame.events;

import com.mantkowdev.rabbitgame.api.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public class GameEventService {
    private Map<Class<? extends GameEvent>, Map<String, List<GameEvent>>> queue = new HashMap<>();

    public <T extends GameEvent> void pushEvent(T gameEvent) {
        queue
                .computeIfAbsent(gameEvent.getClass(), event -> new HashMap<>())
                .computeIfAbsent(gameEvent.getTopic(), event -> new ArrayList<>())
                .add(gameEvent);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameEvent> Optional<T> getEvent(Class<T> type, String topic) {
        if (queue.containsKey(type) && queue.get(type).containsKey(topic)) {
            return (Optional<T>) queue.get(type).get(topic).stream().findFirst();
        } else {
            return empty();
        }
    }

    /**
     * this method should be called at the end of main loop of the application to provide housekeeping
     */
    public void update() {
        for (Class<? extends GameEvent> eventType : queue.keySet()) {
            for (String eventTopic : queue.get(eventType).keySet()) {
                updateEvents(eventType, eventTopic);
            }
        }
    }

    private void updateEvents(Class<? extends GameEvent> eventType, String eventTopic) {
        Iterator<GameEvent> iterator = queue.get(eventType).get(eventTopic).iterator();

        while (iterator.hasNext()) {
            GameEvent gameEvent = iterator.next();
            if (gameEvent.isTooOld()) {
                iterator.remove();
            }
            gameEvent.increaseAge();
        }
    }
}
