package com.mantkowdev.rabbitgame;

import lombok.Value;

@Value
public class Tuple<T> {
    public final T a, b;

    public static <T> Tuple<T> of(T a, T b) {
        return new Tuple<>(a, b);
    }
}
