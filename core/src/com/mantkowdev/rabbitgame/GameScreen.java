package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mantkowdev.rabbitgame.actors.Player;
import com.mantkowdev.rabbitgame.actors.Target;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.features.CollectingCoinFeature;
import com.mantkowdev.rabbitgame.api.Feature;
import com.mantkowdev.rabbitgame.features.KeyboardInputFeature;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.MapLoader;
import com.mantkowdev.rabbitgame.map.PathTile;
import com.mantkowdev.rabbitgame.map.WallTile;
import com.mantkowdev.rabbitgame.plugins.BroadcastPositionPlugin;
import com.mantkowdev.rabbitgame.plugins.PathMovingPlugin;
import com.mantkowdev.rabbitgame.plugins.SafeNeighbourSteeringPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Map<Tuple<Integer>, Image> coins = map
                .getPath()
                .values()
                .stream()
                .filter(PathTile::isCoin)
                .collect(Collectors.toMap(PathTile::getCoordinates, this::toCoinActor));

        coins.values().forEach(stage::addActor);

        Tuple<Integer> coordinates = map.getPlayers().get(0).getCoordinates();
        Vector2 playerPosition = new Vector2(coordinates.a, coordinates.b).scl(PathTile.WIDTH);

        Tuple<Integer> coordinates2 = map.getPlayers().get(1).getCoordinates();
        Vector2 playerPosition2 = new Vector2(coordinates2.a, coordinates2.b).scl(PathTile.WIDTH);

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(assetManager.get("1.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("3.png", Texture.class)));
        frames.add(new TextureRegion(assetManager.get("2.png", Texture.class)));

        Player player = Player.builder()
                .animation(new Animation<>(0.25f, frames, LOOP))
                .plugin(new PathMovingPlugin(map, gameEventService, "steering_player"))
                .plugin(new BroadcastPositionPlugin(gameEventService, "player_position"))
                .position(playerPosition)
                .size(new Vector2(30f, 30f))
                .build();

        Array<TextureRegion> frames2 = new Array<>();
        frames2.add(new TextureRegion(assetManager.get("11.png", Texture.class)));
        frames2.add(new TextureRegion(assetManager.get("22.png", Texture.class)));
        frames2.add(new TextureRegion(assetManager.get("33.png", Texture.class)));
        frames2.add(new TextureRegion(assetManager.get("22.png", Texture.class)));

//        Player player2 = Player.builder()
//                .animation(new Animation<>(0.25f, frames2, LOOP))
//                .plugin(new PathMovingPlugin(map, gameEventService, "steering_player2"))
//                .plugin(new BroadcastPositionPlugin(gameEventService, "player_position2"))
//                .position(playerPosition2)
//                .size(new Vector2(30f, 30f))
//                .build();

        stage.addActor(player);
//        stage.addActor(player2);

        Array<TextureRegion> rframes = new Array<>();
        rframes.add(new TextureRegion(assetManager.get("r1.png", Texture.class)));
        rframes.add(new TextureRegion(assetManager.get("r2.png", Texture.class)));
        rframes.add(new TextureRegion(assetManager.get("r3.png", Texture.class)));
        rframes.add(new TextureRegion(assetManager.get("r2.png", Texture.class)));

        Tuple<Integer> targetCoordinates = map.getTargets().get(0).getCoordinates();
        Vector2 targetPosition = new Vector2(targetCoordinates.a, targetCoordinates.b).scl(PathTile.WIDTH);

        Target target = Target.builder()
                .animation(new Animation<>(0.25f, rframes, LOOP))
                .plugin(new SafeNeighbourSteeringPlugin(map, gameEventService, Arrays.asList("player_position", "player_position2"), "steering_target"))
                .plugin(new PathMovingPlugin(map, gameEventService, "steering_target"))
                .plugin(new BroadcastPositionPlugin(gameEventService, "target_position"))
                .position(targetPosition)
                .size(new Vector2(30f, 30f))
                .build();

        stage.addActor(target);

        Feature playerSteering = new KeyboardInputFeature("steering_player", "steering_player2", gameEventService);
        features.add(playerSteering);
        features.add(new CollectingCoinFeature(coins, map, gameEventService, "target_position"));
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        features.forEach(Feature::act);
        gameEventService.update();
    }

    private Image toCoinActor(PathTile pathTile) {
        Image i = new Image(assetManager.get("coin.png", Texture.class));
        i.setSize(10, 10);
        i.setPosition(pathTile.getCoordinates().a * 10, pathTile.getCoordinates().b * 10);
        return i;
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
