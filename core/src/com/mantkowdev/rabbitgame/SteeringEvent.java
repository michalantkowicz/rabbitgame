package com.mantkowdev.rabbitgame;

public class SteeringEvent extends GameEvent<Direction> {
    SteeringEvent(Direction direction, String topic, int maxAge) {
        super(direction, topic, maxAge);
    }
}

