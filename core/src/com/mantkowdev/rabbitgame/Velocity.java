package com.mantkowdev.rabbitgame;

import lombok.Data;
import lombok.NonNull;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

@Data
public class Velocity {
    private final static float FRICTION_FACTOR = 1.01f;
    private final static float MIN_STEP = 0.05f;

    @NonNull
    private final float maxVelocity;
    private float x, y;

    void stop() {

        if (x > 0) {
            x = max(0f, x / FRICTION_FACTOR - MIN_STEP);
        } else {
            x = min(0f, x / FRICTION_FACTOR + MIN_STEP);
        }

        if (y > 0) {
            y = max(0f, y / FRICTION_FACTOR - MIN_STEP);
        } else {
            y = min(0f, y / FRICTION_FACTOR + MIN_STEP);
        }
    }

    void setUp() {
        if(x != 0) {
            y = abs(x); // to keep the velocity if character is moving
        } else {
            y = min(maxVelocity, abs(y) * FRICTION_FACTOR + MIN_STEP); // in other case speed up
        }
        x = 0;
    }

    void setDown() {
        if(x != 0) {
            y = - abs(x);
        } else {
            y = max(-maxVelocity, -(abs(y) * FRICTION_FACTOR + MIN_STEP));
        }
        x = 0;
    }

    void setLeft() {
        if(y != 0) {
            x = -abs(y);
        } else {
            x = max(-maxVelocity, -(abs(x) * FRICTION_FACTOR + MIN_STEP));
        }
        y = 0;
    }

    void setRight() {
        if(y != 0) {
            x = abs(y);
        } else {
            x = min(maxVelocity, abs(x) * FRICTION_FACTOR + MIN_STEP);
        }
        y = 0;
    }
}
