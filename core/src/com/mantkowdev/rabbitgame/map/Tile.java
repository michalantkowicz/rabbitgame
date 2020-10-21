package com.mantkowdev.rabbitgame.map;

import com.mantkowdev.rabbitgame.Tuple;

public interface Tile {
    TileType getTileType();

    Tuple<Integer> getCoordinates();
}
