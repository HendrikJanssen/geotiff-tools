package de.hendrikjanssen.geotifftools.rendering;

import java.awt.Point;
import java.awt.image.Raster;

public interface Sampler<S extends Sample> {

    S sample(Point samplePoint, Raster raster);
}
