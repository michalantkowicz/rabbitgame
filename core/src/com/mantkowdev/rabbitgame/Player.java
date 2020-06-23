package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Optional;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;

public class Player extends Group {
    private final Animation<TextureRegion> animation;
    private GameEventService gameEventService;
    private float stateTime = 0f;

    private Velocity velocity = new Velocity(2f);
    private float maxVelocity = 2f;


    public Player(Array<? extends TextureRegion> frames, GameEventService gameEventService) {
        super();
        animation = new Animation<>(0.25f, frames, LOOP);
        this.gameEventService = gameEventService;
        setSize(50f, 50f);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;

        Optional<GameEvent> event = gameEventService.getEvent(GameEvent.class, "steering_player");

        if (event.isPresent()) {
            handle(event.get());
        } else {
            velocity.stop();
        }

        if (!willCollide(getX() + velocity.getX(), getY() + velocity.getY())) {
            setX(getX() + velocity.getX());
            setY(getY() + velocity.getY());
        } else {
            if (velocity.getX() != 0) {
                handleVerticalAlignment(getX() + velocity.getX());
            }
            if (velocity.getY() != 0) {
                handleHorizontalAlignment(getY() + velocity.getY());
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        System.out.println("X = " + getX() + ", Y = " + getY());
        batch.draw(animation.getKeyFrame(stateTime, true), getX(), getY(), getWidth(), getHeight());
    }

    public void handle(GameEvent event) {
        if (event.getEventObject().equals("UP")) {
            velocity.setUp();
        } else if (event.getEventObject().equals("DOWN")) {
            velocity.setDown();
        } else if (event.getEventObject().equals("LEFT")) {
            velocity.setLeft();
        } else if (event.getEventObject().equals("RIGHT")) {
            velocity.setRight();
        }
    }

    private void handleVerticalAlignment(float x) {
        for (int i = 1; i <= 20; i++) {
            if (!willCollide(x, (int) getY() + i)) {
                if (i > 1) {
                    setY((int) getY() + velocity.getMaxVelocity());
                } else {
                    setY((int) getY() + 1);
                }
                break;
            } else if (!willCollide(x, (int) getY() - i)) {
                if (i > 1) {
                    setY((int) getY() - velocity.getMaxVelocity());
                } else {
                    setY((int) getY() - 1);
                }
                break;
            }
        }
    }

    private void handleHorizontalAlignment(float y) {
        for (int i = 1; i <= 20; i++) {
            if (!willCollide((int) getX() + i, y)) {
                if (i > 1) {
                    setX((int) getX() + velocity.getMaxVelocity());
                } else {
                    setX((int) getX() + 1);
                }
                break;
            } else if (!willCollide((int) getX() - i, y)) {
                if (i > 1) {
                    setX((int) getX() - velocity.getMaxVelocity());
                } else {
                    setX((int) getX() - 1);
                }
                break;
            }
        }
    }

    private boolean willCollide(float x, float y) {
        return Arrays.stream(getStage().getActors().items)
                .anyMatch(actor ->
                        actor != null &&
                                !actor.equals(this) &&
                                doOverlap(
                                        x, y + getHeight(),
                                        x + getWidth(), y,
                                        actor.getX(), actor.getY() + actor.getHeight(),
                                        actor.getX() + actor.getWidth(), actor.getY()
                                )
                );
    }

    private boolean doOverlap(float tlx1, float tly1, float brx1, float bry1, float tlx2, float tly2, float brx2, float bry2) {
        return !(tlx1 >= brx2) && !(tlx2 >= brx1) && !(tly1 <= bry2) && !(tly2 <= bry1);
    }
}
