package com.mantkowdev.rabbitgame;

import lombok.Data;
import lombok.NonNull;

@Data
public class Velocity {
    @NonNull
    private final float maxVelocity;
    private float x, y;

    void stop() {
        if (Math.abs(x) > 0.05f) {
            x /= 1.15f;
        } else {
            x = 0f;
        }

        if (Math.abs(y) > 0.05f) {
            y /= 1.15f;
        } else {
            y = 0f;
        }
    }

    void setUp() {
        x = 0;
        y = maxVelocity;
    }

    void setDown() {
        x = 0;
        y = -maxVelocity;
    }

    void setLeft() {
        x = -maxVelocity;
        y = 0;
    }

    void setRight() {
        x = maxVelocity;
        y = 0;
    }
}
