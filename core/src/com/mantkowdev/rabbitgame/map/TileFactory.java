package com.mantkowdev.rabbitgame.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

import java.util.Optional;

import static com.mantkowdev.rabbitgame.map.TileType.PATH;
import static com.mantkowdev.rabbitgame.map.TileType.PLAYER;
import static com.mantkowdev.rabbitgame.map.TileType.WALL;
import static java.util.Optional.of;

public class TileFactory {
    public static Optional<Tile> produceTile(float x, float y, Cell cell) {
        final String tileType = getTileType(cell);

        if (tileType.equals(WALL.getCode())) {
            return of(new WallTile(new Vector2(x, y)));
        } else if (tileType.equals(PLAYER.getCode())) {
            return of(new PlayerTile(new Vector2(x, y)));
        } else if (tileType.equals(PATH.getCode())) {

        }
        return Optional.empty();
    }

    private static String getTileType(Cell cell) {
        return (String) cell.getTile().getProperties().get("TYPE");
    }
}
