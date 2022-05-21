package de.hendrikjanssen.geotifftools.rendering;

import java.awt.Point;
import java.awt.image.Raster;

public class IntegerSampler implements Sampler<IntegerSample> {

    private final int[] bands;

    private final int[] sampleValueBuffer;

    IntegerSampler(int[] bands) {
        this.bands = bands;
        this.sampleValueBuffer = new int[bands.length];
    }

    public static IntegerSampler ofBandIndices(int... bandIndices) {
        return new IntegerSampler(bandIndices);
    }

    @Override
    public IntegerSample sample(Point samplePoint, Raster raster) {

        for (int k = 0; k < bands.length; k++) {
            sampleValueBuffer[k] = raster.getSample(samplePoint.x, samplePoint.y, bands[k]);
        }

        return new IntegerSample(samplePoint, sampleValueBuffer);
    }
}
