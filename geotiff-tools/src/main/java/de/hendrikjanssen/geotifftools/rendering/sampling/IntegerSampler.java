package de.hendrikjanssen.geotifftools.rendering.sampling;

import java.awt.Point;
import java.awt.image.Raster;

/**
 * Samples integer values from the given bands of a pixel
 */
public class IntegerSampler implements Sampler<IntegerSample> {

    private final int[] bandIndices;

    // Pre-allocated buffer for sample values to avoid creating a new array
    // for every sample operation.
    private final int[] sampleValueBuffer;

    IntegerSampler(int[] bandIndices) {
        this.bandIndices = bandIndices;
        this.sampleValueBuffer = new int[bandIndices.length];
    }

    /**
     * Constructs an IntegerSampler extracting sample values from the given bands.
     * @param bandIndices The zero-based indices of the bands that should be extracted
     * @return An IntegerSampler extracting sample values from the given bands.
     */
    public static IntegerSampler ofBandIndices(int... bandIndices) {
        return new IntegerSampler(bandIndices);
    }

    @Override
    public IntegerSample sample(Point samplePoint, Raster raster) {

        for (int i = 0; i < bandIndices.length; i++) {
            sampleValueBuffer[i] = raster.getSample(samplePoint.x, samplePoint.y, bandIndices[i]);
        }

        return new IntegerSample(samplePoint, sampleValueBuffer);
    }
}
