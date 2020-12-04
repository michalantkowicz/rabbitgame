package com.mantkowdev.rabbitgame.features;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mantkowdev.rabbitgame.Tuple;
import com.mantkowdev.rabbitgame.api.Feature;
import com.mantkowdev.rabbitgame.events.GameEventService;
import com.mantkowdev.rabbitgame.events.PositionEvent;
import com.mantkowdev.rabbitgame.map.GameMap;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class CollectingCoinFeature implements Feature {
    private final Map<Tuple<Integer>, Image> coins;
    private final GameMap map;
    private final GameEventService gameEventService;
    private final String topic;

    @Override
    public void act() {
        gameEventService.getEvent(PositionEvent.class, topic).ifPresent(this::handleEvent);
    }

    private void handleEvent(PositionEvent positionEvent) {
        final Tuple<Float> objectPosition = positionEvent.getEventObject();

        map.getPathTileAt(objectPosition).ifPresent(tile -> {
            if (tile.isAtCenter(objectPosition) && coins.containsKey(tile.getCoordinates())) {
                coins.get(tile.getCoordinates()).remove();
                tile.setCoin(false);
            }
        });
    }
}
