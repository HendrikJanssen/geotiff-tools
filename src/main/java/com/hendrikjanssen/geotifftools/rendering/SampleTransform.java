package com.hendrikjanssen.geotifftools.rendering;

import com.hendrikjanssen.geotifftools.GeoTiff;

public interface SampleTransform {

    Sample transform(GeoTiff geoTiff, Sample sample);
}
