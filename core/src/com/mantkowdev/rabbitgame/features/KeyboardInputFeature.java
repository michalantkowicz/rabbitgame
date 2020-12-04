package com.mantkowdev.rabbitgame.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.api.Feature;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.SteeringEvent;
import lombok.RequiredArgsConstructor;

import static com.mantkowdev.rabbitgame.Direction.DOWN;
import static com.mantkowdev.rabbitgame.Direction.LEFT;
import static com.mantkowdev.rabbitgame.Direction.RIGHT;
import static com.mantkowdev.rabbitgame.Direction.UP;

@RequiredArgsConstructor
public class KeyboardInputFeature implements Feature {
    private final String topic;
    private final String topic2;
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


        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            sendEvent2(UP);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            sendEvent2(DOWN);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            sendEvent2(LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            sendEvent2(RIGHT);
        }
    }

    private void sendEvent(Direction direction) {
        gameEventService.pushEvent(new SteeringEvent(direction, topic, 1));
    }

    private void sendEvent2(Direction direction) {
        gameEventService.pushEvent(new SteeringEvent(direction, topic2, 1));
    }
}
