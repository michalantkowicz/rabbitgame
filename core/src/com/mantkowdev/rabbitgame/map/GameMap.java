package com.mantkowdev.rabbitgame.map;

import com.mantkowdev.rabbitgame.SteeringDirection;
import com.mantkowdev.rabbitgame.Tuple;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Value
public class GameMap {
    private final List<WallTile> walls;
    private final List<PlayerTile> players;
    private final Map<Tuple<Integer>, PathTile> path;

    @Builder
    public GameMap(List<WallTile> walls, List<PlayerTile> players, List<PathTile> pathTiles) {
        this.walls = walls;
        this.players = players;
        path = pathTiles.stream().collect(toMap(PathTile::getCoordinates, identity()));
        path.values().forEach(this::updateNeighbours);
    }

    private void updateNeighbours(PathTile pathTile) {
        Arrays.stream(SteeringDirection.values())
                .forEach(direction -> {
                    final Tuple<Integer> neighbourCoordinates = direction.calculateDelta(pathTile.getCoordinates(), 1);
                    if (path.containsKey(neighbourCoordinates)) {
                        pathTile.getNeighbours().put(direction, path.get(neighbourCoordinates));
                    }
                });
    }

    //TODO ensure that tile exists (if this one is throwing exception then player left path)
    public PathTile getPathTileAt(Tuple<Float> position) {
        Tuple<Integer> tileCoordinates = Tuple.of((int) Math.floor(position.a / 10f), (int) Math.floor(position.b / 10f));
        return path.get(tileCoordinates);
    }
}
