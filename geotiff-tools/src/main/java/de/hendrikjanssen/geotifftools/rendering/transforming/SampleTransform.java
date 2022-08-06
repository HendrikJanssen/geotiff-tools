package de.hendrikjanssen.geotifftools.rendering.transforming;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;

public interface SampleTransform {

    Sample transform(GeoTiff geoTiff, Sample sample);
}
