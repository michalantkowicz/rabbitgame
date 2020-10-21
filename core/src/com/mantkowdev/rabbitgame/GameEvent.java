package com.mantkowdev.rabbitgame;

import lombok.Getter;

abstract class GameEvent<T> {
    private static final int IMMORTAL = Integer.MIN_VALUE;

    @Getter
    private String topic;
    @Getter
    private final T eventObject;

    private int age;
    private int maxAge;

    /**
     * @param maxAge how much loops event lives, must be greater or equal 0
     * @throws IllegalArgumentException if maxAge < 0
     */
    GameEvent(T eventObject, String topic, int maxAge) {
        this.eventObject = eventObject;
        this.topic = topic;
        if (maxAge != IMMORTAL && maxAge < 0) {
            throw new IllegalArgumentException("The maxAge must be at least 0");
        }
        this.maxAge = maxAge;
        this.age = 0;
    }

    void increaseAge() {
        age++;
    }

    boolean isTooOld() {
        return (maxAge != IMMORTAL) && (age >= maxAge);
    }
}
