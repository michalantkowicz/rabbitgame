package com.mantkowdev.rabbitgame.plugins;

import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.api.GameActor;
import com.mantkowdev.rabbitgame.api.Plugin;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.PositionEvent;
import com.mantkowdev.rabbitgame.events.SteeringEvent;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.PathTile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.Comparator;
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

import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class SafeNeighbourSteeringPlugin<T extends GameActor> implements Plugin<T> {
    private static final int SAFE_DISTANCE = 4;

    GameMap map;
    GameEventService gameEventService;
    List<String> positionTopic;
    String steeringTopic;

    @NonFinal
    Direction currentDirection = null;

    @NonFinal
    List<PathTile> path = null;

    @Override
    public void handle(T object) {
        if (currentDirection == null || map.getPathTileAt(object.getCenter()).get().isAtCenter(object.getCenter())) {
            List<PositionEvent> positions = positionTopic.stream().map(topic -> gameEventService.getEvent(PositionEvent.class, topic)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

            if (!positions.isEmpty()) {
                PathTile currentTile = map.getPathTileAt(object.getCenter()).get();
                List<PathTile> playerTiles = positions.stream().map(position -> map.getPathTileAt(position.getEventObject()).get()).collect(Collectors.toList());

                Map<PathTile, Integer> tilesCost = getCost(playerTiles);

                if (currentPathIsNotSafe(tilesCost)) {
                    final PathData pathData = traversePath(
                            currentTile,
                            tile -> tilesCost.get(tile) > SAFE_DISTANCE,
                            PathData::new,
                            PathData::update
                    );
                    path = calculatePath(currentTile, findTarget(pathData), pathData);
                }

                currentDirection = calculateDirection(currentTile);
            }
        }
        if (currentDirection != null) {
            gameEventService.pushEvent(new SteeringEvent(currentDirection, steeringTopic, 0));
        }
    }

    private Direction calculateDirection(PathTile currentTile) {
        if (path.isEmpty()) {
            return null;
        } else {
            final PathTile nextTile = path.remove(0);
            return currentTile
                    .getNeighbours()
                    .entrySet()
                    .stream()
                    .filter(entry -> nextTile.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .get();
        }
    }

    private PathTile findTarget(PathData pathData) {
        return pathData.values.values().stream().max(new PathTileValueComparator()).get().pathTile;
    }

    private List<PathTile> calculatePath(PathTile from, PathTile target, PathData pathData) {
        final List<PathTile> path = new LinkedList<>();
        while (target != from) {
            path.add(0, target);
            target = pathData.parents.get(target);
        }
        return path;
    }

    private boolean currentPathIsNotSafe(Map<PathTile, Integer> tilesCost) {
        return path == null || path.isEmpty() || path.stream().anyMatch(tile -> tilesCost.get(tile) <= SAFE_DISTANCE);
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

    private static class PathData {
        private final Map<PathTile, PathTile> parents = new HashMap<>();
        private final Map<PathTile, PathTileValue> values = new HashMap<>();

        PathData(PathTile tile) {
            parents.put(tile, null);
            values.put(tile, new PathTileValue(tile, 0, tile.getCoins()));
        }

        void update(PathTile tile, PathTile neighbour) {
            parents.put(neighbour, tile);
            values.computeIfAbsent(neighbour, PathTileValue::new)
                    .update(
                            values.get(tile).distance + 1,
                            values.get(tile).coins + neighbour.getCoins()
                    );
        }
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Getter
    private static class PathTileValue {
        private final PathTile pathTile;
        private int distance;
        private int coins;

        private void update(int distance, int coins) {
            this.distance = distance;
            this.coins = coins;
        }
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

    private class PathTileValueComparator implements Comparator<PathTileValue> {
        @Override
        public int compare(PathTileValue o1, PathTileValue o2) {
            return (o1.coins == o2.coins) ? Integer.compare(o2.distance, o1.distance) : Integer.compare(o1.coins, o2.coins);
        }
    }
}
