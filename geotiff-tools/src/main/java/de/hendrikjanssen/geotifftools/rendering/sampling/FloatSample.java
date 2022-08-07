package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

public final class FloatSample extends Sample {

    private final float[] values;

    public FloatSample(RasterPoint sourcePosition, float[] values) {
        super(sourcePosition);
        this.values = values;
    }

    public float[] getValues() {
        return values;
    }

    @Override
    public int getBandCount() {
        return values.length;
    }

    public float getAt(int index) {
        return values[index];
    }
}
