package com.mantkowdev.rabbitgame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.Math.abs;

@RequiredArgsConstructor
public enum Direction {
    LEFT(Tuple.of(-1f, 0f)),
    RIGHT(Tuple.of(1f, 0f)),
    UP(Tuple.of(0f, 1f)),
    DOWN(Tuple.of(0f, -1f));

    static {
        LEFT.firstPerpendicular = DOWN;
        LEFT.secondPerpendicular = UP;

        RIGHT.firstPerpendicular = DOWN;
        RIGHT.secondPerpendicular = UP;

        UP.firstPerpendicular = LEFT;
        UP.secondPerpendicular = RIGHT;

        DOWN.firstPerpendicular = LEFT;
        DOWN.secondPerpendicular = RIGHT;
    }

    private final Tuple<Float> delta;

    @Getter
    private Direction firstPerpendicular, secondPerpendicular;

    public Tuple<Float> calculateDelta(Tuple<Float> originCoordinates, float step) {
        return Tuple.of(originCoordinates.a + (delta.a * step), originCoordinates.b + (delta.b * step));
    }

    public Tuple<Integer> calculateDelta(Tuple<Integer> originCoordinates, int step) {
        return Tuple.of((int) (originCoordinates.a + (delta.a * step)), (int) (originCoordinates.b + (delta.b * step)));
    }

    public static Optional<Direction> getFromVector(Tuple<Float> vector) {
        final float normalizedA = vector.a.equals(0f) ? 0f : (vector.a / abs(vector.a));
        final float normalizedB = vector.b.equals(0f) ? 0f : (vector.b / abs(vector.b));
        final Tuple<Float> normalizedVector = Tuple.of(normalizedA, normalizedB);

        return Arrays.stream(values()).filter(direction -> direction.delta.equals(normalizedVector)).findFirst();
    }
}
