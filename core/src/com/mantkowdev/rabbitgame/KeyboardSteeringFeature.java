package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeyboardSteeringFeature implements Feature {
    private final String topic;
    private final GameEventService gameEventService;


    @Override
    public void act() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sendEvent("UP");
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            sendEvent("DOWN");
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sendEvent("LEFT");
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sendEvent("RIGHT");
        }
    }

    private void sendEvent(String value) {
        gameEventService.sendEvent(GameEvent.builder().topic(topic).value(value).build());
    }
}
