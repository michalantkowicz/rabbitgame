package com.mantkowdev.rabbitgame.plugins;

import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.api.Plugin;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.SteeringEvent;
import com.mantkowdev.rabbitgame.Tuple;
import com.mantkowdev.rabbitgame.api.GameActor;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.PathTile;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

import static com.mantkowdev.rabbitgame.Direction.getFromVector;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class PathMovingPlugin<T extends GameActor> implements Plugin<T> {
    GameMap gameMap;
    GameEventService gameEventService;
    String topic;
    private float STEP = 2f;

    @Override
    public void handle(T object) {
        gameEventService
                .getEvent(SteeringEvent.class, topic)
                .ifPresent(event -> handleEvent(object, event.getEventObject()));
    }

    private void handleEvent(T object, Direction direction) {
        if (topic.equals("steering_target")) {
            System.out.println("Handling " + direction);
        }
        gameMap
                .getPathTileAt(object.getCenter())
                .map(tile -> calculateNewPosition(object, direction, tile))
                .ifPresent(newPosition -> validateAndSetNewPosition(object, newPosition));
    }

    private Tuple<Float> calculateNewPosition(T object, Direction direction, PathTile tile) {
        PathTile target = tile.getNeighbours().getOrDefault(direction, tile.getPathNeighbours().getOrDefault(direction, tile));
        Optional<Direction> currentDirection = getFromVector(vector(object.getCenter(), target.getCenter()));

        return currentDirection
                .map(d -> d.calculateDelta(object.getCenter(), STEP))
                .orElseGet(() -> {
                    Optional<Direction> fromVector = getFromVector(vector(object.getCenter(), tile.getCenter()));
                    return fromVector.map(dd -> dd.calculateDelta(object.getCenter(), STEP)).orElseGet(() -> object.getCenter());
                });
    }

    private Tuple<Float> calculateStep(Tuple<Float> from, Tuple<Float> to) {
        Direction direction = getFromVector(vector(from, to)).get();

        if (distance(from, to) > STEP) {
            return direction.calculateDelta(from, STEP);
        } else {
            return to;
        }
    }

    private boolean validate(Tuple<Float> newPosition) {
        Optional<PathTile> pathTile = gameMap.getPathTileAt(newPosition);
        return pathTile.isPresent() && pathTile.get().isOnAxis(newPosition);
    }

    private void validateAndSetNewPosition(T object, Tuple<Float> newPosition) {
        if (validate(newPosition)) {
            object.setCenterPosition(newPosition.a, newPosition.b);
        }
    }

    private static float distance(Tuple<Float> from, Tuple<Float> to) {
        final float delta_x = to.a - from.a;
        final float delta_y = to.b - from.b;
        return (float) Math.sqrt(delta_x * delta_x + delta_y * delta_y);
    }

    private static Tuple<Float> vector(Tuple<Float> from, Tuple<Float> to) {
        return Tuple.of(to.a - from.a, to.b - from.b);
    }
}
