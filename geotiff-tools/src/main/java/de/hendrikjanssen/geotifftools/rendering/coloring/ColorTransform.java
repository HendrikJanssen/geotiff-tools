package de.hendrikjanssen.geotifftools.rendering.coloring;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;

import java.awt.Color;

public interface ColorTransform {

    Color transform(GeoTiff geoTiff, Sample sample);
}
