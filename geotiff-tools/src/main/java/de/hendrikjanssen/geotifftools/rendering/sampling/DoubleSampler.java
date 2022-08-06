package de.hendrikjanssen.geotifftools.rendering.sampling;

import java.awt.Point;
import java.awt.image.Raster;

public class DoubleSampler implements Sampler<DoubleSample> {

    private final int[] bands;

    DoubleSampler(int[] bands) {
        this.bands = bands;
    }

    public static DoubleSampler ofBandIndices(int... bandIndices) {
        return new DoubleSampler(bandIndices);
    }

    @Override
    public DoubleSample sample(Point samplePoint, Raster raster) {

        double[] sampleValues = new double[bands.length];

        for (int k = 0; k < bands.length; k++) {
            sampleValues[k] = raster.getSampleDouble(samplePoint.x, samplePoint.y, bands[k]);
        }

        return new DoubleSample(samplePoint, sampleValues);
    }
}
