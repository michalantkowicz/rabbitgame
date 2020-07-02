package com.mantkowdev.rabbitgame.map;

import com.badlogic.gdx.math.Vector2;
import lombok.Value;

@Value
public class WallTile implements Tile {
    private final Vector2 position;

    @Override
    public TileType getTileType() {
        return TileType.WALL;
    }
}
