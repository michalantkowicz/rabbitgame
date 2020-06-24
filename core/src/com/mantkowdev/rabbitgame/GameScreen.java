package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
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
        Vector2 playerPosition = new Vector2();

        TiledMap map = new TmxMapLoader().load("level1.tmx");
        TiledMapTileLayer mapLayer = (TiledMapTileLayer) map.getLayers().get(0);

        for (int x = 0; x < mapLayer.getWidth(); x++) {
            for (int y = 0; y < mapLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = mapLayer.getCell(x, y);
                String cellType = (String) cell.getTile().getProperties().get("TYPE");
                if (cellType.equals("WALL")) {
                    Actor wall = new Actor();
                    wall.debug();
                    wall.setSize(10, 10);
                    wall.setPosition(x * 10, y * 10);
                    stage.addActor(wall);
                } else if (cellType.equals("PLAYER")) {
                    playerPosition.set(x * 10, y * 10);
                }
            }
        }

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
