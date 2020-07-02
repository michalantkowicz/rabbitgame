package com.mantkowdev.rabbitgame.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapLoader {
    @NonNull
    private final String fileName;

    public GameMap load() {
        GameMap map = new GameMap();

        TiledMapTileLayer mapLayer = getTiledMapLayer();

        for (int x = 0; x < mapLayer.getWidth(); x++) {
            for (int y = 0; y < mapLayer.getHeight(); y++) {
                Cell cell = mapLayer.getCell(x, y);
                TileFactory.produceTile(x, y, cell).ifPresent(map::addTile);


//                if (cellType.equals("WALL")) {
//
//                } else if (cellType.equals("PLAYER")) {
//                    playerPosition.set(x * 10, y * 10);
//                }
            }
        }

        return map;
    }

    private TiledMapTileLayer getTiledMapLayer() {
        return (TiledMapTileLayer) new TmxMapLoader().load(this.fileName).getLayers().get(0);
    }
}
