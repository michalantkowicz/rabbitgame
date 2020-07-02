package com.mantkowdev.rabbitgame.map;

import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mantkowdev.rabbitgame.map.TileType.PLAYER;
import static com.mantkowdev.rabbitgame.map.TileType.WALL;

@Value
public class GameMap {
    private final Map<TileType, List<Tile>> tiles = new HashMap<>();
    private final Path path = new Path();

    public void addTile(Tile tile) {
        tiles.computeIfAbsent(tile.getTileType(), value -> new ArrayList<>()).add(tile);
    }

    @SuppressWarnings("unchecked")
    public List<WallTile> getWalls() {
        return (List<WallTile>) (List<?>) tiles.get(WALL);
    }

    @SuppressWarnings("unchecked")
    public List<PlayerTile> getPlayers() {
        return (List<PlayerTile>) (List<?>) tiles.get(PLAYER);
    }
}
