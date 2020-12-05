package com.mantkowdev.rabbitgame.plugins;

import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.api.GameActor;
import com.mantkowdev.rabbitgame.api.Plugin;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.PositionEvent;
import com.mantkowdev.rabbitgame.events.SteeringEvent;
import com.mantkowdev.rabbitgame.map.GameMap;
import com.mantkowdev.rabbitgame.map.PathTile;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class SafeNeighbourSteeringPlugin<T extends GameActor> implements Plugin<T> {
    GameMap map;
    GameEventService gameEventService;
    List<String> positionTopic;
    String steeringTopic;

    @NonFinal
    Direction currentDirection = null;

    @NonFinal
    PathTile currentTarget = null;
    @NonFinal
    List<PathTile> currentTargetPath = null;

    @Override
    public void handle(T object) {
        System.out.println("OBJECT CENTER: " + object.getCenter());
        if (currentDirection == null || map.getPathTileAt(object.getCenter()).get().isAtCenter(object.getCenter())) {
            List<PositionEvent> positions = positionTopic.stream().map(topic -> gameEventService.getEvent(PositionEvent.class, topic)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

            if (!positions.isEmpty()) {
                PathTile currentTile = map.getPathTileAt(object.getCenter()).get();
                List<PathTile> playerTiles = positions.stream().map(position -> map.getPathTileAt(position.getEventObject()).get()).collect(Collectors.toList());

                Map<PathTile, Integer> tilesCost = getCost(playerTiles);

                if (!currentTargetIsSafe(tilesCost) || currentTile.equals(currentTarget)) {
                    if (!currentTargetIsSafe(tilesCost)) System.out.println("NOT SAFE ANYMORE");
                    System.out.println("CHANGING TARGET");
                    List<PathTile> queue = new LinkedList<>();
                    Set<PathTile> visited = new HashSet<>();
                    Map<PathTile, Integer> distance = new HashMap<>();
                    Map<PathTile, PathTile> parent = new HashMap<>();
                    Map<PathTile, Integer> costInCoins = new HashMap<>();

                    queue.add(currentTile);
                    visited.add(currentTile);
                    distance.put(currentTile, 0);
                    parent.put(currentTile, null);
                    costInCoins.put(currentTile, currentTile.isCoin() ? 1 : 0);

                    tilesCost.entrySet().stream().filter(entry -> entry.getValue() < 10).forEach(entry -> visited.add(entry.getKey()));

                    while (!queue.isEmpty()) {
                        PathTile tile = queue.remove(0);
                        for (PathTile neighbour : tile.getNeighbours().values()) {
                            if (!visited.contains(neighbour)) {
                                queue.add(neighbour);
                                distance.put(neighbour, distance.get(tile) + 1);
                                costInCoins.put(neighbour, costInCoins.get(tile) + (neighbour.isCoin() ? 1 : 0));
                                parent.put(neighbour, tile);
                                visited.add(neighbour);
                            }
                        }
                    }

                    List<Map.Entry<PathTile, Integer>> mostValuablePaths = costInCoins
                            .entrySet()
                            .stream()
                            .collect(Collectors.groupingBy(Map.Entry::getValue))
                            .entrySet()
                            .stream()
                            .max(Map.Entry.comparingByKey())
                            .get()
                            .getValue();

                    currentTarget = Collections.min(mostValuablePaths, Comparator.comparingInt(Map.Entry::getValue)).getKey();

                    currentTargetPath = new LinkedList<>();
                    PathTile current = currentTarget;
                    while (current != currentTile) {
                        currentTargetPath.add(0, current);
                        current = parent.get(current);
                    }
                }

                if (!currentTargetPath.isEmpty()) {
                    PathTile finalTargetTile = currentTargetPath.remove(0);
                    System.out.println("CURRENT: " + finalTargetTile.getCoordinates() + ", " + object.getCenter());
                    Optional<Direction> first = currentTile
                            .getNeighbours()
                            .entrySet()
                            .stream()
                            .filter(entry -> finalTargetTile.equals(entry.getValue()))
                            .map(Map.Entry::getKey).findFirst();

                    currentDirection = first.get();
                } else {
                    currentDirection = null;
                }
            }
        }
        if (currentDirection != null) {
            gameEventService.pushEvent(new SteeringEvent(currentDirection, steeringTopic, 0));
        }
    }

    private boolean currentTargetIsSafe(Map<PathTile, Integer> tilesCost) {
        if (currentTargetPath == null || currentTargetPath.isEmpty()) {
            System.out.println("EMPTY!");
            return false;
        } else {
            for (PathTile tt : currentTargetPath) {
                if (tilesCost.get(tt) < 10) {
                    System.out.println("! " + tt.getCoordinates() + tilesCost.get(tt));
                    return false;
                }
            }
            return true;
        }
    }

    private Map<PathTile, Integer> getCost(List<PathTile> playerTiles) {
        final Map<PathTile, Integer> result = new HashMap<>();

        playerTiles
                .stream()
                .map(playerTile -> traversePath(
                        playerTile,
                        tile -> mapOf(tile, 0),
                        (map, currentTile, neighbour) -> map.put(neighbour, map.get(currentTile) + 1))
                ).forEach(cost ->
                cost.entrySet().stream().forEach(entry -> {
                            if (!result.containsKey(entry.getKey())) {
                                result.put(entry.getKey(), entry.getValue());
                            } else {
                                result.put(entry.getKey(), Math.min(entry.getValue(), result.get(entry.getKey())));
                            }
                        }
                ));
        return result;
    }

    private <U> U traversePath(PathTile startTile, Function<PathTile, U> init, Update<U, PathTile, PathTile> update) {
        final List<PathTile> queue = new LinkedList<>();
        queue.add(startTile);

        final Set<PathTile> visited = new HashSet<>();
        visited.add(startTile);

        final U result = init.apply(startTile);

        while (!queue.isEmpty()) {
            final PathTile currentTile = queue.remove(0);
            for (PathTile neighbour : currentTile.getNeighbours().values()) {
                if (!visited.contains(neighbour)) {
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
}
