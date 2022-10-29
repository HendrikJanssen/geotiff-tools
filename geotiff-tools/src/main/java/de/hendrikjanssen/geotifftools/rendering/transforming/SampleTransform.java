package de.hendrikjanssen.geotifftools.rendering.transforming;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface SampleTransform {

    @Nullable
    Sample transform(GeoTiff geoTiff, @Nullable Sample sample);
}
