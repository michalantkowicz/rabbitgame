package com.mantkowdev.rabbitgame.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TileType {
    WALL("WALL"), PLAYER("PLAYER"), PATH("PATH"), TARGET("TARGET");

    @Getter
    private String code;
}
