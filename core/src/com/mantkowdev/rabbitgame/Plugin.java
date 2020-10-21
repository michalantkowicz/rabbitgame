package com.mantkowdev.rabbitgame;

public interface Plugin<T> {
    void handle(T owner);
}
