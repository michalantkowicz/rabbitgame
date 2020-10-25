package com.mantkowdev.rabbitgame;

public class SteeringEvent extends GameEvent<SteeringDirection> {
    SteeringEvent(SteeringDirection direction, String topic, int maxAge) {
        super(direction, topic, maxAge);
    }
}

