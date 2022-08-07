package de.hendrikjanssen.geotifftools.rendering.coloring;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;

public interface ColorTransform {

    Color transform(GeoTiff geoTiff, @Nullable Sample sample);
}
