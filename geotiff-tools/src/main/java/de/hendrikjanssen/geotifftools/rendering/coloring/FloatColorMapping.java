package de.hendrikjanssen.geotifftools.rendering.coloring;

import java.awt.Color;

public record FloatColorMapping(
    float value,
    Color color
) implements Comparable<FloatColorMapping> {

    @Override
    public int compareTo(FloatColorMapping o) {
        return (int) (o.value - this.value);
    }
}
