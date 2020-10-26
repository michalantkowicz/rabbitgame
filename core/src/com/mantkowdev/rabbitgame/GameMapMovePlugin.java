package com.mantkowdev.rabbitgame;

import com.mantkowdev.rabbitgame.api.GameActor;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.PathTile;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

import static com.mantkowdev.rabbitgame.Utils.distance;
import static com.mantkowdev.rabbitgame.Utils.vector;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class GameMapMovePlugin<T extends GameActor> implements Plugin<T> {
    GameMap gameMap;
    GameEventService gameEventService;
    private float STEP = 2f;

    @Override
    public void handle(T object) {
        gameEventService
                .getEvent(SteeringEvent.class, "steering_player")
                .ifPresent(event -> handleEvent(object, event.getEventObject()));
    }

    private void handleEvent(T object, Direction direction) {
        gameMap
                .getPathTileAt(object.getCenter())
                .map(tile -> calculateNewPosition(object, direction, tile))
                .ifPresent(newPosition -> validateAndSetNewPosition(object, newPosition));
    }

    private Tuple<Float> calculateNewPosition(T object, Direction direction, PathTile tile) {
        if (tile.getNeighbours().containsKey(direction)) {
            Direction currentDirection = Direction.getFromVector(vector(object.getCenter(), tile.getNeighbours().get(direction).getCenter())).get();
            Tuple<Float> newPosition = currentDirection.calculateDelta(object.getCenter(), STEP);
            return validate(newPosition) ? newPosition : calculateAlignToCenter(object, tile, direction);
        } else if (tile.getPathNeighbours().containsKey(direction)) {
            Direction currentDirection = Direction.getFromVector(vector(object.getCenter(), tile.getPathNeighbours().get(direction).getCenter())).get();
            Tuple<Float> newPosition = currentDirection.calculateDelta(object.getCenter(), STEP);
            return validate(newPosition) ? newPosition : calculateAlignToCenter(object, tile, direction);
        } else {
            return calculateAlignToCenter(object, tile, direction);
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

    private Tuple<Float> calculateAlignToCenter(T object, PathTile tile, Direction direction) {
        if (distance(object.getCenter(), tile.getCenter()) > STEP) {
            return direction.calculateDelta(object.getCenter(), STEP);
        } else {
            return tile.getCenter();
        }
    }
}
