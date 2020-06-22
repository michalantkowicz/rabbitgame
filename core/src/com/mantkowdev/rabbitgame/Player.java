package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;

public class Player extends Group implements GameEventHandler {
    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private final Intersector intersector = new Intersector();

    public Player(Array<? extends TextureRegion> frames) {
        super();
        animation = new Animation<>(0.25f, frames, LOOP);
        setSize(50f, 50f);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(animation.getKeyFrame(stateTime, true), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void handle(GameEvent event) {
        if (event.getValue().equals("UP")) {
            if (!willCollide(getX(), getY() + 2)) {
                setY(getY() + 2);
            } else {
                handleHorizontalAlignment(getY() + 2);
            }
        } else if (event.getValue().equals("DOWN")) {
            if (!willCollide(getX(), getY() - 2)) {
                setY(getY() - 2);
            } else {
                handleHorizontalAlignment(getY() - 2);
            }
        } else if (event.getValue().equals("LEFT")) {
            if (!willCollide(getX() - 2, getY())) {
                setX(getX() - 2);
            } else {
                handleVerticalAlignment(getX() - 2);
            }
        } else if (event.getValue().equals("RIGHT")) {
            if (!willCollide(getX() + 2, getY())) {
                setX(getX() + 2);
            } else {
                handleVerticalAlignment(getX() + 2);
            }
        }
    }

    private void handleVerticalAlignment(float x) {
        for (int i = 1; i <= 10; i++) {
            if (!willCollide(x, getY() + i)) {
                setY(getY() + 1);
                break;
            } else if (!willCollide(x, getY() - i)) {
                setY(getY() - 1);
                break;
            }
        }
    }

    private void handleHorizontalAlignment(float y) {
        for (int i = 1; i <= 10; i++) {
            if (!willCollide(getX() + i, y)) {
                setX(getX() + 1);
                break;
            } else if (!willCollide(getX() - i, y)) {
                setX(getX() - 1);
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
