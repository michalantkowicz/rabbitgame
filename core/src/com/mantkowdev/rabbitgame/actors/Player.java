package com.mantkowdev.rabbitgame.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mantkowdev.rabbitgame.Plugin;
import com.mantkowdev.rabbitgame.api.GameActor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

public class Player extends GameActor {
    private Animation<TextureRegion> animation;
    private List<Plugin<Player>> plugins;

    private float stateTime = 0f;

    @Builder
    public Player(
            Animation<TextureRegion> animation,
            @Singular List<Plugin<Player>> plugins,
            @NonNull Vector2 position,
            @NonNull Vector2 size
    ) {
        this.animation = animation;
        this.plugins = plugins;
        setPosition(position.x, position.y);
        setSize(size.x, size.y);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
        plugins.forEach(plugin -> plugin.handle(this));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(animation.getKeyFrame(stateTime, true), getX(), getY(), getWidth(), getHeight());
    }
}
