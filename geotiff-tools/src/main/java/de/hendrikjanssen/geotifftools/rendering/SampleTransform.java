package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;

public interface SampleTransform {

    Sample transform(GeoTiff geoTiff, Sample sample);
}
