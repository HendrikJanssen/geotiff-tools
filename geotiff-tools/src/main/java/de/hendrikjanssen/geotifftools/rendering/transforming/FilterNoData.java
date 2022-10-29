package de.hendrikjanssen.geotifftools.rendering.transforming;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.StatefulBegin;
import de.hendrikjanssen.geotifftools.rendering.sampling.DoubleSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.FloatSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.IntegerSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;
import java.math.BigDecimal;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FilterNoData implements SampleTransform, StatefulBegin {

    private final double epsilon;

    public FilterNoData(double epsilon) {
        this.epsilon = epsilon;
    }

    public FilterNoData() {
        this(0.00001d);
    }

    @Nullable
    private BigDecimal noDataValue;

    @Override
    @Nullable
    public Sample transform(GeoTiff geoTiff, @Nullable Sample sample) {
        if (noDataValue == null) {
            return sample;
        }

        if (sample instanceof DoubleSample doubleSample) {
            for (double sampleValue : doubleSample.getValues()) {
                if (Math.abs(sampleValue - noDataValue.doubleValue()) < epsilon) {
                    return null;
                }
            }
        } else if (sample instanceof IntegerSample integerSample) {
            for (int sampleValue : integerSample.getValues()) {
                if (Math.abs(sampleValue - noDataValue.intValue()) < epsilon) {
                    return null;
                }
            }
        } else if (sample instanceof FloatSample floatSample) {
            for (float sampleValue : floatSample.getValues()) {
                if (Math.abs(sampleValue - noDataValue.floatValue()) < epsilon) {
                    return null;
                }
            }
        }

        return sample;
    }

    @Override
    public void onBegin(GeoTiff geoTiff) {
        this.noDataValue = geoTiff.metadata().getNoDataValue();
    }
}
