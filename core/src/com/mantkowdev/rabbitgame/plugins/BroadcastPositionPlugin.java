package com.mantkowdev.rabbitgame.plugins;

import com.mantkowdev.rabbitgame.api.Plugin;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.PositionEvent;
import com.mantkowdev.rabbitgame.api.GameActor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BroadcastPositionPlugin<T extends GameActor> implements Plugin<T> {
    GameEventService gameEventService;
    String topic;

    @Override
    public void handle(T object) {
        gameEventService.pushEvent(new PositionEvent(object.getCenter(), topic, 1));
    }
}
