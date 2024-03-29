package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

public final class DoubleSample extends Sample {

    private final double[] values;

    public DoubleSample(RasterPoint sourcePosition, double[] values) {
        super(sourcePosition);
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }

    @Override
    public int getBandCount() {
        return values.length;
    }
}
