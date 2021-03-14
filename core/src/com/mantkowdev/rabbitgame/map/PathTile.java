package com.mantkowdev.rabbitgame.map;

import com.mantkowdev.rabbitgame.Direction;
import com.mantkowdev.rabbitgame.Tuple;
import com.mantkowdev.rabbitgame.api.Tile;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Value
@ToString(exclude = "neighbours")
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class PathTile implements Tile {
    public static float WIDTH = 10f;

    Tuple<Integer> coordinates;
    @NonFinal
    @Setter
    boolean coin;
    Map<Direction, PathTile> neighbours = new HashMap<>();
    Map<Direction, PathTile> pathNeighbours = new HashMap<>();

    @Override
    public TileType getTileType() {
        return TileType.PATH;
    }

    public Tuple<Float> getCenter() {
        return Tuple.of(coordinates.a * WIDTH + WIDTH / 2f, coordinates.b * WIDTH + WIDTH / 2f);
    }

    public boolean isOnAxis(Tuple<Float> position) {
        return position.a.equals(coordinates.a * WIDTH + WIDTH / 2f) || position.b.equals(coordinates.b * WIDTH + WIDTH / 2f);
    }

    public boolean isAtCenter(Tuple<Float> position) {
        return position.a.equals(coordinates.a * WIDTH + WIDTH / 2f) && position.b.equals(coordinates.b * WIDTH + WIDTH / 2f);
    }

    public int getCoins() {
        return isCoin() ? 1 : 0;
    }

    @Override
    public String toString() {
        return "PathTile(" + coordinates.a + ", " + coordinates.b + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathTile pathTile = (PathTile) o;
        return coordinates.equals(pathTile.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates);
    }
}
