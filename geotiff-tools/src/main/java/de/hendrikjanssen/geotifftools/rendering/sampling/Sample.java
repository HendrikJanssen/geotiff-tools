package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

/**
 * Base class for all samples that ensures the Sample contains the raster coordinates it was sampled from.
 */
public sealed abstract class Sample permits IntegerSample, FloatSample, DoubleSample {

    private final RasterPoint sourcePosition;

    public Sample(RasterPoint sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    /**
     * Gets the source position in raster space this sample originates from.
     *
     * @return The source Point.
     */
    public RasterPoint getSourcePosition() {
        return this.sourcePosition;
    }

    public abstract int getBandCount();
}
