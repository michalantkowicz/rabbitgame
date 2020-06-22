package com.mantkowdev.rabbitgame;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameEvent {
    private final String topic;
    private final String value;
}
