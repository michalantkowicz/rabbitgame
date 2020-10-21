package com.mantkowdev.rabbitgame.map;

import com.mantkowdev.rabbitgame.Path;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GameMap {
    private final List<WallTile> walls;
    private final List<PlayerTile> players;
    private final Path path;
}
