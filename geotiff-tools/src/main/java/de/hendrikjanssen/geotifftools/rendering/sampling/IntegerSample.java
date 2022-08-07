package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

import java.util.Arrays;

public final class IntegerSample extends Sample {

    private final int[] values;

    public IntegerSample(RasterPoint sourcePosition, int[] values) {
        super(sourcePosition);
        this.values = values;
    }

    @Override
    public int getBandCount() {
        return values.length;
    }

    public int[] getValues() {
        return values;
    }

    public int getAt(int index) {
        return this.values[index];
    }

    public void setAt(int index, int value) {
        this.values[index] = value;
    }

    @Override
    public String toString() {
        return "IntegerSample(sourcePosition=%s, values=%s)".formatted(
            getSourcePosition().toString(),
            Arrays.toString(values)
        );
    }
}
