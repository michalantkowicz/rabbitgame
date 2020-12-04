package com.mantkowdev.rabbitgame.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mantkowdev.rabbitgame.api.Plugin;
import com.mantkowdev.rabbitgame.api.GameActor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

public class Target extends GameActor {
    private Animation<TextureRegion> animation;
    private List<Plugin<Target>> plugins;

    private float stateTime = 0f;

    @Builder
    public Target(
            Animation<TextureRegion> animation,
            @Singular List<Plugin<Target>> plugins,
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
