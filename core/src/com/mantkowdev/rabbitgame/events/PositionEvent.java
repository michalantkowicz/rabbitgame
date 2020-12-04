package com.mantkowdev.rabbitgame.events;

import com.mantkowdev.rabbitgame.Tuple;
import com.mantkowdev.rabbitgame.api.GameEvent;

public class PositionEvent extends GameEvent<Tuple<Float>> {
    public PositionEvent(Tuple<Float> position, String topic, int maxAge) {
        super(position, topic, maxAge);
    }
}

