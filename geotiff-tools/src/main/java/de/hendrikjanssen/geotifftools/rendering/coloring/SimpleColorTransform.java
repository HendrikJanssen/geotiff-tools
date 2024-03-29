package de.hendrikjanssen.geotifftools.rendering.coloring;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.DoubleSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.IntegerSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;

import java.awt.Color;

public class SimpleColorTransform implements ColorTransform {

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    @Override
    public Color transform(GeoTiff geoTiff, Sample sample) {
        if (sample == null) {
            return TRANSPARENT;
        }

        if (sample instanceof DoubleSample doubleSample) {
            double[] values = doubleSample.getValues();

            if (values.length == 1) {
                return new Color(
                    (float) values[0],
                    (float) values[0],
                    (float) values[0]
                );
            } else if (values.length == 3) {
                return new Color(
                    (float) values[0],
                    (float) values[1],
                    (float) values[2]
                );
            }
        }

        if (sample instanceof IntegerSample integerSample) {
            int[] values = integerSample.getValues();

            if (values.length == 1) {
                return new Color(
                    values[0],
                    values[0],
                    values[0]
                );
            } else if (values.length == 3) {
                return new Color(
                    values[0],
                    values[1],
                    values[2]
                );
            }
        }

        throw new UnsupportedOperationException("Only Samples of type DoubleSample and IntegerSample are supported.");
    }
}
