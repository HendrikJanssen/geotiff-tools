package de.hendrikjanssen.geotifftools.rendering.sampling;

import java.awt.Point;

/**
 * Base class for all samples that ensures the Sample contains the raster coordinates it was sampled from.
 */
public class Sample {

    private final Point sourcePosition;

    public Sample(Point sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    /**
     * Gets the source position in raster space this sample originates from.
     * @return The source Point.
     */
    public Point getSourcePosition() {
        return this.sourcePosition;
    }
}
