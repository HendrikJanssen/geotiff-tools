package de.hendrikjanssen.geotifftools.rendering.sampling;

import java.awt.Point;

public class IntegerSample extends Sample {

    private final int[] values;

    public IntegerSample(Point sourcePosition, int[] values) {
        super(sourcePosition);
        this.values = values;
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
}