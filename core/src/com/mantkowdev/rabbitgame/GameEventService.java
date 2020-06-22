package com.mantkowdev.rabbitgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class GameEventService {
    private final Map<String, List<GameEventHandler>> handlers = new HashMap<>();
    private final List<GameEventHandler> dlqList = singletonList(new DlqHandler());

    public void registerHandler(String topic, GameEventHandler handler) {
        handlers
                .computeIfAbsent(topic, value -> new ArrayList<>())
                .add(handler);
    }

    public void sendEvent(GameEvent event) {
        handlers
                .getOrDefault(event.getTopic(), dlqList)
                .forEach(handler -> handler.handle(event));
    }

    private class DlqHandler implements GameEventHandler {
        private final List<GameEvent> store = new LinkedList<>();

        @Override
        public void handle(GameEvent event) {
            store.add(event);
        }
    }
}
