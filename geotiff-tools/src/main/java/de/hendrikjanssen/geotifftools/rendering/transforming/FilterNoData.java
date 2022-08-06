package de.hendrikjanssen.geotifftools.rendering.transforming;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.DoubleSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.IntegerSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;

public class FilterNoData implements SampleTransform {

    private final double epsilon;

    public FilterNoData(double epsilon) {
        this.epsilon = epsilon;
    }

    public FilterNoData() {
        this(0.00001d);
    }

    @Override
    public Sample transform(GeoTiff geoTiff, Sample sample) {
        if (geoTiff.getMetadata().getNoDataValue().isEmpty()) {
            return sample;
        }

        double noDataValue = geoTiff.getMetadata().getNoDataValue().get();

        if (sample instanceof DoubleSample) {
            DoubleSample doubleSample = (DoubleSample) sample;

            for (double sampleValue : doubleSample.getValues()) {
                if (Math.abs(sampleValue - noDataValue) < epsilon) {
                    return null;
                }
            }

            return sample;
        } else if (sample instanceof IntegerSample) {
            IntegerSample integerSample = (IntegerSample) sample;

            for (int sampleValue : integerSample.getValues()) {
                if (Math.abs(sampleValue - noDataValue) < epsilon) {
                    return null;
                }
            }

            return sample;
        }

        return sample;
    }
}
