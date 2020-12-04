package com.mantkowdev.rabbitgame.events;

import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.api.GameEvent;

public class SteeringEvent extends GameEvent<Direction> {
    public SteeringEvent(Direction direction, String topic, int maxAge) {
        super(direction, topic, maxAge);
    }
}

