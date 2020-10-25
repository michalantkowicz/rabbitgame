package com.mantkowdev.rabbitgame.api;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mantkowdev.rabbitgame.Tuple;

public abstract class GameActor extends Actor {
    public Tuple<Float> getCenter() {
        return Tuple.of(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
    }

    public void setCenterPosition(float centerX, float centerY) {
        setPosition(centerX - getWidth() / 2f, centerY - getHeight() / 2f);
    }
}
