package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

import java.awt.image.Raster;

public class FloatSampler implements Sampler<FloatSample> {

    private final int[] bands;

    FloatSampler(int[] bands) {
        this.bands = bands;
    }

    public static FloatSampler ofBandIndices(int... bandIndices) {
        return new FloatSampler(bandIndices);
    }

    @Override
    public FloatSample sample(RasterPoint samplePoint, Raster raster) {

        float[] sampleValues = new float[bands.length];

        for (int k = 0; k < bands.length; k++) {
            sampleValues[k] = raster.getSampleFloat((int) samplePoint.x(), (int) samplePoint.y(), bands[k]);
        }

        return new FloatSample(samplePoint, sampleValues);
    }
}
