package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private final Stage stage = new GameStage(new FitViewport(Main.WIDTH, Main.HEIGHT));
    private AssetManager assetManager;
    private GameEventService gameEventService = new GameEventService();

    private final List<Feature> features = new ArrayList<>();

    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(assetManager.get("1.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("3.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));

        Player player = new Player(frames, gameEventService);
        player.setPosition(200, 150);
        stage.addActor(player);

        String playerSteeringTopic = "steering_player";

        Feature playerSteering = new KeyboardSteeringFeature(playerSteeringTopic, gameEventService);
        features.add(playerSteering);

        Actor wall = new Actor();
        wall.debug();
        wall.setSize(100,200);
        wall.setPosition(100, 100);
        stage.addActor(wall);

        Actor wall2 = new Actor();
        wall2.debug();
        wall2.setSize(100,200);
        wall2.setPosition(250, 100);
        stage.addActor(wall2);

        Actor wall3 = new Actor();
        wall3.debug();
        wall3.setSize(250,100);
        wall3.setPosition(100, 350);
        stage.addActor(wall3);
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        features.forEach(Feature::act);
        gameEventService.update();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
