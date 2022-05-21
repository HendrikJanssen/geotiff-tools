package com.hendrikjanssen.geotifftools.rendering;

import java.awt.Point;

public class Sample {

    private final Point sourcePosition;

    public Sample(Point sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    public Point getSourcePosition() {
        return this.sourcePosition;
    }
}
