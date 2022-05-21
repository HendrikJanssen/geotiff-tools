package com.hendrikjanssen.geotifftools.rendering;

import com.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class RenderTarget {

    private final Rectangle sourceRect;
    private Dimension targetDimension;

    public RenderTarget(Dimension targetDimension) {
        this(targetDimension, null);
    }

    public RenderTarget(Dimension targetDimension, Rectangle sourceRect) {
        this.targetDimension = targetDimension;
        this.sourceRect = sourceRect;
    }

    public static RenderTarget ofOriginalGeoTiffSize(GeoTiff geoTiff) {
        return new RenderTarget(new Dimension(
            geoTiff.getMetadata().getWidth(),
            geoTiff.getMetadata().getHeight()
        ));
    }

    public RenderTarget scaleUniform(double factor) {
        
        if (Double.isNaN(factor)) {
            throw new IllegalArgumentException("Factor cannot be NaN");
        }

        if (Double.isInfinite(factor)) {
            throw new IllegalArgumentException("Factor cannot be infinite");
        }

        if (factor <= 0d) {
            throw new IllegalArgumentException("Factor cannot zero or negative");
        }

        this.targetDimension = new Dimension(
            (int) (targetDimension.width * factor),
            (int) (targetDimension.height * factor)
        );

        return this;
    }

    public RenderTarget constraintToSize(int maxWidth, int maxHeight) {
        int newWidth = targetDimension.width;
        int newHeight = targetDimension.height;

        if (targetDimension.width > maxWidth) {
            newWidth = maxWidth;
            newHeight = (newWidth * targetDimension.height) / targetDimension.width;
        }

        if (newHeight > maxHeight) {
            newHeight = maxHeight;
            newWidth = (newHeight * targetDimension.width) / targetDimension.height;
        }

        this.targetDimension = new Dimension(newWidth, newHeight);

        return this;
    }

    public Rectangle getSourceRect() {
        return sourceRect;
    }

    public Dimension getTargetDimension() {
        return targetDimension;
    }

    public Point transformFromTargetToSource(Point targetPoint) {

        double normalizedX = (double) targetDimension.width / targetPoint.x;
        double normalizedY = (double) targetDimension.height / targetPoint.y;

        return new Point(
            (int) Math.ceil(normalizedX * sourceRect.getWidth()) + sourceRect.x,
            (int) Math.ceil(normalizedY * sourceRect.getHeight()) + sourceRect.y
        );
    }
}
