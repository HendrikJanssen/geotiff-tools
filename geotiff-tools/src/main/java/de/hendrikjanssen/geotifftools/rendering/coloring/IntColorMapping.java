package de.hendrikjanssen.geotifftools.rendering.coloring;

import java.awt.Color;

public record IntColorMapping(
    int value,
    Color color
) implements Comparable<IntColorMapping> {

    @Override
    public int compareTo(IntColorMapping o) {
        return o.value - this.value;
    }
}
