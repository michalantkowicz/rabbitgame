package com.mantkowdev.rabbitgame.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mantkowdev.rabbitgame.Path;
import com.mantkowdev.rabbitgame.PathNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mantkowdev.rabbitgame.Tuple.of;
import static com.mantkowdev.rabbitgame.map.TileFactory.produceTile;
import static com.mantkowdev.rabbitgame.map.TileType.PATH;
import static com.mantkowdev.rabbitgame.map.TileType.PLAYER;
import static com.mantkowdev.rabbitgame.map.TileType.WALL;

@RequiredArgsConstructor
public class MapLoader {
    @NonNull
    private final String fileName;

    public GameMap load() {
        final List<Tile> tiles = loadTiles();

        return GameMap.builder()
                .path(createPath(tiles))
                .walls(filterByType(tiles, WALL))
                .players(filterByType(tiles, PLAYER))
                .build();
    }

    private Path createPath(List<Tile> tiles) {
        final Path.PathBuilder pathBuilder = Path.builder();
        tiles.stream()
                .filter(tile -> tile.getTileType() == PATH)
                .forEach(tile -> pathBuilder.pathNode(of(tile.getCoordinates().a, tile.getCoordinates().b), new PathNode()));
        return pathBuilder.build();
    }

    private List<Tile> loadTiles() {
        final List<Tile> tiles = new ArrayList<>();
        final TiledMapTileLayer mapLayer = getTiledMapLayer();

        for (int x = 0; x < mapLayer.getWidth(); x++) {
            for (int y = 0; y < mapLayer.getHeight(); y++) {
                produceTile(x, y, mapLayer.getCell(x, y)).ifPresent(tiles::add);
            }
        }
        return tiles;
    }

    @SuppressWarnings("unchecked")
    private <T extends Tile> List<T> filterByType(List<Tile> tiles, TileType tileType) {
        return (List<T>) tiles.stream().filter(tile -> tile.getTileType() == tileType).collect(Collectors.toList());
    }

    private TiledMapTileLayer getTiledMapLayer() {
        return (TiledMapTileLayer) new TmxMapLoader().load(this.fileName).getLayers().get(0);
    }
}
