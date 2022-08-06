package de.hendrikjanssen.geotifftools.rendering.sampling;

import java.awt.Point;

public class DoubleSample extends Sample {

    private final double[] values;

    public DoubleSample(Point sourcePosition, double[] values) {
        super(sourcePosition);
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }
}
