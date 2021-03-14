package com.mantkowdev.rabbitgame.features;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mantkowdev.rabbitgame.GameStage;
import com.mantkowdev.rabbitgame.Tuple;
import com.mantkowdev.rabbitgame.actors.Target;
import com.mantkowdev.rabbitgame.api.Feature;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.PositionEvent;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.PathTile;
import com.mantkowdev.rabbitgame.plugins.BroadcastPositionPlugin;
import com.mantkowdev.rabbitgame.plugins.PathMovingPlugin;
import com.mantkowdev.rabbitgame.plugins.SafeNeighbourSteeringPlugin;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
public class CatchingTargetFeature implements Feature {
    private int nextPosition;
    private final AssetManager assetManager;
    private final Stage stage;
    private Target target;
    private final GameMap map;
    private final GameEventService gameEventService;
    private final String targetPositionTopic;
    private final List<String> playerPositionTopics;

    @Override
    public void act() {
        gameEventService.getEvent(PositionEvent.class, targetPositionTopic).ifPresent(this::handleEvent);
    }

    private void handleEvent(PositionEvent positionEvent) {
        final List<PositionEvent> playerPositions = playerPositionTopics.stream()
                .map(topic -> gameEventService.getEvent(PositionEvent.class, topic))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        final Tuple<Float> objectPosition = positionEvent.getEventObject();

        playerPositions.forEach(position -> {
            if(distance(objectPosition, position.getEventObject()) < 30f) {
                // remove target
                target.remove();

                // add new target
                Array<TextureRegion> rframes = new Array<>();
                rframes.add(new TextureRegion(assetManager.get("r1.png", Texture.class)));
                rframes.add(new TextureRegion(assetManager.get("r2.png", Texture.class)));
                rframes.add(new TextureRegion(assetManager.get("r3.png", Texture.class)));
                rframes.add(new TextureRegion(assetManager.get("r2.png", Texture.class)));

                Tuple<Integer> targetCoordinates = map.getTargets().get(nextPosition).getCoordinates();
                Vector2 targetPosition = new Vector2(targetCoordinates.a, targetCoordinates.b).scl(PathTile.WIDTH);

                nextPosition++;
                if(nextPosition > 5) nextPosition = 0;

                target = Target.builder()
                        .animation(new Animation<>(0.25f, rframes, LOOP))
                        .plugin(new SafeNeighbourSteeringPlugin(map, gameEventService, Arrays.asList("player_position", "player_position2", "player_position3", "player_position4"), "steering_target"))
                        .plugin(new PathMovingPlugin(map, gameEventService, "steering_target"))
                        .plugin(new BroadcastPositionPlugin(gameEventService, "target_position"))
                        .position(targetPosition)
                        .size(new Vector2(30f, 30f))
                        .build();

                stage.addActor(target);
            }
        });



//
//        map.getPathTileAt(objectPosition).ifPresent(tile -> {
//            if (tile.isAtCenter(objectPosition) && coins.containsKey(tile.getCoordinates())) {
//                coins.get(tile.getCoordinates()).remove();
//                tile.setCoin(false);
//            }
//        });
    }

    private Map<PathTile, Integer> getCost(List<PathTile> playerTiles) {
        return playerTiles
                .stream()
                .map(playerTile ->
                        traversePath(
                                playerTile,
                                tile -> true,
                                tile -> mapOf(tile, 0),
                                (map, currentTile, neighbour) -> map.put(neighbour, map.get(currentTile) + 1)
                        )
                ).flatMap(map -> map.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::min));
    }

    private <U> U traversePath(PathTile startTile, Predicate<PathTile> visitPredicate, Function<PathTile, U> init, Update<U, PathTile, PathTile> update) {
        final List<PathTile> queue = new LinkedList<>();
        queue.add(startTile);

        final Set<PathTile> visited = new HashSet<>();
        visited.add(startTile);

        final U result = init.apply(startTile);

        while (!queue.isEmpty()) {
            final PathTile currentTile = queue.remove(0);
            for (PathTile neighbour : currentTile.getNeighbours().values()) {
                if (!visited.contains(neighbour) && visitPredicate.test(neighbour)) {
                    queue.add(neighbour);
                    visited.add(neighbour);
                    update.update(result, currentTile, neighbour);
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    private interface Update<Q, W, E> {
        void update(Q q, W w, E e);
    }

    private <A, B> Map<A, B> mapOf(A a, B b) {
        Map<A, B> map = new HashMap<>();
        map.put(a, b);
        return map;
    }

    private static float distance(Tuple<Float> from, Tuple<Float> to) {
        final float delta_x = to.a - from.a;
        final float delta_y = to.b - from.b;
        return (float) Math.sqrt(delta_x * delta_x + delta_y * delta_y);
    }
}
