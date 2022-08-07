package de.hendrikjanssen.geotifftools.rendering.coloring;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.FloatSample;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;
import java.util.Arrays;

public class FloatColorRampTransform implements ColorTransform {

    private final Color noDataColor;

    private final SamplingMode samplingMode;

    private final FloatColorMapping[] colorMappings;

    public FloatColorRampTransform(Color noDataColor, FloatColorMapping[] colorMappings, SamplingMode samplingMode) {
        this.noDataColor = noDataColor;
        this.colorMappings = colorMappings;
        Arrays.sort(this.colorMappings);
        this.samplingMode = samplingMode;
    }

    @Override
    public Color transform(GeoTiff geoTiff, @Nullable Sample sample) {
        if (sample == null) {
            return this.noDataColor;
        }

        if (!(sample instanceof FloatSample floatSample)) {
            throw new UnsupportedOperationException("Sample type must be FloatSample");
        }

        float value = floatSample.getAt(0);

        return switch (this.samplingMode) {
            case Threshold -> {
                if (colorMappings.length == 1) {
                    yield colorMappings[0].value() >= value ? colorMappings[0].color() : noDataColor;
                }

                for (int i = 1; i < colorMappings.length; i++) {
                    if (value >= colorMappings[i].value()) {
                        yield colorMappings[i - 1].color();
                    }
                }

                yield null;
            }
            case Nearest, Interpolate -> null;
        };
    }
}
