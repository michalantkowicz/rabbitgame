package com.mantkowdev.rabbitgame.map;

import com.mantkowdev.rabbitgame.Tuple;
import lombok.Value;

@Value
public class WallTile implements Tile {
    private final Tuple<Integer> coordinates;

    @Override
    public TileType getTileType() {
        return TileType.WALL;
    }
}
