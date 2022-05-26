package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.Color;

public interface ColorTransform {

    Color transform(GeoTiff geoTiff, Sample sample);
}
