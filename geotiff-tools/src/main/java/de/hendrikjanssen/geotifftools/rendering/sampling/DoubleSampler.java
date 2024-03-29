package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

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
    public DoubleSample sample(RasterPoint samplePoint, Raster raster) {

        double[] sampleValues = new double[bands.length];

        for (int k = 0; k < bands.length; k++) {
            sampleValues[k] = raster.getSampleDouble((int) samplePoint.x(), (int) samplePoint.y(), bands[k]);
        }

        return new DoubleSample(samplePoint, sampleValues);
    }
}
