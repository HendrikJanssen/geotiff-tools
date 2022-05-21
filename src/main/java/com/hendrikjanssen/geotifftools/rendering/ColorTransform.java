package com.hendrikjanssen.geotifftools.rendering;

import com.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.Color;

public interface ColorTransform {

    Color transform(GeoTiff geoTiff, Sample sample);
}
