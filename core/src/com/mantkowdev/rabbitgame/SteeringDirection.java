package com.mantkowdev.rabbitgame;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SteeringDirection {
    LEFT(Tuple.of(-1, 0)),
    RIGHT(Tuple.of(1, 0)),
    UP(Tuple.of(0, 1)),
    DOWN(Tuple.of(0, -1));

    private Tuple<Integer> delta;

    public Tuple<Float> calculateDelta(Tuple<Float> originCoordinates, float step) {
        return Tuple.of(originCoordinates.a + (delta.a * step), originCoordinates.b + (delta.b * step));
    }

    public Tuple<Integer> calculateDelta(Tuple<Integer> originCoordinates, int step) {
        return Tuple.of(originCoordinates.a + (delta.a * step), originCoordinates.b + (delta.b * step));
    }
}
