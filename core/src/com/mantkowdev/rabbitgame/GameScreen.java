package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.MapLoader;
import com.mantkowdev.rabbitgame.map.WallTile;

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
        MapLoader mapLoader = new MapLoader("level1.tmx");
        GameMap map = mapLoader.load();

        map
                .getWalls()
                .stream()
                .map(this::toWallActor)
                .forEach(stage::addActor);

        Vector2 playerPosition = map.getPlayers().get(0).getPosition().scl(10f);

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(assetManager.get("1.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("3.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));

        Player player = new Player(frames, gameEventService);
        player.setPosition(playerPosition.x, playerPosition.y);
        stage.addActor(player);

        String playerSteeringTopic = "steering_player";

        Feature playerSteering = new KeyboardSteeringFeature(playerSteeringTopic, gameEventService);
        features.add(playerSteering);
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        features.forEach(Feature::act);
        gameEventService.update();
    }

    private Actor toWallActor(WallTile wallTile) {
        Actor a = new Actor();
        a.debug();
        a.setSize(10, 10);
        a.setPosition(wallTile.getPosition().x * 10, wallTile.getPosition().y * 10);
        return a;
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
