package com.mantkowdev.rabbitgame.api;

import com.mantkowdev.rabbitgame.Tuple;
import com.mantkowdev.rabbitgame.map.TileType;

public interface Tile {
    TileType getTileType();

    Tuple<Integer> getCoordinates();
}
