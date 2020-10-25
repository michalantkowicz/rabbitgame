package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mantkowdev.rabbitgame.actors.Player;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.MapLoader;
import com.mantkowdev.rabbitgame.map.WallTile;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;

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

        Tuple<Integer> coordinates = map.getPlayers().get(0).getCoordinates();
        Vector2 playerPosition = new Vector2(coordinates.a, coordinates.b).scl(10f);

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(assetManager.get("1.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("3.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));

        Player player = Player.builder()
                .animation(new Animation<>(0.25f, frames, LOOP))
//                .plugin(new SteeringPlugin(gameEventService))
                .plugin(new GameMapMovePlugin(map, gameEventService))
                .position(playerPosition)
                .size(new Vector2(30f, 30f))
                .build();

        stage.addActor(player);

        String playerSteeringTopic = "steering_player";

        Feature playerSteering = new KeyboardInputFeature(playerSteeringTopic, gameEventService);
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
        a.setPosition(wallTile.getCoordinates().a * 10, wallTile.getCoordinates().b * 10);
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
