package com.mantkowdev.rabbitgame.api;

public interface Plugin<T> {
    void handle(T object);
}
