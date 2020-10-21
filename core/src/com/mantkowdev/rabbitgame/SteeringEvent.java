package com.mantkowdev.rabbitgame;

public class SteeringEvent extends GameEvent<String> {
    SteeringEvent(String eventObject, String topic, int maxAge) {
        super(eventObject, topic, maxAge);
    }
}
