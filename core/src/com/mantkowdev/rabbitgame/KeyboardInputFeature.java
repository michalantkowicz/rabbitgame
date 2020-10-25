package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lombok.RequiredArgsConstructor;

import static com.mantkowdev.rabbitgame.SteeringDirection.DOWN;
import static com.mantkowdev.rabbitgame.SteeringDirection.LEFT;
import static com.mantkowdev.rabbitgame.SteeringDirection.RIGHT;
import static com.mantkowdev.rabbitgame.SteeringDirection.UP;

@RequiredArgsConstructor
public class KeyboardInputFeature implements Feature {
    private final String topic;
    private final GameEventService gameEventService;


    @Override
    public void act() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sendEvent(UP);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            sendEvent(DOWN);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sendEvent(LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sendEvent(RIGHT);
        }
    }

    private void sendEvent(SteeringDirection direction) {
        gameEventService.pushEvent(new SteeringEvent(direction, topic, 2));
    }
}
