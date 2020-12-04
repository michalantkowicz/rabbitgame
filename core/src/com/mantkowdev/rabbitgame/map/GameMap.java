package com.mantkowdev.rabbitgame.map;

import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.Tuple;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Value
public class GameMap {
    private final List<WallTile> walls;
    private final List<PlayerTile> players;
    private final List<TargetTile> targets;
    private final Map<Tuple<Integer>, PathTile> path;

    @Builder
    public GameMap(List<WallTile> walls, List<PlayerTile> players, List<TargetTile> targets, List<PathTile> pathTiles) {
        this.walls = walls;
        this.players = players;
        this.targets = targets;
        path = pathTiles.stream().collect(toMap(PathTile::getCoordinates, identity()));
        path.values().forEach(this::updateNeighbours);

        path.values().forEach(tile -> {
            Arrays.stream(Direction.values()).forEach(direction -> {
                if (!tile.getNeighbours().containsKey(direction)) {
                    setProperPathTile(tile, direction, direction.getFirstPerpendicular(), direction.getSecondPerpendicular());
                }
            });
        });
    }

    private void updateNeighbours(PathTile pathTile) {
        Arrays.stream(Direction.values())
                .forEach(direction -> {
                    final Tuple<Integer> neighbourCoordinates = direction.calculateDelta(pathTile.getCoordinates(), 1);
                    if (path.containsKey(neighbourCoordinates)) {
                        pathTile.getNeighbours().put(direction, path.get(neighbourCoordinates));
                    }
                });
    }

    private int MAX_BRANCH_LENGTH = 3;

    private void setProperPathTile(PathTile tile, Direction targetDirection, Direction firstDirection, Direction secondDirection) {
        int firstBranchLength = getBranchLength(tile, firstDirection, targetDirection, 0);
        int secondBranchLength = getBranchLength(tile, secondDirection, targetDirection, 0);

        if (firstBranchLength >= 0 || secondBranchLength >= 0) {
            if (firstBranchLength >= 0 && secondBranchLength >= 0) {
                if (firstBranchLength <= secondBranchLength && firstBranchLength < MAX_BRANCH_LENGTH) {
                    tile.getPathNeighbours().put(targetDirection, tile.getNeighbours().get(firstDirection));
                } else if (secondBranchLength <= firstBranchLength && secondBranchLength < MAX_BRANCH_LENGTH) {
                    tile.getPathNeighbours().put(targetDirection, tile.getNeighbours().get(secondDirection));
                }
            } else {
                if (firstBranchLength > secondBranchLength && firstBranchLength < MAX_BRANCH_LENGTH) {
                    tile.getPathNeighbours().put(targetDirection, tile.getNeighbours().get(firstDirection));
                } else if (secondBranchLength > firstBranchLength && secondBranchLength < MAX_BRANCH_LENGTH) {
                    tile.getPathNeighbours().put(targetDirection, tile.getNeighbours().get(secondDirection));
                }
            }
        }
    }

    private int getBranchLength(PathTile tile, Direction branchDirection, Direction targetDirection, int length) {
        if (tile.getNeighbours().containsKey(targetDirection)) {
            return length;
        } else {
            if (tile.getNeighbours().containsKey(branchDirection)) {
                return getBranchLength(tile.getNeighbours().get(branchDirection), branchDirection, targetDirection, length + 1);
            } else {
                return -1;
            }
        }
    }

    //TODO ensure that tile exists (if this one is throwing exception then player left path)
    public Optional<PathTile> getPathTileAt(Tuple<Float> position) {
        Tuple<Integer> tileCoordinates = Tuple.of((int) Math.floor(position.a / PathTile.WIDTH), (int) Math.floor(position.b / PathTile.WIDTH));
        return Optional.ofNullable(path.get(tileCoordinates));
    }


}
