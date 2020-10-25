package com.mantkowdev.rabbitgame;

import com.mantkowdev.rabbitgame.api.GameActor;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.PathTile;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class GameMapMovePlugin<T extends GameActor> implements Plugin<T> {
    GameMap gameMap;
    GameEventService gameEventService;

    @Override
    public void handle(T object) {
        gameEventService
                .getEvent(SteeringEvent.class, "steering_player")
                .ifPresent(event -> handleEvent(object, event.getEventObject()));
    }

    private void handleEvent(T object, SteeringDirection direction) {
        PathTile currentTile = gameMap.getPathTileAt(object.getCenter());
        if (currentTile != null && currentTile.getNeighbours().containsKey(direction)) {
            Tuple<Float> newPosition = direction.calculateDelta(object.getCenter(), 2f);
            object.setCenterPosition(newPosition.a, newPosition.b);
        }
    }
}
