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
import com.mantkowdev.rabbitgame.api.Feature;
import com.mantkowdev.rabbitgame.api.Plugin;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.features.CatchingTargetFeature;
import com.mantkowdev.rabbitgame.features.CollectingCoinFeature;
import com.mantkowdev.rabbitgame.features.KeyboardInputFeature;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.MapLoader;
import com.mantkowdev.rabbitgame.map.PathTile;
import com.mantkowdev.rabbitgame.map.WallTile;
import com.mantkowdev.rabbitgame.plugins.BroadcastPositionPlugin;
import com.mantkowdev.rabbitgame.plugins.PathMovingPlugin;
import com.mantkowdev.rabbitgame.plugins.SafeNeighbourSteeringPlugin;
import com.mantkowdev.rabbitgame.plugins.TrackingTargetSteeringPlugin;

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
    @SuppressWarnings("unchecked")
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

        Player player = createPlayer(
                map,
                0,
                Arrays.asList("1", "2", "3", "2"),
                Arrays.asList(
                        new PathMovingPlugin(map, gameEventService, "steering_player"),
                        new BroadcastPositionPlugin(gameEventService, "player_position")
                )
        );

        Player player2 = createPlayer(
                map,
                1,
                Arrays.asList("11", "22", "33", "22"),
                Arrays.asList(
                        new TrackingTargetSteeringPlugin(map, gameEventService, Arrays.asList("target_position", "target_position2"), Arrays.asList("player_position", "player_position3", "player_position4"), "steering_player2"),
                        new PathMovingPlugin(map, gameEventService, "steering_player2"),
                        new BroadcastPositionPlugin(gameEventService, "player_position2")
                )
        );

        Player player3 = createPlayer(
                map,
                2,
                Arrays.asList("111", "222", "333", "222"),
                Arrays.asList(
                        new TrackingTargetSteeringPlugin(map, gameEventService, Arrays.asList("target_position", "target_position2"), Arrays.asList("player_position", "player_position2", "player_position4"), "steering_player3"),
                        new PathMovingPlugin(map, gameEventService, "steering_player3"),
                        new BroadcastPositionPlugin(gameEventService, "player_position3")
                )
        );

        Player player4 = createPlayer(
                map,
                3,
                Arrays.asList("1111", "2222", "3333", "2222"),
                Arrays.asList(
                        new TrackingTargetSteeringPlugin(map, gameEventService, Arrays.asList("target_position", "target_position2"), Arrays.asList("player_position", "player_position2", "player_position3"), "steering_player4"),
                        new PathMovingPlugin(map, gameEventService, "steering_player4"),
                        new BroadcastPositionPlugin(gameEventService, "player_position4")
                )
        );

        stage.addActor(player);
        stage.addActor(player2);
        stage.addActor(player3);
        stage.addActor(player4);

        Array<TextureRegion> rframes = new Array<>();
        rframes.add(new TextureRegion(assetManager.get("r1.png", Texture.class)));
        rframes.add(new TextureRegion(assetManager.get("r2.png", Texture.class)));
        rframes.add(new TextureRegion(assetManager.get("r3.png", Texture.class)));
        rframes.add(new TextureRegion(assetManager.get("r2.png", Texture.class)));

        Tuple<Integer> targetCoordinates = map.getTargets().get(0).getCoordinates();
        Vector2 targetPosition = new Vector2(targetCoordinates.a, targetCoordinates.b).scl(PathTile.WIDTH);

        Target target = Target.builder()
                .animation(new Animation<>(0.25f, rframes, LOOP))
                .plugin(new SafeNeighbourSteeringPlugin(map, gameEventService, Arrays.asList("player_position", "player_position2", "player_position3", "player_position4"), "steering_target"))
                .plugin(new PathMovingPlugin(map, gameEventService, "steering_target"))
                .plugin(new BroadcastPositionPlugin(gameEventService, "target_position"))
                .position(targetPosition)
                .size(new Vector2(30f, 30f))
                .build();

        Tuple<Integer> targetCoordinates2 = map.getTargets().get(5).getCoordinates();
        Vector2 targetPosition2 = new Vector2(targetCoordinates2.a, targetCoordinates2.b).scl(PathTile.WIDTH);

        Target target2 = Target.builder()
                .animation(new Animation<>(0.25f, rframes, LOOP))
                .plugin(new SafeNeighbourSteeringPlugin(map, gameEventService, Arrays.asList("player_position", "player_position2", "player_position3", "player_position4"), "steering_target2"))
                .plugin(new PathMovingPlugin(map, gameEventService, "steering_target2"))
                .plugin(new BroadcastPositionPlugin(gameEventService, "target_position2"))
                .position(targetPosition2)
                .size(new Vector2(30f, 30f))
                .build();

        stage.addActor(target);
        stage.addActor(target2);

        Feature playerSteering = new KeyboardInputFeature("steering_player", "steering_player2", gameEventService);
        features.add(playerSteering);
        features.add(new CollectingCoinFeature(coins, map, gameEventService, "target_position"));
        features.add(new CollectingCoinFeature(coins, map, gameEventService, "target_position2"));

        //TODO fix this!
        features.add(new CatchingTargetFeature(1, assetManager, stage, target, map, gameEventService, "target_position", Arrays.asList("player_position", "player_position2", "player_position3", "player_position4")));
        features.add(new CatchingTargetFeature(1, assetManager, stage, target, map, gameEventService, "target_position2", Arrays.asList("player_position", "player_position2", "player_position3", "player_position4")));
    }

    private Player createPlayer(GameMap map, int playerIndex, List<String> frameNames, List<Plugin<Player>> plugins) {
        Array<TextureRegion> frames = new Array<>();
        frameNames.forEach(name -> frames.add(new TextureRegion(assetManager.get(name + ".png", Texture.class))));

        Tuple<Integer> coordinates = map.getPlayers().get(playerIndex).getCoordinates();
        Vector2 playerPosition = new Vector2(coordinates.a, coordinates.b).scl(PathTile.WIDTH);

        Player.PlayerBuilder builder = Player.builder()
                .animation(new Animation<>(0.25f, frames, LOOP))
                .position(playerPosition)
                .size(new Vector2(30f, 30f));

        plugins.forEach(builder::plugin);

        return builder.build();
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
