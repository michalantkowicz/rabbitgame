package com.mantkowdev.rabbitgame;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Builder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class Path {
    @Singular
    Map<Tuple<Integer>, PathNode> pathNodes;
}
