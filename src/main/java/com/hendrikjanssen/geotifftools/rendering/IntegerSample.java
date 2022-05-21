package com.hendrikjanssen.geotifftools.rendering;

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
}