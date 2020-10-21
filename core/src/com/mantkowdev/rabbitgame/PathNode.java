package com.mantkowdev.rabbitgame;

import lombok.Value;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Value
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class PathNode {
}
